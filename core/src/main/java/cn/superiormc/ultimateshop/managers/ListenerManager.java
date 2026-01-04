package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.listeners.CacheListener;
import cn.superiormc.ultimateshop.listeners.SellChestListener;
import cn.superiormc.ultimateshop.listeners.SellStickListener;
import org.bukkit.Bukkit;

public class ListenerManager {

    public static ListenerManager listenerManager;

    public ListenerManager(){
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new CacheListener(), UltimateShop.instance);
        if (!UltimateShop.freeVersion) {
            Bukkit.getPluginManager().registerEvents(new SellStickListener(), UltimateShop.instance);
            Bukkit.getPluginManager().registerEvents(new SellChestListener(), UltimateShop.instance);
        }
    }
}
