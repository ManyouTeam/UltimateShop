package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.objects.buttons.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ObjectMoreMenu extends ObjectMenu {

    private final ObjectItem item;

    private final List<Integer> displayItemSlots = new ArrayList<>();

    private final ConfigurationSection section;

    private boolean invalid;

    public ObjectMoreMenu(ConfigurationSection section, ObjectItem item) {
        super(section.getString("menu", "buy-more"), item);
        this.item = item;
        this.type = MenuType.More;
        this.section = section;
        this.invalid = true;
        initDisplayItem();
        initConfirmItem();
        initSelectAmountItem();
    }

    private void initDisplayItem() {
        if (menuConfigs == null) {
            return;
        }
        ConfigurationSection paddingItemSection = menuConfigs.getConfigurationSection("display-items-padding-item");
        List<String> displayItems = getConfiguredDisplayItems();
        if (!displayItems.isEmpty()) {
            parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
                int index = displayItems.indexOf(id);
                if (index < 0) {
                    return;
                }
                displayItemSlots.add(slot);
                getButtons().put(slot, new ObjectMoreDisplayButton(
                        null,
                        paddingItemSection,
                        item,
                        index
                ));
            });
            return;
        }

        String displayItem = menuConfigs.getString("display-item");
        if (displayItem == null) {
            return;
        }

        parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
            if (displayItem.equals(id)) {
                displayItemSlots.add(slot);
                getButtons().put(slot, new ObjectMoreDisplayButton(
                        null,
                        paddingItemSection,
                        item,
                        displayItemSlots.size() - 1
                ));
            }
        });
    }

    private List<String> getConfiguredDisplayItems() {
        Object rawItems = menuConfigs.get("display-items");
        if (!(rawItems instanceof List<?> itemList)) {
            return List.of();
        }

        Set<String> result = new LinkedHashSet<>();
        for (Object rawItem : itemList) {
            String parsedItem = parseDisplayItem(rawItem);
            if (parsedItem != null && !parsedItem.isEmpty()) {
                result.add(parsedItem);
            }
        }
        return new ArrayList<>(result);
    }

    private String parseDisplayItem(Object rawItem) {
        if (rawItem == null) {
            return null;
        }
        return String.valueOf(rawItem).trim();
    }

    private void initConfirmItem() {
        if (menuConfigs == null) {
            return;
        }
        ConfigurationSection tempVal1 = menuConfigs.getConfigurationSection("confirm-items");
        if (tempVal1 == null) {
            return;
        }

        for (String button : tempVal1.getKeys(false)) {
            ObjectMoreBuyButton buyButton = new ObjectMoreBuyButton(tempVal1.getConfigurationSection(button), item);
            buttonItems.put(button, buyButton);
            if (!buyButton.isInvalid()) {
                invalid = false;
            }
        }

        parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
            AbstractButton btn = buttonItems.get(id);
            if (btn != null) {
                getButtons().put(slot, btn);
            }
        });
    }

    private void initSelectAmountItem() {
        if (menuConfigs == null) {
            return;
        }
        ConfigurationSection tempVal1 = menuConfigs.getConfigurationSection("amount-items");
        if (tempVal1 == null) {
            return;
        }
        for (String button : tempVal1.getKeys(false)) {
            buttonItems.put(button, new ObjectMoreButton(tempVal1.getConfigurationSection(button)));
        }

        parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
            AbstractButton btn = buttonItems.get(id);
            if (btn != null) {
                getButtons().put(slot, btn);
            }
        });
    }

    public int getDisplayItemSlot() {
        if (displayItemSlots.isEmpty()) {
            return -1;
        }
        return displayItemSlots.get(0);
    }
    public ConfigurationSection getSection() {
        return section;
    }

    public boolean isInvalid() {
        return invalid;
    }
}
