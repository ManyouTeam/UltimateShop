package cn.superiormc.ultimateshop.objects;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.managers.MinecraftResourceManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ItemStorage;
import cn.superiormc.ultimateshop.objects.menus.ObjectSearchMenu;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SearchResult {

    private final ItemStorage storage;

    private final Player player;

    private final String normalizedSearchText;

    private final List<JSONObject> searchLanguages = new ArrayList<>();

    public SearchResult(Player player, String searchText) {
        this(null, player, searchText);
    }

    public SearchResult(ItemStorage storage, Player player, String searchText) {
        this.storage = storage;
        this.player = player;
        this.normalizedSearchText = TextUtil.normalizeText(searchText);
    }

    public List<ObjectItem> getItems() {
        prepareSearchLanguages();
        Set<ObjectItem> result = new LinkedHashSet<>();
        for (ObjectItem item : getCandidateItems()) {
            if (matches(item)) {
                result.add(item);
            }
        }
        return new ArrayList<>(result);
    }

    private Set<String> getSearchLanguages() {
        Set<String> languages = new LinkedHashSet<>();
        if (player != null) {
            languages.add(player.getLocale());
        }
        if (LanguageManager.languageManager != null) {
            languages.add(LanguageManager.languageManager.getPlayerLanguage(player));
            languages.add(LanguageManager.languageManager.getPlayerLanguage(null));
        } else {
            languages.add(ConfigManager.configManager.getString("config-files.language", "en_US"));
        }
        return languages;
    }

    private void prepareSearchLanguages() {
        searchLanguages.clear();
        MinecraftResourceManager resources = MinecraftResourceManager.minecraftResourceManager;
        if (normalizedSearchText.isEmpty() || resources == null) {
            return;
        }
        List<CompletableFuture<?>> loading = new ArrayList<>();
        for (String language : getSearchLanguages()) {
            CompletableFuture<JSONObject> request = resources.requestLanguage(language);
            if (!request.isDone()) {
                loading.add(request);
            } else {
                JSONObject translations = SchedulerUtil.getAvailableResult(request);
                if (translations != null && !searchLanguages.contains(translations)) {
                    searchLanguages.add(translations);
                }
            }
        }
        if (player == null || loading.isEmpty()) {
            return;
        }
        CompletableFuture.allOf(loading.toArray(new CompletableFuture<?>[0])).whenComplete((ignored, exception) ->
            SchedulerUtil.runSync(player, () -> {
                if (!player.isOnline() || MenuStatusManager.menuStatusManager == null) {
                    return;
                }
                AbstractGUI gui = MenuStatusManager.menuStatusManager.getOpeningGUI(player);
                if (gui != null && gui.getMenu() instanceof ObjectSearchMenu) {
                    gui.updateGUI();
                }
            }));
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
            if (!searchLanguages.isEmpty()) {
                String key = UltimateShop.methodUtil.getItemTranslateKey(displayItem);
                for (JSONObject language : searchLanguages) {
                    addSearchAlias(aliases, language.optString(key, null));
                }
            }
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
