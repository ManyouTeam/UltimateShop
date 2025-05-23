package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.conditions.ObjectSingleCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ObjectCondition {

    private ConfigurationSection section;

    private boolean isEmpty = false;

    private ObjectShop shop = null;

    private ObjectItem item = null;

    private final List<ObjectSingleCondition> conditions = new ArrayList<>();

    public ObjectCondition() {
        this.section = new MemoryConfiguration();
    }

    public ObjectCondition(ConfigurationSection section) {
        this.section = section;
        initCondition();
    }

    public ObjectCondition(ConfigurationSection section, ObjectShop shop) {
        this.section = section;
        this.shop = shop;
        initCondition();
    }

    public ObjectCondition(ConfigurationSection section, ObjectItem item) {
        this.section = section;
        this.item = item;
        initCondition();
    }

    private void initCondition() {
        if (section == null) {
            this.isEmpty = true;
            this.section = new MemoryConfiguration();
            return;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection singleActionSection = section.getConfigurationSection(key);
            if (singleActionSection == null) {
                continue;
            }
            ObjectSingleCondition singleAction;
            if (item != null) {
                singleAction = new ObjectSingleCondition(this, singleActionSection, item);
            } else if (shop != null) {
                singleAction = new ObjectSingleCondition(this, singleActionSection, shop);
            } else  {
                singleAction = new ObjectSingleCondition(this, singleActionSection);
            }
            conditions.add(singleAction);
        }
        this.isEmpty = conditions.isEmpty();
    }

    public boolean getAllBoolean(ObjectThingRun thingRun) {
        if (thingRun.getPlayer() == null) {
            return false;
        }
        for (ObjectSingleCondition singleCondition : conditions){
            if (!singleCondition.checkBoolean(thingRun)) {
                return false;
            }
        }
        return true;
    }

    public boolean getAnyBoolean(ObjectThingRun thingRun) {
        if (thingRun.getPlayer() == null) {
            return false;
        }
        for (ObjectSingleCondition singleCondition : conditions){
            if (singleCondition.checkBoolean(thingRun)) {
                return true;
            }
        }
        return false;
    }
}
