package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.managers.ActionManager;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.AbstractSingleRun;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import org.bukkit.configuration.ConfigurationSection;

public class ObjectSingleAction extends AbstractSingleRun {

    private final ObjectAction action;


    public ObjectSingleAction(ObjectAction action, ConfigurationSection actionSection) {
        super(actionSection);
        this.action = action;
    }

    public ObjectSingleAction(ObjectAction action, ConfigurationSection actionSection, ObjectItem item) {
        super(actionSection, item);
        this.action = action;
    }

    public ObjectSingleAction(ObjectAction action, ConfigurationSection actionSection, ObjectShop shop) {
        super(actionSection, shop);
        this.action = action;
    }

    public void doAction(ObjectThingRun thingRun) {
        if (startApply >= 0 && thingRun.getTimes() < startApply) {
            return;
        }
        if (endApply >= 0 && thingRun.getTimes() > startApply) {
            return;
        }
        if (!apply.isEmpty() && !apply.contains(thingRun.getTimes())) {
            return;
        }
        if (sellAllOnce && thingRun.getSellAll()) {
            return;
        }
        if (clickType != null && !clickType.equals(thingRun.getType().name())) {
            return;
        }
        if (openOnce && thingRun.isReopen()) {
            return;
        }
        ActionManager.actionManager.doAction(this, thingRun);
    }

    public boolean isMultiOnce() {
        return multiOnce;
    }

    public boolean isSellAllOnce() {
        return sellAllOnce;
    }

    public void setLastTradeStatus(ProductTradeStatus status) {
        this.action.setLastTradeStatus(status);
    }

    public ObjectAction getAction() {
        return action;
    }

}
