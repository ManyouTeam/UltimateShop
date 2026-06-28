package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.listeners.*;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ListenerManager {

    public static ListenerManager listenerManager;

    private final Map<UUID, InvGUI> listeners = new ConcurrentHashMap<>();

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

    public void registerNewGUIListener(Player player, InvGUI inv) {
        unregisterListeners(player);
        listeners.put(player.getUniqueId(), inv);
    }

    public void unregisterNewGUIListener(Player player, InvGUI inv) {
        listeners.remove(player.getUniqueId(), inv);
    }

    public void unregisterListeners(Player player) {
        InvGUI gui = listeners.remove(player.getUniqueId());
        if (gui != null) {
            gui.closeEventHandle(gui.getInv());
        }
    }

    public InvGUI getInvGUI(Player player) {
        return listeners.get(player.getUniqueId());
    }

    public void unregisterAllListener() {
        listeners.values().forEach(gui -> gui.closeEventHandle(gui.getInv()));
        listeners.clear();
        HandlerList.unregisterAll(UltimateShop.instance);
        if (UltimateShop.usePacketEvents && PacketInventoryUtil.packetInventoryUtil != null) {
            PacketInventoryUtil.packetInventoryUtil.shutdown();
        }
    }
}
