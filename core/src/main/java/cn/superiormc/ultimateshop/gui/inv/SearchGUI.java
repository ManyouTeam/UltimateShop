package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.Prompt;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchActionButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchNoResultButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchResultButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchStateButton;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectSearchMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchGUI extends InvGUI {

    private final ObjectSearchMenu menu;

    private final boolean bypass;

    private final Map<Integer, ObjectSearchResultButton> resultButtons = new HashMap<>();

    private final Map<Integer, ItemStack> inputButtons = new HashMap<>();

    private String searchKeyword = "";

    private boolean suppressCloseReturn;

    private SearchGUI(Player owner, ObjectSearchMenu menu, boolean bypass) {
        super(owner);
        this.menu = menu;
        this.bypass = bypass;
    }

    @Override
    public void constructGUI() {
        if (menu == null || menu.getConfig() == null) {
            return;
        }
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player,
                    "menu-condition-not-meet",
                    "menu",
                    menu.getName());
            return;
        }

        title = TextUtil.withPAPI(CommonUtil.parseLang(player, menu.getString("title", "Search")), player);
        if (Objects.isNull(inv)) {
            inv = UltimateShop.methodUtil.createNewInv(player, menu.getInt("size", 54), title);
        }

        initInputItemMap();
        inv.clear();
        resultButtons.clear();

        menuButtons = menu.getMenu(MenuSender.of(player));
        menuItems = getMenuItems(player);
        for (Map.Entry<Integer, ItemStack> entry : menuItems.entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < inv.getSize()) {
                inv.setItem(entry.getKey(), entry.getValue());
            }
        }

        renderActionButtons();

        for (Map.Entry<Integer, ItemStack> entry : inputButtons.entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < inv.getSize()) {
                inv.setItem(entry.getKey(), entry.getValue());
            }
        }

        boolean hasSearchFilters = !inputButtons.isEmpty() || !TextUtil.normalizeText(searchKeyword).isEmpty();
        List<ObjectItem> matchedItems = getMatchedItems();

        renderStateButtons(hasSearchFilters, matchedItems.size());
        renderResultItems(matchedItems);

        if (matchedItems.isEmpty() && hasSearchFilters) {
            renderNoResultItem();
        }
    }

    private void renderActionButtons() {
        for (Map.Entry<Integer, ObjectSearchActionButton> entry : menu.getActionButtons().entrySet()) {
            if (entry.getKey() < 0 || entry.getKey() >= inv.getSize()) {
                continue;
            }
            inv.setItem(entry.getKey(), entry.getValue().getDisplayItem(player, 1).getItemStack());
        }
    }

    private void renderStateButtons(boolean hasSearchFilters, int resultAmount) {
        int showingAmount = Math.min(resultAmount, menu.getResultSlots().size());
        int inputAmount = countInputItems();
        String keywordDisplay = getSearchKeywordDisplay();

        for (Map.Entry<Integer, ObjectSearchStateButton> entry : menu.getStateButtons().entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue().buildDisplayItem(player,
                    hasSearchFilters,
                    resultAmount,
                    showingAmount,
                    inputAmount,
                    keywordDisplay));
        }
    }

    private void renderResultItems(List<ObjectItem> matchedItems) {
        List<Integer> resultSlots = menu.getResultSlots();
        int limit = Math.min(matchedItems.size(), resultSlots.size());
        for (int i = 0; i < limit; i++) {
            int slot = resultSlots.get(i);
            ObjectSearchResultButton button = new ObjectSearchResultButton(matchedItems.get(i), menu.getResultLore());
            resultButtons.put(slot, button);
            inv.setItem(slot, button.getDisplayItem(player, 1).getItemStack());
        }
    }

    private void renderNoResultItem() {
        ObjectSearchNoResultButton button = menu.getNoResultButton();
        if (button == null) {
            return;
        }
        int slot = menu.getNoResultSlot();
        if (slot < 0 || slot >= inv.getSize()) {
            return;
        }
        inv.setItem(slot, button.buildDisplayItem(player, countInputItems(), getSearchKeywordDisplay()));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (menu.getInputSlots().contains(slot)) {
            queueRefresh();
            return false;
        }

        ObjectSearchActionButton actionButton = menu.getActionButton(slot);
        if (actionButton != null) {
            handleActionButton(actionButton);
            return true;
        }

        ObjectSearchResultButton resultButton = resultButtons.get(slot);
        if (resultButton != null) {
            resultButton.getItem().clickEvent(type, player);
            queueRefresh();
            return true;
        }

        AbstractButton normalButton = menuButtons.get(slot);
        if (normalButton != null) {
            normalButton.clickEvent(type, player);
            queueRefresh();
        }
        return true;
    }

    private void handleActionButton(ObjectSearchActionButton button) {
        switch (button.getActionType()) {
            case "input-name":
            case "search-name":
                promptName();
                return;
            case "clear-search":
                returnInputItems();
                searchKeyword = "";
                constructGUI();
                return;
            default:
                return;
        }
    }

    @Override
    public boolean dragEventHandle(Map<Integer, ItemStack> newItems) {
        boolean changedInput = false;
        for (int slot : newItems.keySet()) {
            if (!menu.getInputSlots().contains(slot)) {
                return true;
            }
            changedInput = true;
        }
        if (changedInput) {
            queueRefresh();
        }
        return false;
    }

    @Override
    public boolean closeEventHandle(Inventory inventory) {
        if (!suppressCloseReturn) {
            returnInputItems();
        }
        return super.closeEventHandle(inventory);
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }

    private List<ObjectItem> getMatchedItems() {
        boolean hasItemInput = !inputButtons.isEmpty();
        String normalizedKeyword = TextUtil.normalizeText(searchKeyword);
        if (!hasItemInput && normalizedKeyword.isEmpty()) {
            return Collections.emptyList();
        }

        if (hasItemInput) {
            ItemStack[] inputItems = inputButtons.values().toArray(new ItemStack[0]);
            return ShopHelper.getTargetItems(inputItems, player, normalizedKeyword);
        }
        return ShopHelper.getTargetItems(normalizedKeyword, player);
    }

    private void returnInputItems() {
        List<ItemStack> items = new ArrayList<>();
        for (int slot : menu.getInputSlots()) {
            ItemStack item = inv == null ? null : inv.getItem(slot);
            if (item != null && !item.getType().isAir()) {
                items.add(item.clone());
                inv.setItem(slot, null);
            }
        }
        inputButtons.clear();
        if (!items.isEmpty()) {
            CommonUtil.giveOrDrop(player, items.toArray(new ItemStack[0]));
        }
    }

    private void initInputItemMap() {
        inputButtons.clear();
        for (int slot : menu.getInputSlots()) {
            ItemStack itemStack = inv.getItem(slot);
            if (itemStack != null && !itemStack.getType().isAir()) {
                inputButtons.put(slot, itemStack.clone());
            }
        }
    }

    private int countInputItems() {
        int amount = 0;
        for (ItemStack itemStack : inputButtons.values()) {
            amount += itemStack.getAmount();
        }
        return amount;
    }

    private String getSearchKeywordDisplay() {
        String normalizedKeyword = searchKeyword == null ? "" : searchKeyword.trim();
        if (normalizedKeyword.isEmpty()) {
            return LanguageManager.languageManager.getStringText(player, "plugin.search-gui-name-empty");
        }
        return normalizedKeyword;
    }

    private void promptName() {
        suppressCloseReturn = true;
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
                LanguageManager.languageManager.getStringText(player, "plugin.search-gui-name-input", "&fType the item name you want to search in chat. Type {value} to remove the name filter.", "value", ConfigManager.configManager.getStringWithLang(player, "menu.search-gui.prompt.clear-keyword")),
                (p, input) -> {
                    suppressCloseReturn = false;
                    String normalizedInput = input == null ? "" : input.trim();
                    if (normalizedInput.equalsIgnoreCase("clear")
                            || normalizedInput.equalsIgnoreCase(ConfigManager.configManager.getStringWithLang(player, "menu.search-gui.prompt.clear-keyword"))) {
                        normalizedInput = "";
                    }
                    searchKeyword = normalizedInput;
                    openGUI(true);
                },
                p -> {
                    suppressCloseReturn = false;
                    openGUI(true);
                },
                false
        ));
    }

    private void queueRefresh() {
        SchedulerUtil.runTaskLater(() -> {
            if (inv != null && player.getOpenInventory().getTopInventory().equals(inv)) {
                constructGUI();
            }
        }, 1L);
    }

    public static void openGUI(Player player) {
        openGUI(player, "search");
    }

    public static void openGUI(Player player, String menuName) {
        ObjectMenu menu = ObjectMenu.commonMenus.get(menuName);
        if (!(menu instanceof ObjectSearchMenu searchMenu) || searchMenu.getConfig() == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.menu-not-found",
                    "menu",
                    menuName);
            return;
        }
        openGUI(player, searchMenu, false, true);
    }

    public static void openGUI(Player player, ObjectSearchMenu menu, boolean bypass, boolean reopen) {
        SearchGUI gui = new SearchGUI(player, menu, bypass);
        gui.openGUI(reopen);
    }
}
