package cn.superiormc.ultimateshop.objects.ui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractButton {

    public ConfigurationSection config;

    public AbstractButton(ConfigurationSection config){
        this.config = config;
    }

    public AbstractButton(){
        // Empty...
    }

    public ItemStack getDisplayItem(Player player) {
        return null;
    }

    public void clickEvent(ClickType type, Player player) {
        return;
    }

    public ConfigurationSection getButtonConfig() {
        return config;
    }
}
