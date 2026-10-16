package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.Prompt;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchActionButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchNoResultButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchResultButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchStateButton;
import cn.superiormc.ultimateshop.objects.menus.ObjectSearchMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;

final class ArcSearchGUI extends ArcCommonGUI {

    private final ObjectSearchMenu searchMenu;
    private String searchKeyword = "";

    ArcSearchGUI(Player player, ObjectSearchMenu menu, boolean bypass, ArcMenuRuntime runtime) {
        super(player, menu, bypass, runtime);
        this.searchMenu = menu;
    }

    @Override
    protected void prepareMenu() {
        super.prepareMenu();
        title = TextUtil.withPAPI(CommonUtil.parseLang(player, searchMenu.getString("title", "Search")), player);
        int size = searchMenu.getInt("size", 54);
        for (Map.Entry<Integer, ObjectSearchActionButton> entry : searchMenu.getActionButtons().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < size) {
                menuButtons.put(entry.getKey(), entry.getValue());
                menuItems.put(entry.getKey(), entry.getValue().getDisplayItem(player, 1).getItemStack());
            }
        }
        String keyword = TextUtil.normalizeText(searchKeyword);
        List<ObjectItem> matched = keyword.isEmpty() ? List.of() : ShopHelper.getTargetItems(keyword, player);
        boolean hasFilter = !keyword.isEmpty();
        String displayKeyword = searchKeyword.trim().isEmpty()
            ? LanguageManager.languageManager.getStringText(player, "plugin.search-gui-name-empty")
            : searchKeyword.trim();
        for (Map.Entry<Integer, ObjectSearchStateButton> entry : searchMenu.getStateButtons().entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < size) {
                menuButtons.put(entry.getKey(), entry.getValue());
                menuItems.put(entry.getKey(), entry.getValue().buildDisplayItem(player,
                    hasFilter, matched.size(), Math.min(matched.size(), searchMenu.getResultSlots().size()),
                    0, displayKeyword));
            }
        }
        for (int i = 0; i < Math.min(matched.size(), searchMenu.getResultSlots().size()); i++) {
            int slot = searchMenu.getResultSlots().get(i);
            ObjectSearchResultButton button = new ObjectSearchResultButton(matched.get(i), searchMenu.getResultLore());
            menuButtons.put(slot, button);
            menuItems.put(slot, button.getDisplayItem(player, 1).getItemStack());
        }
        if (matched.isEmpty() && hasFilter) {
            ObjectSearchNoResultButton button = searchMenu.getNoResultButton();
            int slot = searchMenu.getNoResultSlot();
            if (button != null && slot >= 0 && slot < size) {
                menuButtons.put(slot, button);
                menuItems.put(slot, button.buildDisplayItem(player, 0, displayKeyword));
            }
        }
    }

    @Override
    protected ObjectItem product(int slot) {
        return buttons.get(slot) instanceof ObjectSearchResultButton result ? result.getItem() : super.product(slot);
    }

    @Override
    protected boolean shouldRenderSlot(int slot) {
        return !searchMenu.getInputSlots().contains(slot);
    }

    @Override
    protected boolean isProductCard(int slot) {
        return super.isProductCard(slot) || buttons.get(slot) instanceof ObjectSearchResultButton;
    }

    @Override
    protected void dispatchClick(int slot, ClickType type) {
        ObjectSearchActionButton action = searchMenu.getActionButton(slot);
        if (action != null) {
            switch (action.getActionType()) {
                case "input-name":
                case "search-name":
                    promptName();
                    break;
                case "clear-search":
                    searchKeyword = "";
                    break;
                default:
                    break;
            }
            return;
        }
        if (buttons.get(slot) instanceof ObjectSearchResultButton result) {
            result.getItem().clickEvent(type, player);
            return;
        }
        super.dispatchClick(slot, type);
    }

    private void promptName() {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            LanguageManager.languageManager.getStringText(player, "plugin.search-gui-name-input",
                "&fType the item name you want to search in chat. Type {value} to remove the name filter.",
                "value", ConfigManager.configManager.getStringWithLang(player, "menu.search-gui.prompt.clear-keyword")),
            (p, input) -> {
                String value = input == null ? "" : input.trim();
                if (value.equalsIgnoreCase("clear")
                    || value.equalsIgnoreCase(ConfigManager.configManager.getStringWithLang(player,
                        "menu.search-gui.prompt.clear-keyword"))) {
                    value = "";
                }
                searchKeyword = value;
                openGUI(true);
            },
            p -> openGUI(true),
            false
        ));
    }
}
