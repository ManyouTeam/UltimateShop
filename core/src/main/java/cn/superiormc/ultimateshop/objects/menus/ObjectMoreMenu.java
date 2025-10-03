package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.objects.buttons.*;
import org.bukkit.configuration.ConfigurationSection;

public class ObjectMoreMenu extends ObjectMenu {

    private final ObjectItem item;

    private int displayItemSlot = -1;

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
        String displayItem = menuConfigs.getString("display-item");
        if (displayItem == null) {
            return;
        }

        parseLayout(menuConfigs.getStringList("layout"), (slot, id) -> {
            if (displayItem.equals(id)) {
                displayItemSlot = slot;
                menuItems.put(slot, new ObjectMoreDisplayButton(
                        menuConfigs.getConfigurationSection("display-item"),
                        item
                ));
            }
        });
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
                menuItems.put(slot, btn);
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
                menuItems.put(slot, btn);
            }
        });
    }

    public int getDisplayItemSlot() {
        return displayItemSlot;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public boolean isInvalid() {
        return invalid;
    }
}
