package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectLimit {

    private ConfigurationSection limitSection;

    private ConfigurationSection conditionsSection;

    private ObjectItem item;

    public ObjectLimit() {
        // Empty
    }

    public ObjectLimit(ConfigurationSection limitSection,
                       ConfigurationSection conditionsSection,
                       ObjectItem item) {
        this.limitSection = limitSection;
        this.conditionsSection = conditionsSection;
        this.item = item;
    }

    public int getPlayerLimits(Player player) {
        if (limitSection == null) {
            return -1;
        }
        if (conditionsSection == null) {
            return checkLimitValue(player, "default");
        }
        List<Integer> result = new ArrayList<>();
        for (String conditionName : limitSection.getKeys(false)) {
            if (!conditionName.equals("default") && !conditionName.equals("global") && checkLimitsCondition(conditionName, player)) {
                result.add(checkLimitValue(player, conditionName));
            }
        }
        result.add(checkLimitValue(player, "default"));
        return Collections.max(result);
    }

    public int getServerLimits(Player player) {
        if (limitSection == null) {
            return -1;
        }
        int tempVal2 = -1;
        String tempVal1 = limitSection.getString("global", "-1");
        if (!tempVal1.equals("-1")) {
            tempVal2 = checkLimitValue(player, "global");
        }
        return tempVal2;
    }

    private boolean checkLimitsCondition(String conditionName, Player player) {
        ObjectCondition tempVal1 = new ObjectCondition(conditionsSection.getConfigurationSection(conditionName));
        return tempVal1.getAllBoolean(new ObjectThingRun(player));
    }

    private int checkLimitValue(Player player, String path) {
        int tempVal2 = -1;
        String tempVal1 = limitSection.getString(path, "-1");
        if (!tempVal1.equals("-1")) {
            if (item != null && ConfigManager.configManager.getBoolean("placeholder.data.can-used-in-amount")) {
                int playerBuyTimes = 0;
                int playerSellTimes = 0;
                int serverBuyTimes = 0;
                int serverSellTimes = 0;
                ObjectUseTimesCache tempVal3 = CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
                ObjectUseTimesCache tempVal4 = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
                if (tempVal3 != null) {
                    playerBuyTimes = tempVal3.getBuyUseTimes();
                    playerSellTimes = tempVal3.getSellUseTimes();
                }
                if (tempVal4 != null) {
                    serverBuyTimes = tempVal4.getBuyUseTimes();
                    serverSellTimes = tempVal4.getSellUseTimes();
                }
                tempVal1 = CommonUtil.modifyString(tempVal1,
                        "buy-times-player",
                        String.valueOf(playerBuyTimes),
                        "sell-times-player",
                        String.valueOf(playerSellTimes),
                        "buy-times-server",
                        String.valueOf(serverBuyTimes),
                        "sell-times-server",
                        String.valueOf(serverSellTimes),
                        "last-buy-player", tempVal3 != null ? tempVal3.getBuyLastTimeName() : "",
                        "last-sell-player", tempVal3 != null ? tempVal3.getSellLastTimeName() : "",
                        "last-buy-server", tempVal4 != null ? tempVal4.getBuyLastTimeName() : "",
                        "last-sell-server", tempVal4 != null ? tempVal4.getSellLastTimeName() : "");
            }
            tempVal2 = MathUtil.doCalculate(
                    TextUtil.withPAPI(tempVal1, player)).intValue();
        }
        return tempVal2;
    }
}
