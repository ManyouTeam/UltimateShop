package cn.superiormc.ultimateshop.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import cn.superiormc.ultimateshop.managers.ConfigManager;

public class InvUtil {

    public static Inventory createNewInv(Player player, int size, String... args) {
        if (ConfigManager.configManager.getBoolean("menu.use-component-title")) {
            TextComponent.Builder textComponent = Component.text().content(args[0]);
            if (args[1] != null) {
                textComponent.font(Key.key(args[1]));
            }
            return Bukkit.createInventory(player, size, textComponent.build());
        } else {
            return Bukkit.createInventory(player, size, args[0]);
        }
    }
}
