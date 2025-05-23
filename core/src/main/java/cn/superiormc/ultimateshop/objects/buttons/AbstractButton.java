package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.component.ButtonComponent;

public abstract class AbstractButton {

    public ConfigurationSection config;

    public ButtonType type;

    public AbstractButton(ConfigurationSection config){
        this.config = config;
    }

    public AbstractButton(){
        // Empty...
    }

    public abstract ObjectDisplayItemStack getDisplayItem(Player player, int multi);

    //public abstract ButtonComponent getBedrockButton(Player player, int multi);

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
