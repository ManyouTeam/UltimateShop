package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.listeners.CacheListener;
import org.bukkit.Bukkit;

public class ListenerManager {

    public static ListenerManager listenerManager;

    public ListenerManager(){
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new CacheListener(), UltimateShop.instance);
    }
}
