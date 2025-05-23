package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.objects.buttons.*;
import org.bukkit.configuration.ConfigurationSection;

public class ObjectMoreMenu extends ObjectMenu {

    private final ObjectItem item;

    private int displayItemSlot = -1;

    private final ConfigurationSection section;

    public ObjectMoreMenu(ConfigurationSection section, ObjectItem item) {
        super(section.getString("menu", "buy-more"), item);
        this.item = item;
        this.type = MenuType.More;
        this.section = section;
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
        int i = 0;
        for (String singleLine : menuConfigs.getStringList("layout")) {
            for (int c = 0 ; c < singleLine.length() ; c ++) {
                char itemChar = singleLine.charAt(c);
                int slot = i;
                i ++;
                if (displayItem.equals(String.valueOf(itemChar))) {
                    displayItemSlot = slot;
                    menuItems.put(slot, new ObjectMoreDisplayButton(
                            menuConfigs.getConfigurationSection("display-item"),
                            item)
                    );
                }
            }
        }
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
            buttonItems.put(button, new ObjectMoreBuyButton(tempVal1.getConfigurationSection(button),
                    item));
        }
        int i = 0;
        for (String singleLine : menuConfigs.getStringList("layout")) {
            for (int c = 0 ; c < singleLine.length() ; c ++) {
                char itemChar = singleLine.charAt(c);
                int slot = i;
                i ++;
                if (buttonItems.get(String.valueOf(itemChar)) == null) {
                    continue;
                }
                menuItems.put(slot, buttonItems.get(String.valueOf(itemChar)));
            }
        }
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
        int i = 0;
        for (String singleLine : menuConfigs.getStringList("layout")) {
            for (int c = 0 ; c < singleLine.length() ; c ++) {
                char itemChar = singleLine.charAt(c);
                int slot = i;
                i ++;
                if (buttonItems.get(String.valueOf(itemChar)) == null) {
                    continue;
                }
                menuItems.put(slot, buttonItems.get(String.valueOf(itemChar)));
            }
        }
    }

    public int getDisplayItemSlot() {
        return displayItemSlot;
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
