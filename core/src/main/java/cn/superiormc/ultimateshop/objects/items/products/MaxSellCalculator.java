package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.PriceModifierManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.ItemStorage;
import cn.superiormc.ultimateshop.objects.items.MaxSellResult;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import cn.superiormc.ultimateshop.objects.items.ThingMode;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.items.prices.PriceType;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/* package-private */ final class MaxSellCalculator {

    private final ObjectProducts products;

    private final ItemStorage storage;

    private final Player player;

    private final int times;

    public MaxSellCalculator(ObjectProducts products, ItemStorage storage, Player player, int times) {
        this.products = products;
        this.storage = storage;
        this.player = player;
        this.times = times;
    }

    public MaxSellResult calculate(int limit, ObjectItem item, int serverTimes, ObjectPrices price,
                            boolean ignoreMultiplier, double multiplier) {
        boolean sellAll = item != null;
        if (sellAll) {
            int playerLimit = item.getPlayerSellLimit(player);
            int serverLimit = item.getServerSellLimit(player);
            int increment = item.getSellTimesIncrement();
            int configLimit = ConfigManager.configManager.getInt("sell.max-amount", 128);
            if (playerLimit >= 0) {
                limit = (int) Math.min(limit, Math.max(0L, (long) playerLimit - times) / increment);
            }
            if (serverLimit >= 0) {
                limit = (int) Math.min(limit, Math.max(0L, (long) serverLimit - serverTimes) / increment);
            }
            if (configLimit >= 0) {
                limit = Math.min(limit, configLimit);
            }
        }
        if (limit <= 0) {
            return new MaxSellResult();
        }

        boolean dynamic = (products.mode == ThingMode.ANY || products.mode == ThingMode.ALL)
                && products.singleProducts.stream().anyMatch(product -> !product.isStatic());
        boolean checkQuota = price != null && price.singlePrices.stream().anyMatch(entry ->
                entry.getSingleSection() != null && entry.getSingleSection().isConfigurationSection("require-conditions"));
        boolean dynamicPrice = price != null && (price.getMode() == ThingMode.ALL || price.getMode() == ThingMode.ANY);
        int formulaCeiling = limit;
        Map<AbstractSingleThing, Integer> dynamicCaps = sellAll && dynamicPrice
                ? ObjectCondition.withoutActions(() -> dynamicPriceCaps(price, item, serverTimes, formulaCeiling))
                : Map.of();
        if (sellAll) {
            limit = ObjectCondition.withoutActions(() -> {
                int productLimit = productFormulaLimit(formulaCeiling, item, serverTimes);
                if (!dynamicCaps.isEmpty()) {
                    for (var entry : price.getPriceType(player, times, 1).entrySet()) {
                        if (price.getMode() == ThingMode.ALL || entry.getValue() == PriceType.FIRST) {
                            productLimit = Math.min(productLimit, dynamicCaps.getOrDefault(entry.getKey(), productLimit));
                        }
                    }
                }
                return priceFormulaLimit(productLimit, item, serverTimes, price, ignoreMultiplier, multiplier);
            });
            if (limit == 0) {
                return new MaxSellResult();
            }
        }
        int smallestDynamicCap = dynamicCaps.values().stream().mapToInt(Integer::intValue).min().orElse(limit);
        if (!dynamic && !checkQuota && limit <= smallestDynamicCap) {
            return fixedPlan(limit);
        }

        int ceiling = limit;
        DynamicSellCosts costs = dynamic ? new DynamicSellCosts() : null;
        MaxSellResult best = ObjectCondition.withoutActions(() -> {
            MaxSellResult stock = dynamic ? null : fixedPlan(ceiling);
            if (!dynamic && !stock.getTakeResult().getResultBoolean()) {
                return null;
            }
            int upper = dynamic ? ceiling : stock.getMaxAmount();
            int attempts = ConfigManager.configManager.getInt("sell.max-search-attempts", 5);
            return SellAmountSearch.find(upper, Math.max(1, attempts), amount -> {
                MaxSellResult plan;
                if (dynamic) {
                    plan = dynamicPlan(amount, costs);
                } else if (amount == upper) {
                    plan = stock;
                } else {
                    plan = fixedPlan(amount);
                }
                TakeResult take = plan.getTakeResult();
                if (!take.getResultBoolean() || !take.getConditionBoolean()) {
                    return null;
                }
                if (checkQuota || amount > smallestDynamicCap) {
                    GiveResult reward = price.give(player, times, amount);
                    if (reward.empty) {
                        return null;
                    }
                    for (AbstractSingleThing entry : reward.getResultMap().keySet()) {
                        if (amount > dynamicCaps.getOrDefault(entry, amount)) {
                            return null;
                        }
                    }
                    if (!checkQuota) {
                        return plan;
                    }
                    BigDecimal total = reward.getResultMap().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                    reward.setMultiplier(ShopHelper.getFinalSellMultiplier(player, item, storage, times, amount,
                            total, ignoreMultiplier, multiplier));
                    if (!reward.checkSellPriceConditions(times, amount, player)) {
                        return null;
                    }
                }
                return plan;
            });
        });
        if (best == null) {
            return new MaxSellResult();
        }
        if (dynamic && !sellAll) {
            return dynamicPlan(best.getMaxAmount(), costs);
        }
        return best;
    }

    private int productFormulaLimit(int ceiling, ObjectItem item, int serverTimes) {
        if (products.singleProducts.stream().noneMatch(product -> {
            ConfigurationSection section = product.getSingleSection();
            return section != null && section.contains("max-sell-amount");
        })) {
            return ceiling;
        }
        boolean any = products.mode == ThingMode.ANY || products.mode == ThingMode.CLASSIC_ANY;
        int limit = any ? 0 : ceiling;
        boolean hasFormula = false;
        for (ObjectSingleProduct product : products.singleProducts) {
            ConfigurationSection section = product.getSingleSection();
            if (section == null || !product.getApplyCondition(player)) {
                continue;
            }
            String formula = section.getString("max-sell-amount");
            if (formula == null) {
                if (any) {
                    return ceiling;
                }
                continue;
            }
            formula = fillTradeCounters(formula, item, serverTimes);
            double stock = product.playerHasAmount(storage, player);
            int amount = evaluateFormula(section, formula.replace("{stock}", Double.toString(stock)), ceiling);
            hasFormula = true;
            if (any) {
                limit += Math.min(ceiling - limit, amount);
            } else {
                limit = Math.min(limit, amount);
            }
        }
        return hasFormula ? limit : ceiling;
    }

    private String fillTradeCounters(String formula, ObjectItem item, int serverTimes) {
        if (formula == null || item == null) {
            return formula;
        }
        if (!formula.contains("{buy-times-") && !formula.contains("{sell-times-")
                && !formula.contains("%buy-times-") && !formula.contains("%sell-times-")) {
            return formula;
        }
        ObjectUseTimesCache serverCache = ShopHelper.getServerUseTimesCache(item);
        return CommonUtil.modifyString(player, formula,
                "buy-times-player", Integer.toString(ShopHelper.getBuyUseTimes(item, player)),
                "buy-times-server", Integer.toString(serverCache.getBuyUseTimes()),
                "buy-times-increment", Integer.toString(item.getBuyTimesIncrement()),
                "sell-times-player", Integer.toString(times),
                "sell-times-server", Integer.toString(serverTimes),
                "sell-times-increment", Integer.toString(item.getSellTimesIncrement()));
    }

    private Map<AbstractSingleThing, Integer> dynamicPriceCaps(ObjectPrices price, ObjectItem item,
                                                               int serverTimes, int ceiling) {
        Map<AbstractSingleThing, Integer> caps = new HashMap<>();
        for (AbstractSingleThing entry : price.singlePrices) {
            ConfigurationSection section = entry.getSingleSection();
            if (section == null) {
                continue;
            }
            String formula = section.getString("max-sell-amount");
            if (formula == null || formula.contains("{sell-unit-income}") || !entry.getApplyCondition(player)) {
                continue;
            }
            int cap = evaluateFormula(section, fillTradeCounters(formula, item, serverTimes), ceiling);
            if (cap < ceiling) {
                caps.put(entry, cap);
            }
        }
        return caps;
    }

    private int priceFormulaLimit(int ceiling, ObjectItem item, int serverTimes, ObjectPrices price,
                                  boolean ignoreMultiplier, double multiplier) {
        if (ceiling == 0 || price == null || (price.getMode() != ThingMode.CLASSIC_ANY
                && price.getMode() != ThingMode.CLASSIC_ALL) || price.singlePrices.stream().noneMatch(entry ->
                entry.getSingleSection() != null && entry.getSingleSection().contains("max-sell-amount"))) {
            return ceiling;
        }
        if (!ignoreMultiplier && item.isPriceModifierEnabled()
                && !PriceModifierManager.priceModifierManager.getSellPriceModifiers().getModifiers().isEmpty()) {
            return ceiling;
        }

        GiveResult unit = price.give(player, times, 1);
        BigDecimal base = unit.getOriginalTotal();
        BigDecimal factor = ShopHelper.getFinalSellMultiplier(player, item, storage, times, 1, base,
                ignoreMultiplier, multiplier);
        Map<String, BigDecimal> groupIncome = new HashMap<>();
        for (Map.Entry<AbstractSingleThing, BigDecimal> entry : unit.getResultMap().entrySet()) {
            BigDecimal income = entry.getValue().multiply(factor);
            if (income.compareTo(MathUtil.applyConfiguredScale(income)) != 0) {
                return ceiling;
            }
            ConfigurationSection section = entry.getKey().getSingleSection();
            if (section == null) {
                return ceiling;
            }
            String group = section.getString("shared-price");
            if (group != null) {
                groupIncome.merge(group, income, BigDecimal::add);
            }
        }

        int limit = ceiling;
        for (Map.Entry<AbstractSingleThing, BigDecimal> entry : unit.getResultMap().entrySet()) {
            ConfigurationSection section = entry.getKey().getSingleSection();
            String formula = section.getString("max-sell-amount");
            if (formula == null) {
                continue;
            }
            String group = section.getString("shared-price");
            BigDecimal income = group == null ? entry.getValue().multiply(factor) : groupIncome.get(group);
            if (income.signum() > 0) {
                limit = evaluateFormula(section, fillTradeCounters(formula, item, serverTimes)
                        .replace("{sell-unit-income}", income.toPlainString()), limit);
            }
        }
        return limit;
    }

    private int evaluateFormula(ConfigurationSection section, String formula, int ceiling) {
        ConfigurationSection conditions = section.getConfigurationSection("require-conditions");
        if (conditions != null) {
            for (String key : conditions.getKeys(false)) {
                String value = conditions.getString(key + ".value");
                if (value != null) {
                    formula = formula.replace("{require-conditions." + key + ".value}", "(" + value + ")");
                }
            }
        }
        return MathUtil.tryCalculate(TextUtil.withPAPI(formula, player), 16)
                .map(value -> value.max(BigDecimal.ZERO).min(BigDecimal.valueOf(ceiling))
                        .setScale(0, RoundingMode.FLOOR).intValueExact())
                .orElse(ceiling);
    }

    private MaxSellResult dynamicPlan(int amount, DynamicSellCosts costs) {
        MaxSellResult result = new MaxSellResult();
        if (amount <= 0) {
            return result;
        }
        ItemStorage remainingStock = ItemStorage.of(storage.getStorageContents());
        boolean any = products.mode == ThingMode.ANY;
        int remaining = amount;
        for (ObjectSingleProduct product : products.singleProducts) {
            if (!product.getApplyCondition(player)) {
                continue;
            }
            int target = any ? remaining : amount;
            int offset = any ? amount - remaining : 0;
            BigDecimal stock = BigDecimal.valueOf(product.playerHasAmount(remainingStock, player));
            DynamicSellCosts.Match match = costs.match(product, player, offset, target, stock);
            int matched = match.count();
            BigDecimal needed = match.cost();
            if (!any && matched != target) {
                return result;
            }
            if (matched == 0) {
                continue;
            }
            boolean reserveItems = switch (product.type) {
                case VANILLA_ITEM, HOOK_ITEM, MATCH_ITEM -> true;
                default -> false;
            };
            if (!product.playerHasEnough(remainingStock, player, reserveItems, needed.doubleValue())) {
                return result;
            }
            result.getTakeResult().addResultMapElement(product, player, times, matched, needed);
            if (any) {
                remaining -= matched;
                if (remaining == 0) {
                    break;
                }
            }
        }
        if (!result.getTakeResult().empty && (products.mode == ThingMode.ALL || remaining == 0)) {
            result.getTakeResult().setResultBoolean();
            result.setMaxAmount(amount);
        }
        return result;
    }

    private MaxSellResult fixedPlan(int maxLimit) {
        int maxAmount = -1;
        MaxSellResult sellResult = new MaxSellResult();
        switch (products.mode) {
            case UNKNOWN:
                return MaxSellResult.empty;
            case ANY:
            case CLASSIC_ANY:
                boolean breakThisTime = false;
                boolean needTrue = true;
                for (ObjectSingleProduct tempVal1 : products.singleProducts) {
                    if (!tempVal1.getApplyCondition(player)) {
                        continue;
                    }
                    BigDecimal cost = products.getAmount(player, times, 1, false).get(tempVal1);
                    int tempVal2 = (int) (tempVal1.playerHasAmount(storage, player) / cost.doubleValue());
                    if (tempVal2 > 0) {
                        if (maxAmount < 0) {
                            maxAmount = 0;
                        }
                        if (tempVal2 >= maxLimit - maxAmount) {
                            tempVal2 = maxLimit - maxAmount;
                            maxAmount = maxLimit;
                            breakThisTime = true;
                        } else {
                            maxAmount = tempVal2 + maxAmount;
                        }
                        BigDecimal realCost = products.getAmount(player, times, tempVal2, false).get(tempVal1);
                        if (tempVal1.playerHasEnough(storage, player, false, realCost.doubleValue())) {
                            sellResult.getTakeResult().addResultMapElement(tempVal1, player, times, tempVal2, realCost);
                        } else {
                            needTrue = false;
                        }
                        if (breakThisTime) {
                            break;
                        }
                    }
                }
                if (needTrue && maxAmount > 0) {
                    sellResult.getTakeResult().setResultBoolean();
                }
                sellResult.setMaxAmount(maxAmount);
                return sellResult;
            case ALL:
            case CLASSIC_ALL:
                boolean needFalse = false;
                for (ObjectSingleProduct tempVal1 : products.singleProducts) {
                    if (!tempVal1.getApplyCondition(player)) {
                        continue;
                    }
                    BigDecimal cost = products.getAmount(player, times, 1, false).get(tempVal1);
                    int tempVal2 = (int) (tempVal1.playerHasAmount(storage, player) / cost.doubleValue());
                    if (maxAmount == -1 || tempVal2 < maxAmount) {
                        maxAmount = tempVal2;
                    }
                }
                if (maxAmount >= maxLimit) {
                    maxAmount = maxLimit;
                }
                if (maxAmount > 0) {
                    for (ObjectSingleProduct tempVal1 : products.singleProducts) {
                        if (!tempVal1.getApplyCondition(player)) {
                            continue;
                        }
                        BigDecimal realCost = products.getAmount(player, times, maxAmount, false).get(tempVal1);
                        sellResult.getTakeResult().addResultMapElement(tempVal1, player, times, maxAmount, realCost);

                        if (!tempVal1.playerHasEnough(storage, player, false, realCost.doubleValue())) {
                            needFalse = true;
                        }
                    }

                    if (!needFalse) {
                        sellResult.getTakeResult().setResultBoolean();
                    }
                }
                sellResult.setMaxAmount(maxAmount);
                return sellResult;
            default:
                ErrorManager.errorManager.sendErrorMessage("§cError: Can not get price-mode section in your shop config!!");
                return MaxSellResult.empty;
        }
    }
}
