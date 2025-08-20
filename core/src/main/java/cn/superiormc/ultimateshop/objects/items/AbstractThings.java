package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class AbstractThings {

    public ThingMode mode;

    public ConfigurationSection section;

    public boolean empty;

    protected ObjectItem item;

    public AbstractThings() {
        this.mode = ThingMode.UNKNOWN;
        this.empty = true;
    }

    public AbstractThings(ConfigurationSection section, String mode, ObjectItem item) {
        initThingMode(mode);
        this.section = section;
        this.item = item;
    }

    public ThingMode getMode() {
        return mode;
    }

    public abstract GiveResult give(Player player,
                                    int times,
                                    int amount);

    public abstract TakeResult take(Inventory inventory,
                                    Player player,
                                    int times,
                                    int amount,
                                    boolean test);


    private void initThingMode(String mode) {
        switch(mode.toUpperCase()) {
            case ("ANY") :
                this.mode = ThingMode.ANY;
                break;
            case ("ALL") :
                this.mode = ThingMode.ALL;
                break;
            case ("CLASSIC") :
            case ("CLASSIC_ALL"):
                this.mode = ThingMode.CLASSIC_ALL;
                break;
            case ("CLASSIC_ANY"):
                this.mode = ThingMode.CLASSIC_ANY;
                break;
            default:
                this.mode = ThingMode.UNKNOWN;
                break;
        }
    }

    public ObjectItem getItem() {
        return item;
    }
}
