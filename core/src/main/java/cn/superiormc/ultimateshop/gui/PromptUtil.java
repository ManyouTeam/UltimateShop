package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.entity.Player;

public class PromptUtil {

    public static String getCancelKeyword(Player player) {
        String keyword = ConfigManager.configManager.getStringWithLang(player, "menu.prompt.cancel-keyword", "cancel");
        if (keyword == null) {
            return "cancel";
        }
        keyword = keyword.trim();
        if (keyword.isEmpty()) {
            return "cancel";
        }
        return keyword;
    }

    public static boolean matchesCancel(Player player, String input) {
        if (input == null) {
            return false;
        }
        if (input.trim().equalsIgnoreCase("cancel")) {
            return true;
        }
        return input.trim().equalsIgnoreCase(getCancelKeyword(player));
    }
}
