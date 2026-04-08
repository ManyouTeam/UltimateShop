package cn.superiormc.ultimateshop.objects;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ItemStorage;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SearchResult {

    private final ItemStorage storage;

    private final Player player;

    private final String normalizedSearchText;

    public SearchResult(Player player, String searchText) {
        this(null, player, searchText);
    }

    public SearchResult(ItemStorage storage, Player player, String searchText) {
        this.storage = storage;
        this.player = player;
        this.normalizedSearchText = TextUtil.normalizeText(searchText);
    }

    public List<ObjectItem> getItems() {
        Set<ObjectItem> result = new LinkedHashSet<>();
        for (ObjectItem item : getCandidateItems()) {
            if (matches(item)) {
                result.add(item);
            }
        }
        return new ArrayList<>(result);
    }

    private List<ObjectItem> getCandidateItems() {
        if (storage != null) {
            if (storage.isEmpty()) {
                return new ArrayList<>();
            }
            return ShopHelper.getTargetItems(storage, player);
        }

        if (normalizedSearchText.isEmpty()) {
            return new ArrayList<>();
        }

        List<ObjectItem> result = new ArrayList<>();
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            result.addAll(shop.getProductListNotHidden(player));
        }
        return result;
    }

    private boolean matches(ObjectItem item) {
        if (normalizedSearchText.isEmpty()) {
            return true;
        }
        for (String alias : getSearchAliases(item)) {
            if (TextUtil.normalizeText(alias).contains(normalizedSearchText)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getSearchAliases(ObjectItem item) {
        Set<String> aliases = new LinkedHashSet<>();
        ItemStack displayItem = item.getDisplayItem(player);

        addSearchAlias(aliases, item.getDisplayName(player));
        addSearchAlias(aliases, ItemUtil.getItemName(displayItem));
        if (displayItem != null) {
            addSearchAlias(aliases, displayItem.getType().name());
        }
        return aliases;
    }

    private void addSearchAlias(Set<String> aliases, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        aliases.add(value);
    }
}
