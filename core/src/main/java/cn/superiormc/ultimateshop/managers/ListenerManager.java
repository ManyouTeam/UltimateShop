package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.listeners.EditorChatListener;
import cn.superiormc.ultimateshop.listeners.CacheListener;
import cn.superiormc.ultimateshop.listeners.GUIListener;
import cn.superiormc.ultimateshop.listeners.SellChestListener;
import cn.superiormc.ultimateshop.listeners.SellStickListener;
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
        Bukkit.getPluginManager().registerEvents(new EditorChatListener(), UltimateShop.instance);
        Bukkit.getPluginManager().registerEvents(new GUIListener(), UltimateShop.instance);
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
    }
}
