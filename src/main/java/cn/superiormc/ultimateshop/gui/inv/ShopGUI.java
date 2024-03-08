package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.InvUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ShopGUI extends InvGUI {

    private ObjectShop shop;

    private ObjectMenu shopMenu = null;

    public ShopGUI(Player owner, ObjectShop shop) {
        super(owner);
        this.shop = shop;
    }

    @Override
    public void openGUI() {
        super.openGUI();
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
        PlayerCache tempVal1 = CacheManager.cacheManager.getPlayerCache(owner.getPlayer());
        ServerCache tempVal2 = ServerCache.serverCache;
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(owner.getPlayer(),
                    "error.player-not-found",
                    "player",
                    owner.getPlayer().getName());
            return;
        }
        if (shop.getShopMenu() == null) {
            LanguageManager.languageManager.sendStringText(owner.getPlayer(),
                    "error.shop-does-not-have-menu",
                    "shop",
                    shop.getShopName());
            return;
        }
        shopMenu = ObjectMenu.shopMenus.get(shop);
        if (shopMenu == null) {
            LanguageManager.languageManager.sendStringText(owner.getPlayer(),
                    "error.shop-menu-not-found",
                    "shop",
                    shop.getShopName(),
                    "menu",
                    shop.getShopMenu());
            return;
        }
        if (!shopMenu.getCondition().getBoolean(owner.getPlayer())) {
            LanguageManager.languageManager.sendStringText(owner,
                    "menu-condition-not-meet",
                    "menu",
                    shop.getShopMenu());
            return;
        }
        for (ObjectItem tempVal5 : shop.getProductList()) {
            ObjectUseTimesCache tempVal3 = tempVal1.getUseTimesCache().get(tempVal5);
            if (tempVal3 != null && tempVal3.getBuyRefreshTime() != null && tempVal3.getBuyRefreshTime().isBefore(LocalDateTime.now())) {
                if (ConfigManager.configManager.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("" +
                            "§x§9§8§F§B§9§8[UltimateShop] §bReset player buy data by GUI open check!");
                }
                tempVal1.getUseTimesCache().get(tempVal5).setBuyUseTimes(0);
                tempVal1.getUseTimesCache().get(tempVal5).setLastBuyTime(null);
            }
            if (tempVal3 != null && tempVal3.getSellRefreshTime() != null && tempVal3.getSellRefreshTime().isBefore(LocalDateTime.now())) {
                if (ConfigManager.configManager.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("" +
                            "§x§9§8§F§B§9§8[UltimateShop] §bReset player sell data by GUI open check!");
                }
                tempVal1.getUseTimesCache().get(tempVal5).setSellUseTimes(0);
                tempVal1.getUseTimesCache().get(tempVal5).setLastSellTime(null);
            }
            ObjectUseTimesCache tempVal4 = tempVal2.getUseTimesCache().get(tempVal5);
            if (tempVal4 != null && tempVal4.getBuyRefreshTime() != null && tempVal4.getBuyRefreshTime().isBefore(LocalDateTime.now())) {
                if (ConfigManager.configManager.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("" +
                            "§x§9§8§F§B§9§8[UltimateShop] §bReset server buy data by GUI open check!");
                }
                tempVal2.getUseTimesCache().get(tempVal5).setBuyUseTimes(0);
                tempVal2.getUseTimesCache().get(tempVal5).setLastBuyTime(null);
            }
            if (tempVal4 != null && tempVal4.getSellRefreshTime() != null && tempVal4.getSellRefreshTime().isBefore(LocalDateTime.now())) {
                if (ConfigManager.configManager.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("" +
                            "§x§9§8§F§B§9§8[UltimateShop] §bReset server sell data by GUI open check!");
                }
                tempVal2.getUseTimesCache().get(tempVal5).setSellUseTimes(0);
                tempVal2.getUseTimesCache().get(tempVal5).setLastSellTime(null);
            }
        }
        menuButtons = shopMenu.getMenu();
        if (ConfigManager.configManager.getBoolean("debug")) {
            for (Integer i : menuButtons.keySet()) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fMenu Buttons: " + menuButtons.get(i));
            }
        }
        menuItems = getMenuItems(owner.getPlayer());
        if (Objects.isNull(inv)) {
            if (shopMenu != null) {
                inv = InvUtil.createNewInv(owner, shopMenu.getInt("size", 54),
                        shopMenu.getString("title", shop.getShopDisplayName())
                                .replace("{shop-name}", shop.getShopDisplayName()));
            }
        }
        for (int slot : menuButtons.keySet()) {
            inv.setItem(slot, menuItems.get(slot));
        }
        //setExtraSlots(glassPane);
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (menuButtons.get(slot) == null) {
            return true;
        }
        menuButtons.get(slot).clickEvent(type, owner.getPlayer());
        constructGUI();
        return true;
    }

    @Override
    public boolean closeEventHandle(Inventory inventory) {
        if (runTask != null) {
            runTask.cancel();
        }
        return true;
    }

    public Map<Integer, ItemStack> getMenuItems(Player player) {
        Map<Integer, AbstractButton> tempVal1 = menuButtons;
        Map<Integer, ItemStack> resultItems = new HashMap<>();
        for (int i : tempVal1.keySet()) {
            resultItems.put(i, tempVal1.get(i).getDisplayItem(player, 1));
        }
        return resultItems;
    }

}
