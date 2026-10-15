package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.gui.MenuUILayout;
import cn.superiormc.ultimateshop.gui.ProductUIModel;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.DialogPackManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
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
import cn.superiormc.ultimateshop.utils.ItemUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogOreGUI extends DialogGUI {

    private final ObjectMenu menu;

    private final ObjectShop shop;

    private final boolean bypass;

    private int page;

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
            if (dialog == null) {
                dialog =
                    DialogView.builder(menu.getString("title", ""))
                    .body("Invalid UI configuration: " + menu.getName())
                    .action(
                    DialogAction.of(
                    "ui_error_close",
                    ConfigManager.configManager.getMenuRendererText(
                        getMenu() == null ? null : getMenu().getConfig(),
                        player, "dialog", "default-button", ""),
                    response -> closeGUI()))
                    .build();
            }
        } catch (IllegalArgumentException exception) {
            ErrorManager.errorManager.sendErrorMessage(
                "§cInvalid Ore Dialog in " + menu.getName() + ": " + exception.getMessage());
            dialog =
                DialogView.builder(menu.getString("title", ""))
                .body("Invalid Dialog layout: " + menu.getName())
                .action(
                DialogAction.of(
                "ore_close",
                ConfigManager.configManager.getMenuRendererText(
                    getMenu() == null ? null : getMenu().getConfig(),
                    player, "dialog", "default-button", ""),
                response -> closeGUI()))
                .build();
        }
    }

    private boolean canDisplayMenu() {
        if (CacheManager.cacheManager.getObjectCache(player) == null) {
            LanguageManager.languageManager.sendStringText(
                player, "error.player-not-found", "player", player.getName());
            return false;
        }
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(
                player, "menu-condition-not-meet", "menu", menu.getName());
            return false;
        }
        return true;
    }

    private void constructOreGUI() {
        MenuUILayout layout = new MenuUILayout(menu.getConfig());
        if (!layout.isValid()) {
            return;
        }
        ObjectDialogScreen screen = new ObjectDialogScreen(layout.getWidth(), layout.getHeight());
        String title = menu.getString("title", shop == null ? "" : shop.getShopDisplayName());
        if (shop != null) {
            title =
                title.replace("{shop-name}", shop.getShopDisplayName())
                .replace("{shop-id}", shop.getShopName());
        }
        DialogView.Builder builder =
            DialogView.builder(CommonUtil.parseLang(player, title))
            .layout("ore")
            .keepOpenAfterAction(true);
        Map<Integer, AbstractButton> buttons = menu.getUiMenu(MenuSender.of(player, "ore"));
        List<MenuUILayout.Entry> entries = new ArrayList<>();
        for (var entry : buttons.entrySet()) {
            AbstractButton button = entry.getValue();
            if (button.isVisibleInMenu("ore") && button.canDisplay(MenuSender.of(player, "ore"))) {
                entries.add(
                    new MenuUILayout.Entry(
                    entry.getKey(),
                    button.getButtonConfig(),
                    button instanceof ObjectItem));
            }
        }
        List<MenuUILayout.Element> elements = layout.arrange(entries);
        if (!layout.isValid()) {
            return;
        }
        int pages =
            Math.max(
            1,
            elements.stream().mapToInt(MenuUILayout.Element::page).max().orElse(0) + 1);
        page = Math.min(page, pages - 1);
        ProductUIModel controls = new ProductUIModel(menu, player);
        for (MenuUILayout.Element element : elements) {
            if (element.page() >= 0 && element.page() != page) {
                continue;
            }
            AbstractButton button = buttons.get(element.slot());
            ObjectDisplayItemStack display = button.getDisplayItem(player, 1);
            String id = "ui_slot_" + element.slot();
            String name = ItemUtil.getItemName(display.getItemStack());
            List<String> lore =
                display.getMeta() == null
            ? List.of()
            : UltimateShop.methodUtil.getItemLore(display.getMeta());
            if (lore == null) {
                lore = List.of();
            }
            List<ObjectDialogWidget.ToolbarAction> toolbar = new ArrayList<>();
            boolean product = button instanceof ObjectItem;
            if (button instanceof ObjectItem item) {
                ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
                ObjectUseTimesCache uses = cache.getUseTimesCache(item);
                if (uses != null) {
                    uses.refreshTimes();
                }
                lore = controls.getLore(item, "ore");
                for (ProductUIModel.Action action : controls.getPrimaryActions(item)) {
                    String actionId = id + "_" + action.id();
                    toolbar.add(new ObjectDialogWidget.ToolbarAction(actionId, action.label()));
                    builder.action(
                        DialogAction.of(
                        actionId,
                        action.label(),
                        response -> {
                            if (canDisplayMenu()
                                && item.isVisibleInMenu("ore")
                                && item.canDisplay(MenuSender.of(player, "ore"))) {
                                action.execute(item, player);
                                updateGUI();
                            }
                        }));
                }
                toolbar.add(new ObjectDialogWidget.ToolbarAction(id, ConfigManager.configManager.getMenuRendererText(
                    getMenu() == null ? null : getMenu().getConfig(),
                    player, "dialog", "details", "")));
            }
            ObjectDialogTemplate template =
                ObjectDialogTemplate.fromUi(
                menu.getConfig(), element, button.getButtonConfig());
            if (!template.isValid()) {
                return;
            }
            screen.addWidget(
                new ObjectDialogWidget(
                template,
                element.bounds().x(),
                element.bounds().y(),
                name,
                lore,
                id,
                product ? ConfigManager.configManager.getMenuRendererText(
                    getMenu() == null ? null : getMenu().getConfig(),
                    player, "dialog", "details", "") : "",
                product,
                DialogPackManager.showsItemIcon()
                && element.appearance().equals("card")
                ? display.getSprite()
                : null,
                false,
                product
                ? controls.getHoverLore(
                (ObjectItem) button, "ore-hover")
                : lore)
                .withShowSprite(button.getShowSprite())
                .withToolbar(toolbar));
            builder.action(
                DialogAction.of(
                id,
                name,
                response -> {
                    if (!canDisplayMenu()
                        || !button.isVisibleInMenu("ore")
                        || !button.canDisplay(MenuSender.of(player, "ore"))) {
                        return;
                    }
                    closeGUI();
                    if (button instanceof ObjectItem item) {
                        new DialogInfoGUI(
                            player,
                            item,
                            null,
                            () -> openGUI(true),
                            controls.getExtraActions(item))
                            .openGUI(true);
                    } else {
                        button.clickEvent(ClickType.LEFT, player);
                    }
                }));
        }
        addHeader(layout, screen, title);
        if (pages > 1) {
            for (MenuUILayout.Element element : layout.getPaginationElements()) {
                String key = element.area().substring(11);
                String text =
                    key.equals("indicator")
                ? (page + 1) + " / " + pages
                : key.equals("previous") ? "<" : ">";
                String id = key.equals("indicator") ? "" : element.area();
                screen.addWidget(
                    new ObjectDialogWidget(
                    ObjectDialogTemplate.fromUi(menu.getConfig(), element),
                    element.bounds().x(),
                    element.bounds().y(),
                    text,
                    List.of(),
                    id,
                    "",
                    false));
                if (!id.isEmpty()) {
                    builder.action(
                        DialogAction.of(
                        id,
                        text,
                        response -> {
                            page =
                                Math.max(
                                0,
                                Math.min(
                                pages - 1,
                                page
                                + (key.equals("previous")
                                ? -1
                                : 1)));
                            updateGUI();
                        }));
                }
            }
        }
        if (screen.isValid()) {
            screen.compactTop();
            dialog = builder.screen(screen).build();
        }
    }

    private void addHeader(MenuUILayout layout, ObjectDialogScreen screen, String title) {
        var header = layout.getUi().getConfigurationSection("layout.header");
        if (header == null) {
            return;
        }
        int index = -1;
        for (String key : header.getKeys(false)) {
            if (key.equals("title")) {
                continue;
            }
            var settings = header.getConfigurationSection(key);
            if (settings == null || !settings.getBoolean("enabled", true)) {
                continue;
            }
            String text =
                settings.getString("text", "")
                .replace("{title}", title)
                .replace("{content}", menu.getString("content", ""));
            if (text.isBlank()) {
                continue;
            }
            int y = settings.getInt("y");
            MenuUILayout.Element element =
                new MenuUILayout.Element(
                index--,
                "header:" + key,
                "button",
                new MenuUILayout.Box(
                settings.getInt("x"), y, settings.getInt("width", 108), 27),
                -1);
            ObjectDialogTemplate template = ObjectDialogTemplate.fromUi(menu.getConfig(), element);
            if (!template.isValid()) {
                return;
            }
            screen.addWidget(
                new ObjectDialogWidget(
                template, element.bounds().x(), y, text, List.of(), "", "", false));
        }
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
