package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectMoreBuyButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectMoreDisplayButton;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ObjectMoreMenu extends ObjectMenu {

    public static Map<ObjectItem, ObjectMoreMenu> moreMenus = new HashMap<>();

    private ObjectItem item;

    private int displayItemSlot = -1;


    public ObjectMoreMenu(String fileName, ObjectItem item) {
        super(fileName);
        this.item = item;
        this.type = MenuType.More;
        moreMenus.put(item, this);
        initDisplayItem();
        initConfirmItem();
        initSelectAmountItem();
    }

    private void initDisplayItem() {
        if (menuConfigs == null) {
            return;
        }
        String displayItem = menuConfigs.getString("display-item");
        int i = 0;
        for (String singleLine : menuConfigs.getStringList("layout")) {
            for (int c = 0 ; c < singleLine.length() ; c ++) {
                char itemChar = singleLine.charAt(c);
                int slot = i;
                i ++;
                if (displayItem.equals(String.valueOf(itemChar))) {
                    displayItemSlot = i;
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

    public int getDisplayItemSlot() {
        return displayItemSlot;
    }
}
