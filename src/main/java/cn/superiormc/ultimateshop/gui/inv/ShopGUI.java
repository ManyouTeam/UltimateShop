package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.form.FormShopGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.PaperUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.Objects;

public class ShopGUI extends InvGUI {

    private final ObjectShop shop;

    private final ObjectMenu shopMenu;

    private final boolean bypass;

    private ShopGUI(Player owner, ObjectShop shop, ObjectMenu shopMenu, boolean bypass) {
        super(owner);
        this.shop = shop;
        this.bypass = bypass;
        this.shopMenu = shopMenu;
    }

    @Override
    public void openGUI(boolean reopen) {
        super.openGUI(reopen);
        if (!super.canOpenGUI(reopen)) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("menu.shop.update")) {
            runTask = new BukkitRunnable() {
                @Override
                public void run() {
                    constructGUI();
                }
            };
            runTask.runTaskTimer(UltimateShop.instance, 20L, 20L);
        }
    }

    @Override
    protected void constructGUI() {
        PlayerCache tempVal1 = CacheManager.cacheManager.getPlayerCache(player.getPlayer());
        ServerCache tempVal2 = ServerCache.serverCache;
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(player.getPlayer(),
                    "error.player-not-found",
                    "player",
                    player.getName());
            return;
        }
        if (!bypass && !shopMenu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player,
                    "menu-condition-not-meet",
                    "menu",
                    shop.getShopMenu());
            return;
        }
        for (ObjectItem tempVal5 : shop.getProductList()) {
            ObjectUseTimesCache tempVal3 = tempVal1.getUseTimesCache().get(tempVal5);
            if (tempVal3 != null) {
                tempVal3.refreshBuyTimes();
                tempVal3.refreshSellTimes();
            }
            ObjectUseTimesCache tempVal4 = tempVal2.getUseTimesCache().get(tempVal5);
            if (tempVal4 != null) {
                tempVal4.refreshBuyTimes();
                tempVal4.refreshSellTimes();
            }
        }
        menuButtons = shopMenu.getMenu();
        if (ConfigManager.configManager.getBoolean("debug")) {
            for (Integer i : menuButtons.keySet()) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fMenu Buttons: " + menuButtons.get(i));
            }
        }
        menuItems = getMenuItems(player);
        if (Objects.isNull(inv)) {
            if (shop.getShopMenuObject() != null) {
                inv = PaperUtil.createNewInv(player, shop.getShopMenuObject().getInt("size", 54),
                        shop.getShopMenuObject().getString("title", shop.getShopDisplayName())
                                .replace("{shop-name}", shop.getShopDisplayName()));
            }
        }
        for (int slot : menuButtons.keySet()) {
            inv.setItem(slot, menuItems.get(slot));
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (menuButtons.get(slot) == null) {
            return true;
        }
        menuButtons.get(slot).clickEvent(type, player.getPlayer());
        if (ConfigManager.configManager.getBoolean("menu.shop.click-update")) {
            constructGUI();
        } else {
            menuItems.put(slot, getMenuItem(player, slot));
            inv.setItem(slot, menuItems.get(slot));
        }
        return true;
    }

    @Override
    public boolean closeEventHandle(Inventory inventory) {
        if (runTask != null) {
            runTask.cancel();
        }
        return super.closeEventHandle(inventory);
    }

    @Override
    public ObjectMenu getMenu() {
        return shop.getShopMenuObject();
    }

    public static void openGUI(Player player, ObjectShop shop, boolean bypass, boolean reopen) {
        if (shop == null) {
            return;
        }

        ObjectMenu shopMenu = shop.getShopMenuObject();
        if (shopMenu == null) {
            LanguageManager.languageManager.sendStringText(player.getPlayer(),
                    "error.shop-does-not-have-menu",
                    "shop",
                    shop.getShopName());
            return;
        }
        if (shopMenu.menuConfigs == null) {
            LanguageManager.languageManager.sendStringText(player.getPlayer(),
                    "error.shop-menu-not-found",
                    "shop",
                    shop.getShopName(),
                    "menu",
                    shop.getShopMenu());
            return;
        }

        if (UltimateShop.useGeyser &&
                shopMenu.isUseFloodgateHook() &&
                CommonUtil.isBedrockPlayer(player)) {
            FormShopGUI formShopGUI = new FormShopGUI(player, shop, shopMenu, bypass);
            formShopGUI.openGUI(reopen);
            return;
        }
        ShopGUI gui = new ShopGUI(player, shop, shopMenu, bypass);
        gui.openGUI(reopen);
    }

}
