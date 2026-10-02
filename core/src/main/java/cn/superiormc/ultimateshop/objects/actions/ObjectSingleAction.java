package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.managers.ActionManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.AbstractSingleRun;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.utils.CommonUtil;
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
        if (thingRun.getPlayer() != null && bedrockOnly && !CommonUtil.isBedrockPlayer(thingRun.getPlayer())) {
            return;
        }
        if (thingRun.getPlayer() != null && javaOnly && CommonUtil.isBedrockPlayer(thingRun.getPlayer())) {
            return;
        }
        if (thingRun.getStatus() != null && failStatus != null && thingRun.getStatus() != failStatus) {
            return;
        }
        ActionManager.actionManager.doAction(this, thingRun);
    }

    public boolean isMultiOnce() {
        return multiOnce;
    }

    public int getTradeAmount(ObjectThingRun thingRun, boolean buy) {
        String configured = section.getString("amount", "1");
        try {
            String resolved;
            if ("{amount}".equals(configured)) {
                resolved = String.valueOf(thingRun.getActionTradeAmount(buy));
            } else if (configured.matches("[0-9]+")) {
                resolved = configured;
            } else {
                resolved = replacePlaceholder(configured, thingRun.getPlayer(), thingRun.getActionTradeAmount(buy));
            }
            int amount = new java.math.BigDecimal(resolved).intValueExact();
            if (amount > 0 && amount < 10000) {
                return amount;
            }
        } catch (IllegalArgumentException | ArithmeticException ignored) {
            // Invalid action quantities must not reach the transaction methods.
        }
        ErrorManager.errorManager.sendErrorMessage("§6Invalid action trade amount at " + section.getCurrentPath() +
                ".amount: " + configured + ". Expected an integer from 1 to 9999; using 1.");
        return 1;
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

    public ObjectAction createNestedAction(ConfigurationSection actionSection) {
        if (item != null) {
            return new ObjectAction(actionSection, item);
        }
        if (shop != null) {
            return new ObjectAction(actionSection, shop);
        }
        return new ObjectAction(actionSection);
    }

}
