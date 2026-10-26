package cn.superiormc.ultimateshop.api;

import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;

/**
 * Lets an addon present the menus UltimateShop would show through ArcMenu.
 * Register it with {@code ArcMenuBridge.arcMenuBridge.registerRenderer(plugin, renderer)}; menus it does not
 * handle keep UltimateShop's built-in ArcMenu renderer. All methods run on the player's thread.
 */
public interface ArcMenuRenderer {

    /**
     * Whether this renderer presents the menu for the player. Only asked for menus whose UI mode
     * already resolved to arcmenu for a Java player.
     */
    boolean handles(ObjectMenu menu, Player player);

    /**
     * Opens the menu. Returning false makes UltimateShop fall back to its inventory menu.
     *
     * @param shop    the shop of a shop menu, otherwise null
     * @param editing initial edit mode of a favourite menu
     */
    boolean open(Player player, ObjectMenu menu, @Nullable ObjectShop shop, boolean bypass, boolean reopen,
                 boolean editing);

    /**
     * Shows a product's details (the select-amount click action) while one of this renderer's menus is open.
     * Returning false lets UltimateShop open its own buy-more menu.
     */
    boolean openInfo(Player player, ObjectItem item);
}
