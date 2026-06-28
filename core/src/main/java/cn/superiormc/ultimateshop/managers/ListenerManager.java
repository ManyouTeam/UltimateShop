package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.listeners.*;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class ListenerManager {

    public static ListenerManager listenerManager;

    public ListenerManager(){
        listenerManager = this;
        registerListeners();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new GUIListener(), UltimateShop.instance);
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

    public void unregisterAllListener() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getOpenInventory().getTopInventory().getHolder() instanceof InvGUI)
                .forEach(player -> player.closeInventory());
        HandlerList.unregisterAll(UltimateShop.instance);
        if (UltimateShop.usePacketEvents && PacketInventoryUtil.packetInventoryUtil != null) {
            PacketInventoryUtil.packetInventoryUtil.shutdown();
        }
    }
}
