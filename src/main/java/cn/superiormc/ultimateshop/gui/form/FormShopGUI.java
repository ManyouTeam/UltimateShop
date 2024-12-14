package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormShopGUI extends FormGUI {

    private final ObjectShop shop;

    private final boolean bypass;

    private final ObjectMenu shopMenu;

    public FormShopGUI(Player owner, ObjectShop shop, ObjectMenu shopMenu, boolean bypass) {
        super(owner);
        this.shop = shop;
        this.bypass = bypass;
        this.shopMenu = shopMenu;
        constructGUI();
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
        if (shop.getShopMenuObject() == null) {
            LanguageManager.languageManager.sendStringText(player.getPlayer(),
                    "error.shop-does-not-have-menu",
                    "shop",
                    shop.getShopName());
            return;
        }
        if (shop.getShopMenuObject().menuConfigs == null) {
            LanguageManager.languageManager.sendStringText(player.getPlayer(),
                    "error.shop-menu-not-found",
                    "shop",
                    shop.getShopName(),
                    "menu",
                    shop.getShopMenu());
            return;
        }
        if (!bypass && !shop.getShopMenuObject().getCondition().getAllBoolean(new ObjectThingRun(player))) {
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
        menuButtons = shop.getShopMenuObject().getMenu();
        if (ConfigManager.configManager.getBoolean("debug")) {
            for (Integer i : menuButtons.keySet()) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fMenu Buttons: " + menuButtons.get(i));
            }
        }
        SimpleForm.Builder tempVal5 = SimpleForm.builder();
        Map<Integer, AbstractButton> tempVal8 = new LinkedHashMap<>();
        Map<Integer, AbstractButton> tempVal7 = new LinkedHashMap<>();
        for (int slot : menuButtons.keySet()) {
            AbstractButton button = menuButtons.get(slot);
            if (button instanceof ObjectItem) {
                tempVal8.put(slot, button);
            } else {
                tempVal7.put(slot, button);
            }
        }
        tempVal8.putAll(tempVal7);
        for (int slot : tempVal8.keySet()) {
            AbstractButton button = tempVal8.get(slot);
            ObjectDisplayItemStack displayItem = button.getDisplayItem(player, 1);
            ButtonComponent tempVal6 = displayItem.parseToBedrockButton();
            if (tempVal6 != null) {
                tempVal5.button(tempVal6);
            }
            menuItems.put(tempVal6, slot);
        }

        tempVal5.title(TextUtil.parse(player, shop.getShopMenuObject().getString("title", shop.getShopDisplayName())
                .replace("{shop-name}", shop.getShopDisplayName())));
        tempVal5.validResultHandler(response -> {
            removeOpenGUIStatus();
            menuButtons.get(menuItems.get(response.clickedButton())).clickEvent(ClickType.LEFT, player);
        });
        tempVal5.closedOrInvalidResultHandler(response -> {
            removeOpenGUIStatus();
        });
        if (getMenu().getString("bedrock.content", null) != null) {
            tempVal5.content(TextUtil.parse(player, getMenu().getString("bedrock.content", "")));
        }
        form = tempVal5.build();
    }

    @Override
    public ObjectMenu getMenu() {
        return shop.getShopMenuObject();
    }
}
