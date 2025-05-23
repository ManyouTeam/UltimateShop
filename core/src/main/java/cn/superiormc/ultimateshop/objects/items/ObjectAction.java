package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.actions.ObjectSingleAction;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectAction {

    private ConfigurationSection section;

    private final List<ObjectSingleAction> everyActions = new ArrayList<>();

    private final List<ObjectSingleAction> onceActions = new ArrayList<>();

    private ObjectShop shop = null;

    private ObjectItem item = null;

    private boolean isEmpty = false;

    private ProductTradeStatus lastTradeStatus = null;

    public ObjectAction() {
        this.isEmpty = true;
    }

    public ObjectAction(ConfigurationSection section) {
        this.section = section;
        initAction();
    }

    public ObjectAction(ConfigurationSection section, ObjectShop shop) {
        this.section = section;
        this.shop = shop;
        initAction();
    }

    public ObjectAction(ConfigurationSection section, ObjectItem item) {
        this.section = section;
        this.shop = item.getShopObject();
        this.item = item;
        initAction();
    }

    private void initAction() {
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
            ObjectSingleAction singleAction;
            if (item != null) {
                singleAction = new ObjectSingleAction(this, singleActionSection, item);
            } else if (shop != null) {
                singleAction = new ObjectSingleAction(this, singleActionSection, shop);
            } else  {
                singleAction = new ObjectSingleAction(this, singleActionSection);
            }
            if (singleAction.isMultiOnce()) {
                onceActions.add(singleAction);
            } else {
                everyActions.add(singleAction);
            }
        }
        this.isEmpty = onceActions.isEmpty() && everyActions.isEmpty();
    }

    public void runAllActions(ObjectThingRun thingRun) {
        runAllOnceActions(thingRun);
        for (int i = 0 ; i < thingRun.getMulti() ; i ++) {
            runAllEveryActions(thingRun);
        }
    }

    public void runAllEveryActions(ObjectThingRun thingRun) {
        for (ObjectSingleAction singleAction : everyActions) {
            singleAction.doAction(thingRun);
        }
    }

    public void runAllOnceActions(ObjectThingRun thingRun) {
        for (ObjectSingleAction singleAction : onceActions) {
            singleAction.doAction(thingRun);
        }
    }

    public void runAnyActions(ObjectThingRun thingRun, int amount) {
        runRandomOnceActions(thingRun, amount);
        for (int i = 0 ; i < thingRun.getMulti() ; i ++) {
            runRandomEveryActions(thingRun, amount);
        }
    }

    public void runRandomOnceActions(ObjectThingRun thingRun, int x) {
        Collections.shuffle(onceActions);  // 随机打乱动作顺序
        for (int i = 0; i < Math.min(x, onceActions.size()); i++) {
            onceActions.get(i).doAction(thingRun);  // 执行 x 个随机动作
        }
    }

    public void runRandomEveryActions(ObjectThingRun thingRun, int x) {
        Collections.shuffle(everyActions);  // 随机打乱动作顺序
        for (int i = 0; i < Math.min(x, everyActions.size()); i++) {
            everyActions.get(i).doAction(thingRun);  // 执行 x 个随机动作
        }
    }

    public void setLastTradeStatus(ProductTradeStatus status) {
        this.lastTradeStatus = status;
    }

    public ProductTradeStatus getLastTradeStatus() {
        return lastTradeStatus;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
