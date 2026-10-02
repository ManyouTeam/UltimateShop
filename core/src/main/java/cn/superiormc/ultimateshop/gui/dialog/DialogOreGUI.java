package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.DialogPackManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.methods.ModifyDisplayItem;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.actions.ObjectSingleAction;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.dialog.ObjectDialogScreen;
import cn.superiormc.ultimateshop.objects.dialog.ObjectDialogTemplate;
import cn.superiormc.ultimateshop.objects.dialog.ObjectDialogWidget;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.objects.dialog.DialogLayoutUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogOreGUI extends DialogGUI {

    private final ObjectMenu menu;

    private final ObjectShop shop;

    private final boolean bypass;

    public DialogOreGUI(Player player, ObjectMenu menu, ObjectShop shop, boolean bypass) {
        super(player);
        this.menu = menu;
        this.shop = shop;
        this.bypass = bypass;
    }

    @Override
    public void constructGUI() {
        dialog = null;
        if (!canDisplayMenu()) {
            return;
        }
        try {
            constructOreGUI();
        } catch (IllegalArgumentException exception) {
            ErrorManager.errorManager.sendErrorMessage("§cInvalid Ore Dialog in " + menu.getName() + ": " + exception.getMessage());
            dialog = DialogView.builder(menu.getString("title", ""))
                    .body("Invalid Dialog layout: " + menu.getName())
                    .action(DialogAction.of("ore_close", getDialogText("default-button"), response -> closeGUI())).build();
        }
    }

    private boolean canDisplayMenu() {
        if (CacheManager.cacheManager.getObjectCache(player) == null) {
            LanguageManager.languageManager.sendStringText(player, "error.player-not-found", "player", player.getName());
            return false;
        }
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player, "menu-condition-not-meet", "menu", menu.getName());
            return false;
        }
        return true;
    }

    private void constructOreGUI() {
        ObjectDialogScreen screen = new ObjectDialogScreen(menu.getInt("dialog.width", 576), menu.getInt("dialog.height", 396));
        String title = menu.getString("title", shop == null ? "" : shop.getShopDisplayName());
        if (shop != null) {
            title = title.replace("{shop-name}", shop.getShopDisplayName()).replace("{shop-id}", shop.getShopName());
        }
        DialogView.Builder builder = DialogView.builder(CommonUtil.parseLang(player, title)).layout("ore").keepOpenAfterAction(true);
        builder.buttonWidth(menu.getInt("dialog.button-width", 150));
        builder.columns(menu.getInt("dialog.columns", 2));
        String content = menu.getString("dialog.content", menu.getString("bedrock.content", ""));
        if (!content.isEmpty()) {
            builder.body(content);
        }
        List<Map.Entry<Integer, AbstractButton>> products = new ArrayList<>();
        int buttonIndex = 0;
        boolean hasClose = false;
        for (Map.Entry<Integer, AbstractButton> entry : menu.getMenu(MenuSender.of(player, "ore")).entrySet()) {
            AbstractButton button = entry.getValue();
            if (!button.canDisplay(MenuSender.of(player, "ore"))) {
                continue;
            }
            boolean visible = button.getButtonConfig() == null || button.getButtonConfig().getBoolean("dialog.enabled", true);
            if (!menu.menuConfigs.getBoolean("dialog.slots." + entry.getKey() + ".enabled", visible)) {
                continue;
            }
            if (button instanceof ObjectItem) {
                products.add(entry);
                continue;
            }
            ObjectDisplayItemStack display = button.getDisplayItem(player, 1);
            String name = ItemUtil.getItemName(display.getItemStack());
            if (ChatColor.stripColor(TextUtil.parse(name)).isBlank()) {
                continue;
            }
            boolean close = button.hasCloseAction();
            String id = "ore_slot_" + entry.getKey();
            List<String> lore = display.getMeta() == null ? null : UltimateShop.methodUtil.getItemLore(display.getMeta());
            if (!close) {
                ObjectDialogTemplate template = getTemplate(DialogLayoutUtil.getTemplateName(menu.menuConfigs, entry.getKey(), button.getButtonConfig(), "button"));
                int[] position = getPosition(entry.getKey(), button, 12,
                        DialogLayoutUtil.getButtonY(menu.menuConfigs, buttonIndex));
                screen.ensureHeight(position[1] + template.getHeight() + 9);
                screen.addWidget(new ObjectDialogWidget(template, position[0], position[1], name,
                        lore == null ? List.of() : lore, id, "", false, null, isCurrentMenuButton(button))
                        .withShowSprite(button.getShowSprite()));
            }
            builder.action(DialogAction.of(id, name, lore == null || lore.isEmpty() ? null : String.join("\n", lore), response -> {
                boolean allowed = canDisplayMenu() && button.canDisplay(MenuSender.of(player, "ore"));
                closeGUI();
                if (allowed) {
                    button.clickEvent(ClickType.LEFT, player);
                }
            }).withShowSprite(button.getShowSprite()));
            hasClose |= close;
            if (!close) {
                buttonIndex++;
            }
        }
        if (!hasClose) {
            String name = getDialogText("default-button");
            builder.action(DialogAction.of("ore_close", name, response -> closeGUI()));
        }
        addShopSidebar(screen, builder);
        List<int[]> positions = getProductPositions(screen, getTemplate(menu.getString("dialog.products.template", "product")), products.size());
        for (int i = 0; i < products.size(); i++) {
            Map.Entry<Integer, AbstractButton> entry = products.get(i);
            ObjectItem item = (ObjectItem) entry.getValue();
            int index = i;
            int[] fallback = index < positions.size() ? positions.get(index) : new int[]{-1, -1};
            int[] position = getPosition(entry.getKey(), item, fallback[0], fallback[1]);
            ObjectDialogTemplate template = getTemplate(DialogLayoutUtil.getTemplateName(menu.menuConfigs, entry.getKey(), item.getButtonConfig(), menu.getString("dialog.products.template", "product")));
            refreshProduct(item);
            String id = "ore_product_" + entry.getKey();
            String name = item.getDisplayName(player, false);
            var displayItem = item.getDisplayItemObject().getDisplayItem(player);
            screen.addWidget(new ObjectDialogWidget(template, position[0], position[1], name,
                    ModifyDisplayItem.getModifiedLore(player, 1, item, false, true, "general", "ore"), id, CommonUtil.modifyString(player,
                    ConfigManager.configManager.getString("menu.dialog.details", "{lang:menu.dialog.details}")), true,
                    DialogPackManager.showsItemIcon() ? displayItem.getSprite() : null,
                    false, ShopHelper.getProductInfoContent(player, item,
                            item.getDefaultBuyAmount(), item.getDefaultSellAmount(), "ore-hover"),
                    String.valueOf(displayItem.getItemStack().getAmount())).withShowSprite(item.getShowSprite()));
            builder.action(DialogAction.of(id, name, response -> {
                boolean allowed = canDisplayMenu() && item.getShopObject().getProductNotHidden(player, item) != null;
                closeGUI();
                if (allowed) {
                    if (ConfigManager.configManager.hasBedrockClickEvent(item, player)) {
                        item.clickEvent(ClickType.LEFT, player);
                    } else {
                        new DialogInfoGUI(player, item, null, this::updateGUI).openGUI(true);
                    }
                }
            }));
        }
        dialog = builder.screen(screen).build();
    }

    private void addShopSidebar(ObjectDialogScreen screen, DialogView.Builder builder) {
        if (shop == null || !menu.menuConfigs.getBoolean("dialog.sidebar.enabled", true)) {
            return;
        }
        ObjectMenu source = ObjectMenu.commonMenus.get(menu.getString("dialog.sidebar.menu", "main"));
        if (source == null || source == menu || !source.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            return;
        }
        int x = menu.getInt("dialog.sidebar.x", 12);
        int bottom = screen.getWidgets().stream()
                .filter(widget -> widget.getX() < menu.getInt("dialog.products.x", 144))
                .mapToInt(widget -> widget.getY() + widget.getTemplate().getHeight()).max().orElse(9);
        int y = menu.getInt("dialog.sidebar.y", bottom + 9);
        int gap = menu.getInt("dialog.sidebar.gap-y", 9);
        if (gap < 0 || gap % 9 != 0) {
            throw new IllegalArgumentException("Sidebar gap-y must be a non-negative multiple of 9");
        }
        ObjectDialogTemplate template = getTemplate(menu.getString("dialog.sidebar.template", "button"));
        if (x + template.getWidth() > menu.getInt("dialog.products.x", 144)) {
            throw new IllegalArgumentException("Sidebar must fit to the left of products.x");
        }
        MenuSender sender = MenuSender.of(player, "ore");
        for (Map.Entry<Integer, AbstractButton> entry : source.getMenuNavigationButtons(sender).entrySet()) {
            AbstractButton button = entry.getValue();
            ObjectDisplayItemStack display = button.getDisplayItem(player, 1);
            String name = ItemUtil.getItemName(display.getItemStack());
            if (ChatColor.stripColor(TextUtil.parse(name)).isBlank()) {
                continue;
            }
            String id = "ore_sidebar_" + entry.getKey();
            List<String> lore = display.getMeta() == null ? null : UltimateShop.methodUtil.getItemLore(display.getMeta());
            screen.ensureHeight(y + template.getHeight() + 9);
            screen.addWidget(new ObjectDialogWidget(template, x, y, name,
                    lore == null ? List.of() : lore, id, "", false, null, isCurrentMenuButton(button))
                    .withShowSprite(button.getShowSprite()));
            builder.action(DialogAction.of(id, name, response -> {
                boolean allowed = canDisplayMenu() && source.getCondition().getAllBoolean(new ObjectThingRun(player))
                        && button.isVisibleInMenu("ore") && button.canDisplay(sender);
                closeGUI();
                if (allowed) {
                    button.clickEvent(ClickType.LEFT, player);
                }
            }));
            y += template.getHeight() + gap;
        }
    }

    private boolean isCurrentMenuButton(AbstractButton button) {
        var currentGUI = MenuStatusManager.menuStatusManager.getOpeningGUI(player);
        if (!(currentGUI instanceof DialogOreGUI current)) {
            return false;
        }
        ObjectMenu currentMenu = MenuStatusManager.menuStatusManager.getOpeningMenu(player);
        ObjectSingleAction action = button.getAction() == null ? null : button.getAction().getMenuNavigationAction();
        if (action == null) {
            return false;
        }
        if ("shop_menu".equalsIgnoreCase(action.getString("type"))) {
            return current.shop != null && action.getString("shop") != null
                    && current.shop.getShopName().equals(action.getString("shop", player, 1));
        }
        return current.shop == null && currentMenu != null && action.getString("menu") != null
                && currentMenu.getName().equals(action.getString("menu", player, 1));
    }

    private void refreshProduct(ObjectItem item) {
        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        ObjectUseTimesCache uses = cache.getUseTimesCache(item);
        if (uses != null) {
            uses.refreshTimes();
        }
        if (CacheManager.cacheManager.serverCache != null) {
            uses = CacheManager.cacheManager.serverCache.getUseTimesCache(item);
            if (uses != null) {
                uses.refreshTimes();
            }
        }
    }

    private ObjectDialogTemplate getTemplate(String name) {
        return ObjectDialogTemplate.fromMenu(menu.menuConfigs, name);
    }

    private int[] getPosition(int slot, AbstractButton button, int x, int y) {
        return DialogLayoutUtil.getPosition(menu.menuConfigs, slot, button.getButtonConfig(), x, y);
    }

    private List<int[]> getProductPositions(ObjectDialogScreen screen, ObjectDialogTemplate template, int productCount) {
        List<int[]> positions = DialogLayoutUtil.getConfiguredProductPositions(menu.menuConfigs);
        if (positions.isEmpty() || positions.size() < productCount) {
            positions.clear();
            for (int index = 0; index < productCount; index++) {
                positions.add(DialogLayoutUtil.getGridProductPosition(menu.menuConfigs, template, index));
            }
        }
        int bottom = positions.stream().mapToInt(position -> position[1] + template.getHeight()).max().orElse(0);
        screen.ensureHeight(bottom + Math.max(18, menu.getInt("dialog.products.gap-y", 9) + 9));
        return positions;
    }
    @Override
    public void updateGUI() {
        if (!canDisplayMenu()) {
            closeGUI();
            return;
        }
        super.updateGUI();
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }
}
