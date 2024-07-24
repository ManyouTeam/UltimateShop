package cn.superiormc.ultimateshop.objects.buttons;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractButton {

    public ConfigurationSection config;

    public ButtonType type;

    public AbstractButton(ConfigurationSection config){
        this.config = config;
    }

    public AbstractButton(){
        // Empty...
    }

    public ItemStack getDisplayItem(Player player, int multi) {
        return new ItemStack(Material.AIR);
    }

    public void clickEvent(ClickType type, Player player) {
        return;
    }

    public ConfigurationSection getButtonConfig() {
        return config;
    }

    public ButtonType getType() {
        return type;
    }

    @Override
    public String toString() {
        if (config == null) {
            return "Empty Button";
        }
        return "Button Config: " + config.getCurrentPath();
    }
}
