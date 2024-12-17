package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.objects.actions.AbstractRunAction;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.actions.*;

import java.util.HashMap;
import java.util.Map;

public class ActionManager {

    public static ActionManager actionManager;

    private Map<String, AbstractRunAction> actions;

    public ActionManager() {
        actionManager = this;
        initActions();
    }

    private void initActions() {
        actions = new HashMap<>();
        registerNewAction("message", new ActionMessage());
        registerNewAction("sound", new ActionSound());
        registerNewAction("announcement", new ActionAnnouncement());
        registerNewAction("effect", new ActionEffect());
        registerNewAction("console_command", new ActionConsoleCommand());
        registerNewAction("op_command", new ActionOPCommand());
        registerNewAction("player_command", new ActionPlayerCommand());
        registerNewAction("close", new ActionClose());
        registerNewAction("teleport", new ActionTeleport());
        registerNewAction("entity_spawn", new ActionEntitySpawn());
        registerNewAction("mythicmobs_spawn", new ActionMythicMobsSpawn());
        registerNewAction("open_menu", new ActionOpenMenu());
        registerNewAction("shop_menu", new ActionShopMenu());
        registerNewAction("buy", new ActionBuy());
        registerNewAction("sell", new ActionSell());
        registerNewAction("chance", new ActionChance());
        registerNewAction("delay", new ActionDelay());
        registerNewAction("any", new ActionAny());
        registerNewAction("conditional", new ActionConditional());
        registerNewAction("connect", new ActionConnect());
    }

    public void registerNewAction(String actionID,
                                  AbstractRunAction action) {
        if (!actions.containsKey(actionID)) {
            actions.put(actionID, action);
        }
    }

    public void doAction(ObjectSingleAction action, ObjectThingRun thingRun) {
        for (AbstractRunAction runAction : actions.values()) {
            String type = action.getString("type");
            if (runAction.getType().equals(type)) {
                runAction.runAction(action, thingRun);
            }
        }
    }
}
