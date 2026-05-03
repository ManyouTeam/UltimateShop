package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.listeners.*;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;

public class ListenerManager {

    public static ListenerManager listenerManager;

    public ListenerManager(){
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new CacheListener(), UltimateShop.instance);
        Bukkit.getPluginManager().registerEvents(new PromptListener(), UltimateShop.instance);
        if (!UltimateShop.freeVersion) {
            Bukkit.getPluginManager().registerEvents(new SellStickListener(), UltimateShop.instance);
            if (!UltimateShop.isFolia) {
                if (ConfigManager.configManager.getBoolean("sell.sell-chest.enabled")) {
                    Bukkit.getPluginManager().registerEvents(new SellChestListener(), UltimateShop.instance);
                }
            } else {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §6Warning: Sell chest feature does not support Folia.");
            }
        }
        if (CommonUtil.getMajorVersion(19) && UltimateShop.methodUtil.methodID().equals("paper") && ConfigManager.configManager.getBoolean("menu.anti-dupe-checker")) {
            Bukkit.getPluginManager().registerEvents(new DupeListener(), UltimateShop.instance);
        }
    }
}
