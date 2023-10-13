package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ObjectButton extends AbstractButton {

    private ObjectShop shop;

    private ObjectAction action;

    public ObjectButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.COMMON;
    }

    public ObjectButton(ConfigurationSection config, ObjectShop shop) {
        super(config);
        this.type = ButtonType.COMMON;
        this.shop = shop;
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        if (shop == null) {
            action  = new ObjectAction(config.getStringList("actions"));
        }
        else {
            action = new ObjectAction(config.getStringList("actions"), shop);
        }
        action.doAction(player, 1);
    }

    @Override
    public ItemStack getDisplayItem(Player player, int unUsed) {
        ConfigurationSection tempVal1 = config.getConfigurationSection("display-item");
        if (tempVal1 == null) {
            return new ItemStack(Material.BEDROCK);
        }
        return ItemUtil.buildItemStack(player, tempVal1, (int) Double.parseDouble
                (TextUtil.withPAPI(tempVal1.getString("amount", "1"), player)));
    }
}
