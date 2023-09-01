package cn.superiormc.ultimateshop.objects.items.prices;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
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

    public ObjectSinglePrice getAnyTargetPrice(Player player, int times, boolean buyOrSell) {
        List<ObjectSinglePrice> maybeResult = new ArrayList<>();
        for (ObjectSinglePrice tempVal1 : getPrices(times)) {
            if (tempVal1.getCondition(player)) {
                if (buyOrSell && tempVal1.checkHasEnough(player, false, times)) {
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
    public void giveThing(Player player, int times) {
        if (section == null || singlePrices.isEmpty()) {
            return;
        }
        switch (mode) {
            case UNKNOWN:
                return;
            case ANY:
                getAnyTargetPrice(player, times, false);
                return;
            case ALL:
                for (ObjectSinglePrice tempVal2 : getPrices(times)) {
                    tempVal2.playerGive(player, times);
                }
                return;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return;
        }
    }

    // 作为价格时候使用
    @Override
    public boolean takeThing(Player player, boolean take, int times) {
        switch (mode) {
            case UNKNOWN:
                return false;
            case ANY:
                if (section == null) {
                    return true;
                }
                if (getAnyTargetPrice(player, times, true)
                        .checkHasEnough(player, false, times)) {
                    if (take) {
                        getAnyTargetPrice(player, times, true)
                                .checkHasEnough(player, true, times);
                    }
                    return true;
                }
                return false;
            case ALL:
                for (ObjectSinglePrice tempVal1 : getPrices(times)) {
                    if (!tempVal1.checkHasEnough(player, false, times)) {
                        return false;
                    }
                }
                if (take) {
                    for (ObjectSinglePrice tempVal1 : getPrices(times)) {
                        tempVal1.checkHasEnough(player, true, times);
                    }
                }
                return true;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return false;
        }
    }

    public List<String> getDisplayName(int times, int multi) {
        Map<ObjectSinglePrice, Double> priceMaps = new HashMap<>();
        for (int i = 0 ; i < multi ; i ++) {
            for (ObjectSinglePrice tempVal3 : getPrices(times + i)) {
                if (priceMaps.containsKey(tempVal3)) {
                    priceMaps.put(tempVal3,
                            priceMaps.get(tempVal3) +
                                    tempVal3.getAmount(times + i));
                }
                else {
                    priceMaps.put(tempVal3, tempVal3.getAmount(times + i));
                }
            }
        }
        List<String> tempVal1 = new ArrayList<>();
        for (ObjectSinglePrice tempVal2 : priceMaps.keySet()) {
            tempVal1.add(tempVal2.getDisplayName(priceMaps.get(tempVal2)));
        }
        return tempVal1;
    }

    public String getDisplayNameWithOneLine(int times, String splitSign) {
        return getDisplayNameWithOneLine(times, 1);
    }

    public String getDisplayNameWithOneLine(int times, int multi) {
        List<String> tempVal1 = getDisplayName(times, multi);
        StringBuilder tempVal2 = null;
        for (String tempVal3 : tempVal1) {
            if (tempVal2 == null) {
                tempVal2 = new StringBuilder(tempVal3);
            }
            else {
                tempVal2 = new StringBuilder(tempVal2 +
                        ConfigManager.configManager.getString("placeholder.price.split-symbol") +
                        tempVal3);
            }
        }
        return tempVal2.toString();
    }

}