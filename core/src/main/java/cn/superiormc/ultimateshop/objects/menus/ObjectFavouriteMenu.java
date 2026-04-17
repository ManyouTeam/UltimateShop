package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteEditModeButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteEmptyButton;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ObjectFavouriteMenu extends ObjectMenu {

    private final List<Integer> resultSlots = new ArrayList<>();

    private final List<String> resultLore = new ArrayList<>();

    private final List<String> editingResultLore = new ArrayList<>();

    private ObjectFavouriteEditModeButton editModeButton;

    private ObjectFavouriteEmptyButton emptyButton;

    private int editModeSlot = -1;

    public ObjectFavouriteMenu(String fileName) {
        super(fileName);
        this.type = MenuType.Favourite;
        initFavouriteStructure();
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §f" + fileName + ".yml set as favourite type menu.");
    }

    private void initFavouriteStructure() {
        if (menuConfigs == null) {
            return;
        }
        initResultSlots();
        initEditModeButton();
        initEmptyButton();
        resultLore.clear();
        resultLore.addAll(menuConfigs.getStringList("result-lore"));
        editingResultLore.clear();
        editingResultLore.addAll(menuConfigs.getStringList("editing-result-lore"));
    }

    private void initResultSlots() {
        resultSlots.clear();

        Set<String> resultIds = new LinkedHashSet<>();
        String singleResultId = menuConfigs.getString("result-item");
        if (singleResultId != null && !singleResultId.isEmpty()) {
            resultIds.add(singleResultId);
        }
        resultIds.addAll(menuConfigs.getStringList("result-items"));
        if (resultIds.isEmpty()) {
            resultIds.add("6");
        }

        parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
            if (resultIds.contains(id)) {
                resultSlots.add(slot);
            }
        });
    }

    private void initEditModeButton() {
        editModeButton = null;
        editModeSlot = -1;

        ConfigurationSection section = menuConfigs.getConfigurationSection("edit-mode-item");
        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {
            ConfigurationSection buttonSection = section.getConfigurationSection(id);
            if (buttonSection == null) {
                continue;
            }
            ObjectFavouriteEditModeButton button = new ObjectFavouriteEditModeButton(buttonSection);
            parseLayout(menuConfigs.getStringList("layout"), (slot, layoutId) -> {
                if (editModeSlot == -1 && id.equals(layoutId)) {
                    editModeSlot = slot;
                    editModeButton = button;
                }
            });
            if (editModeSlot == -1) {
                int slot = buttonSection.getInt("slot", -1);
                if (slot >= 0) {
                    editModeSlot = slot;
                    editModeButton = button;
                }
            }
        }
    }

    private void initEmptyButton() {
        ConfigurationSection section = menuConfigs.getConfigurationSection("empty-item");
        if (section == null) {
            emptyButton = null;
            return;
        }
        emptyButton = new ObjectFavouriteEmptyButton(section);
    }

    public List<Integer> getResultSlots() {
        return new ArrayList<>(resultSlots);
    }

    public List<String> getResultLore() {
        return new ArrayList<>(resultLore);
    }

    public List<String> getEditingResultLore() {
        return new ArrayList<>(editingResultLore);
    }

    public ObjectFavouriteEditModeButton getEditModeButton() {
        return editModeButton;
    }

    public int getEditModeSlot() {
        return editModeSlot;
    }

    public ObjectFavouriteEmptyButton getEmptyButton() {
        return emptyButton;
    }
}
