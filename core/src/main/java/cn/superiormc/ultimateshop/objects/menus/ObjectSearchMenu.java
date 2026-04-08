package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchActionButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchNoResultButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchStateButton;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ObjectSearchMenu extends ObjectMenu {

    private final Map<Integer, ObjectSearchActionButton> actionButtons = new HashMap<>();

    private final Map<Integer, ObjectSearchStateButton> stateButtons = new HashMap<>();

    private final List<Integer> inputSlots = new ArrayList<>();

    private final List<Integer> resultSlots = new ArrayList<>();

    private final List<String> resultLore = new ArrayList<>();

    private ObjectSearchNoResultButton noResultButton;

    private int noResultSlot = -1;

    public ObjectSearchMenu(String fileName) {
        super(fileName);
        this.type = MenuType.Search;
        initSearchStructure();
        if (type == MenuType.Common) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded search menu: " + fileName + ".yml!");
        }
    }

    private void initSearchStructure() {
        if (menuConfigs == null) {
            return;
        }
        initInputAndResultSlots();
        initActionButtons();
        initStateButtons();
        initNoResultButton();
        resultLore.clear();
        resultLore.addAll(menuConfigs.getStringList("result-lore"));
    }

    private void initInputAndResultSlots() {
        inputSlots.clear();
        resultSlots.clear();

        Set<String> inputIds = new LinkedHashSet<>();
        String singleInputId = menuConfigs.getString("input-item");
        if (singleInputId != null && !singleInputId.isEmpty()) {
            inputIds.add(singleInputId);
        }
        inputIds.addAll(menuConfigs.getStringList("input-items"));
        if (inputIds.isEmpty()) {
            inputIds.add("1");
        }

        String resultId = menuConfigs.getString("result-item", "6");
        parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
            if (inputIds.contains(id)) {
                inputSlots.add(slot);
            }
            if (resultId.equals(id)) {
                resultSlots.add(slot);
            }
        });
    }

    private void initActionButtons() {
        actionButtons.clear();

        Map<String, ObjectSearchActionButton> byId = new HashMap<>();
        loadActionButtons(byId, menuConfigs.getConfigurationSection("action-items"));

        if (!byId.isEmpty()) {
            parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
                ObjectSearchActionButton button = byId.get(id);
                if (button != null) {
                    actionButtons.put(slot, button);
                }
            });
        }

        ConfigurationSection clearSection = menuConfigs.getConfigurationSection("clear-search-item");
        if (clearSection != null) {
            for (String id : clearSection.getKeys(false)) {
                ConfigurationSection buttonSection = clearSection.getConfigurationSection(id);
                if (buttonSection == null) {
                    continue;
                }
                if (!buttonSection.contains("action-type")) {
                    buttonSection.set("action-type", "clear-search");
                }
                ObjectSearchActionButton button = new ObjectSearchActionButton(buttonSection);
                parseLayout(menuConfigs.getStringList("layout"), (slot, layoutId) -> {
                    if (id.equals(layoutId)) {
                        actionButtons.put(slot, button);
                    }
                });
            }
        }
    }

    private void loadActionButtons(Map<String, ObjectSearchActionButton> byId, ConfigurationSection actionSection) {
        if (actionSection == null) {
            return;
        }
        for (String id : actionSection.getKeys(false)) {
            byId.putIfAbsent(id, new ObjectSearchActionButton(actionSection.getConfigurationSection(id)));
        }
    }

    private void initStateButtons() {
        stateButtons.clear();

        ConfigurationSection stateSection = menuConfigs.getConfigurationSection("state-items");
        if (stateSection == null) {
            return;
        }

        Map<String, ObjectSearchStateButton> byId = new HashMap<>();
        for (String id : stateSection.getKeys(false)) {
            byId.put(id, new ObjectSearchStateButton(stateSection.getConfigurationSection(id)));
        }
        parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
            ObjectSearchStateButton button = byId.get(id);
            if (button != null) {
                stateButtons.put(slot, button);
            }
        });
    }

    private void initNoResultButton() {
        ConfigurationSection section = menuConfigs.getConfigurationSection("no-result-item");
        if (section == null) {
            noResultButton = null;
            noResultSlot = -1;
            return;
        }
        noResultButton = new ObjectSearchNoResultButton(section);
        noResultSlot = section.getInt("slot", -1);
    }

    public List<Integer> getInputSlots() {
        return new ArrayList<>(inputSlots);
    }

    public List<Integer> getResultSlots() {
        return new ArrayList<>(resultSlots);
    }

    public Map<Integer, ObjectSearchActionButton> getActionButtons() {
        return new HashMap<>(actionButtons);
    }

    public ObjectSearchActionButton getActionButton(int slot) {
        return actionButtons.get(slot);
    }

    public Map<Integer, ObjectSearchStateButton> getStateButtons() {
        return new HashMap<>(stateButtons);
    }

    public List<String> getResultLore() {
        return new ArrayList<>(resultLore);
    }

    public ObjectSearchNoResultButton getNoResultButton() {
        return noResultButton;
    }

    public int getNoResultSlot() {
        return noResultSlot;
    }
}
