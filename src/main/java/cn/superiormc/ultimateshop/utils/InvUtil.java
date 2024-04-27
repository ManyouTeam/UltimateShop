package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import cn.superiormc.ultimateshop.managers.ConfigManager;

public class InvUtil {

    public static Inventory createNewInv(Player player, int size, String text) {
        if (UltimateShop.isPaper && ConfigManager.configManager.getBoolean("use-component.menu-title")) {
            return Bukkit.createInventory(player, size, MiniMessage.miniMessage().deserialize(TextUtil.withPAPI(text, player)));
        } else {
            return Bukkit.createInventory(player, size, TextUtil.parse(text, player));
        }
    }

    public static void sendMessage(Player player, String text) {
        if (UltimateShop.isPaper && ConfigManager.configManager.getBoolean("use-component.message")) {
            if (player == null) {
                Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(text));
            } else {
                player.sendMessage(MiniMessage.miniMessage().deserialize(TextUtil.withPAPI(text, player)));
            }
        } else {
            if (player == null) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.parse(text));
            } else {
                player.sendMessage(TextUtil.parse(text, player));
            }
        }
    }
}
