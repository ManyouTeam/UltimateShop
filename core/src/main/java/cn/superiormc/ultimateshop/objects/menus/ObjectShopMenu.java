package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.utils.ConfigurationUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ObjectShopMenu extends ObjectMenu {

    private final ObjectShop shop;

    private List<String> shopItemIds = List.of();

    private final Map<String, Integer> shopItemMarkerIndices = new HashMap<>();

    private final Map<Integer, Integer> sequentialShopItemIndices = new HashMap<>();

    private final List<Integer> pageProductSlots = new ArrayList<>();

    private final Map<Integer, Integer> pageButtonDirections = new HashMap<>();

    private final Map<Integer, ObjectButton> pageButtons = new HashMap<>();

    private final Map<Integer, ObjectButton> boundaryButtons = new HashMap<>();

    public ObjectShopMenu(String fileName, ObjectShop shop) {
        this(fileName, SHOP_MENU_TEMPLATE_FOLDER, shop);
    }

    public ObjectShopMenu(ObjectShop shop) {
        this(shop.getShopName(), null, shop);
    }

    private ObjectShopMenu(String fileName, String menuFolder, ObjectShop shop) {
        super(fileName, menuFolder, MenuType.Shop);
        this.shop = shop;
        initMenu();
        initButtons();
    }

    @Override
    protected void applyMenuOverrides() {
        ConfigurationSection overrideSection = shop.getConfig().getConfigurationSection("settings.menu-settings");
        if (overrideSection == null) {
            return;
        }

        if (menuConfigs == null) {
            menuConfigs = new YamlConfiguration();
        }

        ConfigurationUtil.merge(menuConfigs, overrideSection);
    }

    @Override
    public void initButtons() {
        initShopItemLayout();
        initPageLayout();
        super.initButtons();
    }

    private void initShopItemLayout() {
        shopItemIds = List.of();
        shopItemMarkerIndices.clear();
        sequentialShopItemIndices.clear();
        if (menuConfigs == null) {
            return;
        }

        for (String configuredMarker : menuConfigs.getStringList("shop-items")) {
            String marker = configuredMarker.trim();
            if (!marker.isEmpty() && !shopItemMarkerIndices.containsKey(marker)) {
                shopItemMarkerIndices.put(marker, shopItemMarkerIndices.size());
            }
        }
        if (shopItemMarkerIndices.isEmpty()) {
            return;
        }

        ConfigurationSection items = shop.getConfig().getConfigurationSection("items");
        if (items != null) {
            shopItemIds = items.getKeys(false).stream().filter(id -> shop.getProduct(id) != null).toList();
        }
        if (shopItemMarkerIndices.size() == 1) {
            String marker = shopItemMarkerIndices.keySet().iterator().next();
            parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
                for (String candidate : id.split("\\|\\|")) {
                    if (marker.equals(candidate.trim())) {
                        sequentialShopItemIndices.put(slot, sequentialShopItemIndices.size());
                        break;
                    }
                }
            });
        }
    }

    @Override
    protected AbstractButton getSingleButtonById(String id, int slot, MenuSender menuSender) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        Integer shopItemIndex = shopItemMarkerIndices.get(id);
        if (shopItemIndex != null) {
            if (shopItemMarkerIndices.size() == 1) {
                shopItemIndex = sequentialShopItemIndices.get(slot);
            }
            if (shopItemIndex == null || shopItemIndex >= shopItemIds.size()) {
                return null;
            }
            AbstractButton product = getShopProductButton(shopItemIds.get(shopItemIndex));
            if (canDisplayLayoutButton(product, menuSender)) {
                return product;
            }
            return null;
        }
        if (!UltimateShop.freeVersion) {
            AbstractButton copyItem = shop.getCopyItem(id);
            if (canDisplayLayoutButton(copyItem, menuSender)) {
                return copyItem;
            }
        }

        AbstractButton button = shop.getButton(id);
        if (canDisplayLayoutButton(button, menuSender)) {
            return button;
        }

        AbstractButton product = shop.getProduct(id);
        if (canDisplayLayoutButton(product, menuSender)) {
            return product;
        }
        return super.getSingleButtonById(id, slot, menuSender);
    }

    private AbstractButton getShopProductButton(String id) {
        AbstractButton copy = UltimateShop.freeVersion ? null : shop.getCopyItem(id);
        return copy == null ? shop.getProduct(id) : copy;
    }

    private void initPageLayout() {
        pageProductSlots.clear();
        pageButtonDirections.clear();
        pageButtons.clear();
        boundaryButtons.clear();
        if (menuConfigs == null || !menuConfigs.contains("previous-page-item") && !menuConfigs.contains("next-page-item")) {
            return;
        }
        if (shopItemMarkerIndices.size() != 1) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Shop menu " + getName()
                    + " needs one shop-items marker to use page buttons.");
            return;
        }

        String productMarker = shopItemMarkerIndices.keySet().iterator().next();
        Map<String, List<Integer>> layoutSlots = new HashMap<>();
        int size = menuConfigs.getInt("size", 54);
        parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
            if (slot < size) {
                layoutSlots.computeIfAbsent(id, key -> new ArrayList<>()).add(slot);
            }
        });
        List<Integer> productSlots = layoutSlots.get(productMarker);
        if (productSlots == null) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Shop menu " + getName()
                    + " has page buttons but no " + productMarker + " slots in its layout.");
            return;
        }
        pageProductSlots.addAll(productSlots);
        initPageButtons("previous-page-item", "hide-on-first-page", -1, layoutSlots);
        initPageButtons("next-page-item", "hide-on-last-page", 1, layoutSlots);
    }

    private void initPageButtons(String key, String hideKey, int direction, Map<String, List<Integer>> layoutSlots) {
        ConfigurationSection buttons = menuConfigs.getConfigurationSection(key);
        if (buttons == null) {
            return;
        }
        for (String marker : buttons.getKeys(false)) {
            ConfigurationSection button = buttons.getConfigurationSection(marker);
            if (button == null || !button.isConfigurationSection("display-item")) {
                ErrorManager.errorManager.sendErrorMessage("§cError: " + getName() + "." + key + "." + marker
                        + " needs a display-item section.");
                continue;
            }
            List<Integer> slots = layoutSlots.get(marker);
            if (slots == null) {
                continue;
            }
            if (shopItemMarkerIndices.containsKey(marker) || slots.stream().anyMatch(pageButtonDirections::containsKey)) {
                ErrorManager.errorManager.sendErrorMessage("§cError: " + getName() + "." + key + "." + marker
                        + " cannot share a layout marker with products or another page button.");
                continue;
            }
            ObjectButton activeButton = createPageItem(button.getConfigurationSection("display-item"));
            ObjectButton boundaryButton = button.getBoolean(hideKey, true)
                    ? createPageItem(button.getConfigurationSection("hidden-item")) : activeButton;
            for (int slot : slots) {
                pageButtonDirections.put(slot, direction);
                pageButtons.put(slot, activeButton);
                boundaryButtons.put(slot, boundaryButton);
            }
        }
    }

    private ObjectButton createPageItem(ConfigurationSection displayItem) {
        if (displayItem == null) {
            return null;
        }
        MemoryConfiguration config = new MemoryConfiguration();
        ConfigurationUtil.merge(config.createSection("display-item"), displayItem);
        return createButton(config);
    }

    public boolean hasInventoryPagination() {
        return !pageButtonDirections.isEmpty();
    }

    public int getPageDirection(int slot) {
        return pageButtonDirections.getOrDefault(slot, 0);
    }

    public List<AbstractButton> getPageProducts(MenuSender sender) {
        List<AbstractButton> products = new ArrayList<>();
        for (String id : shopItemIds) {
            AbstractButton button = getShopProductButton(id);
            if (canDisplayLayoutButton(button, sender)) {
                products.add(button);
            }
        }
        return products;
    }

    public int getPageCount(int productCount) {
        int pageSize = pageProductSlots.size();
        if (productCount == 0 || pageSize == 0) {
            return 1;
        }
        return 1 + (productCount - 1) / pageSize;
    }

    public Map<Integer, AbstractButton> getInventoryPage(MenuSender sender, List<AbstractButton> products, int page) {
        Map<Integer, AbstractButton> result = getMenu(sender);
        if (!hasInventoryPagination()) {
            return result;
        }

        int pages = getPageCount(products.size());
        int currentPage = Math.max(0, Math.min(page, pages - 1));
        int offset = currentPage * pageProductSlots.size();
        for (int index = 0; index < pageProductSlots.size(); index++) {
            int slot = pageProductSlots.get(index);
            if (offset + index < products.size()) {
                result.put(slot, products.get(offset + index));
            } else {
                result.remove(slot);
            }
        }
        for (Map.Entry<Integer, Integer> entry : pageButtonDirections.entrySet()) {
            int slot = entry.getKey();
            int direction = entry.getValue();
            boolean boundary = currentPage + direction < 0 || currentPage + direction >= pages;
            ObjectButton button = boundary ? boundaryButtons.get(slot) : pageButtons.get(slot);
            if (button == null) {
                result.remove(slot);
            } else {
                result.put(slot, button);
            }
        }
        return result;
    }

    public Set<AbstractButton> getListedButtons(MenuSender sender) {
        MenuSender effectiveSender = sender == null ? MenuSender.empty : sender;
        Map<Integer, AbstractButton> slots = getMenu(effectiveSender);
        String presentation = effectiveSender.getPresentation();
        if (!hasInventoryPagination()) {
            return new HashSet<>(slots.values());
        }
        if (presentation == null || "inventory".equals(presentation)) {
            slots.keySet().removeAll(pageProductSlots);
            slots.keySet().removeAll(pageButtonDirections.keySet());
            Set<AbstractButton> listed = new HashSet<>(slots.values());
            listed.addAll(getPageProducts(effectiveSender));
            return listed;
        }

        Set<AbstractButton> listed = new HashSet<>(slots.values());
        if ("arcmenu".equals(presentation) || "ore".equals(presentation)) {
            for (AbstractButton button : getPageProducts(effectiveSender)) {
                if (button instanceof ObjectItem item && canPlaceUiProduct(item)) {
                    listed.add(item);
                }
            }
        }
        return listed;
    }

    @Override
    protected List<AbstractButton> getUiCandidates() {
        List<AbstractButton> result = super.getUiCandidates();
        for (ObjectItem item : shop.getProductList()) {
            if (canPlaceUiProduct(item)) {
                result.add(item);
            }
        }
        return result;
    }

    private boolean canPlaceUiProduct(ObjectItem item) {
        return !isDynamicLayout() || item.getButtonConfig().contains("ui.placement");
    }

    @Override
    protected boolean canDisplayUiCandidate(AbstractButton button, MenuSender sender) {
        if (!super.canDisplayUiCandidate(button, sender)) {
            return false;
        }
        return !(button instanceof ObjectItem item) || shop.getProductNotHidden(sender, item) != null;
    }

    @Override
    protected ObjectAction createOpenAction(ConfigurationSection section) {
        return new ObjectAction(section, shop);
    }

    @Override
    protected ObjectButton createButton(ConfigurationSection section) {
        return new ObjectButton(section, shop);
    }

    @Override
    protected void collectDynamicLayoutCandidates(Set<AbstractButton> candidates) {
        super.collectDynamicLayoutCandidates(candidates);
        shop.addDynamicLayoutCandidates(candidates);
    }

    @Override
    protected boolean hasMenuVisibilitySettings() {
        return super.hasMenuVisibilitySettings() || shop.hasMenuVisibilitySettings();
    }
}
