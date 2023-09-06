package cn.superiormc.ultimateshop.objects.items.prices;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.AbstractThings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectPrices extends AbstractThings {

    public List<ObjectSinglePrice> singlePrices = new ArrayList<>();

    public ObjectPrices() {
        super();
        empty = true;
    }

    public ObjectPrices(ConfigurationSection section, String mode) {
        super(section, mode);
        initSinglePrices();
        empty = false;
    }

    public void initSinglePrices() {
        for (String s : section.getKeys(false)) {
            if (section.getConfigurationSection(s) == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get prices section in your shop config!!");
                singlePrices.add(new ObjectSinglePrice());
            }
            else {
                singlePrices.add(new ObjectSinglePrice(section.getConfigurationSection(s)));
            }
        }
    }

    public ObjectSinglePrice getAnyTargetPrice(Player player,
                                               int times,
                                               boolean buyOrSell,
                                               int amount) {
        List<ObjectSinglePrice> maybeResult = new ArrayList<>();
        for (ObjectSinglePrice tempVal1 : getPrices(times)) {
            double cost = getAmount(player, times, amount).get(tempVal1);
            if (tempVal1.getCondition(player)) {
                if (buyOrSell && tempVal1.checkHasEnough(player, false, cost)) {
                    return tempVal1;
                }
                else if (!buyOrSell) {
                    return tempVal1;
                }
                else {
                    maybeResult.add(tempVal1);
                }
            }
        }
        if (maybeResult.isEmpty()) {
            return new ObjectSinglePrice();
        }
        else {
            return maybeResult.get(0);
        }
    }

    private List<ObjectSinglePrice> getPrices(int times) {
        List<ObjectSinglePrice> applyThings = new ArrayList<>();
        for (ObjectSinglePrice tempVal1 : singlePrices) {
            if (tempVal1.getApplyCostMap().containsKey(times)) {
                applyThings.add(tempVal1);
            }
            else if (times >= tempVal1.getStartApply()) {
                applyThings.add(tempVal1);
            }
        }
        // 没有有效的价格
        if (applyThings.isEmpty()) {
            applyThings.add(new ObjectSinglePrice());
        }
        return applyThings;
    }

    @Override
    public void giveSingleThing(Player player, int times, int amount) {
        if (section == null || singlePrices.isEmpty()) {
            return;
        }
        double cost;
        switch (mode) {
            case UNKNOWN:
                return;
            case ANY:
            case CLASSIC_ANY:
                AbstractSingleThing tempVal5 = getAnyTargetPrice(player, times, false, 1);
                cost = getAmount(player, times, amount).get(tempVal5);
                tempVal5.playerGive(player, cost);
                return;
            case ALL:
            case CLASSIC_ALL:
                for (AbstractSingleThing tempVal2 : getPrices(times)) {
                    cost = getAmount(player, times, amount).get(tempVal2);
                    tempVal2.playerGive(player, cost);
                }
                return;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return;
        }
    }

    // 作为价格时候使用
    @Override
    public boolean takeSingleThing(Player player, boolean take, int times, int amount) {
        if (section == null) {
            return false;
        }
        double cost = 0;
        switch (mode) {
            case UNKNOWN:
                return false;
            case ALL:
            case CLASSIC_ALL:
                for (ObjectSinglePrice tempVal1 : getPrices(times)) {
                    cost = getAmount(player, times, amount).get(tempVal1);
                    if (!tempVal1.checkHasEnough(player, take, cost)) {
                        return false;
                    }
                }
                return true;
            case ANY:
            case CLASSIC_ANY:
                ObjectSinglePrice tempVal11 = getAnyTargetPrice(player, times, true, amount);
                cost = getAmount(player, times, amount).get(tempVal11);
                return tempVal11.checkHasEnough(player, take, cost);
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return false;
        }
    }

    @Override
    public Map<AbstractSingleThing, Double> getAmount(Player player, int times, int multi) {
        Map<AbstractSingleThing, Double> priceMaps = new HashMap<>();
        switch (mode) {
            case ALL:
            case ANY:
                for (int i = 0 ; i < multi ; i ++) {
                    for (ObjectSinglePrice tempVal3 : getPrices(times + i)) {
                        if (priceMaps.containsKey(tempVal3)) {
                            priceMaps.put(tempVal3,
                                    priceMaps.get(tempVal3) +
                                            tempVal3.getAmount(player, times + i));
                        }
                        else {
                            priceMaps.put(tempVal3, tempVal3.getAmount(player, times + i));
                        }
                    }
                }
                break;
            case CLASSIC_ALL:
            case CLASSIC_ANY:
                for (ObjectSinglePrice tempVal3 : getPrices(times)) {
                    if (priceMaps.containsKey(tempVal3)) {
                        priceMaps.put(tempVal3,
                                priceMaps.get(tempVal3) +
                                        tempVal3.getAmount(player, times) * multi);
                    }
                    else {
                        priceMaps.put(tempVal3, tempVal3.getAmount(player, times) * multi);
                    }
                }
                break;
        }
        return priceMaps;
    }

    public List<String> getDisplayName(Player player, int times, int multi) {
        Map<AbstractSingleThing, Double> priceMaps= getAmount(player, times, multi);
        List<String> tempVal1 = new ArrayList<>();
        for (AbstractSingleThing tempVal2 : priceMaps.keySet()) {
            tempVal1.add(tempVal2.getDisplayName(priceMaps.get(tempVal2)));
        }
        return tempVal1;
    }

    public String getDisplayNameWithOneLine(Player player, int times, int multi) {
        List<String> tempVal1 = getDisplayName(player, times, multi);
        StringBuilder tempVal2 = new StringBuilder();
        switch (mode) {
            case ANY: case CLASSIC_ANY:
                for (int i = 0; i < tempVal1.size(); i++) {
                    if (i > 0) {
                        tempVal2.append(ConfigManager.configManager.getString("placeholder.price.split-symbol-any"));
                    }
                    tempVal2.append(tempVal1.get(i));
                }
                break;
            case ALL: case CLASSIC_ALL:
                    for (int i = 0; i < tempVal1.size(); i++) {
                    if (i > 0) {
                        tempVal2.append(ConfigManager.configManager.getString("placeholder.price.split-symbol-all"));
                    }
                    tempVal2.append(tempVal1.get(i));
                }
                break;
            default:
                tempVal2 = new StringBuilder(ConfigManager.configManager.getString("placeholder.price.unknown-price-type"));
                break;
        }
        return tempVal2.toString();
    }

}