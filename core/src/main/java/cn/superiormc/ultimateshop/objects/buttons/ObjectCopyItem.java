package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ObjectCopyItem extends AbstractButton {

    private final ObjectItem item;

    private final ConfigurationSection section;

    private ObjectDisplayItem displayItem;

    public ObjectCopyItem(ConfigurationSection section, ObjectItem item) {
        this.type = ButtonType.SHOP;
        this.section = section;
        this.item = item;
        initDisplayItem();
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fLoaded sub button for product " + item.getProduct() + " in shop " +
                item.getShop() + "!");
    }

    private void initDisplayItem() {
        if (section.contains("display-item")) {
            displayItem = new ObjectDisplayItem(section.getConfigurationSection("display-item"),
                    section.getConfigurationSection(ConfigManager.configManager.getString("conditions.display-item-key")),
                    item);
        } else {
            displayItem = item.getDisplayItemObject();
        }
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        if (displayItem == null) {
            return item.getDisplayItem(player, multi);
        }
        return displayItem.getDisplayItem(player, multi);
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        item.clickEvent(type, player);
    }
}
