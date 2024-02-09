package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ObjectCondition {

    private final List<String> condition;

    public ObjectCondition() {
        this.condition = new ArrayList<>();
        condition.add("none");
    }

    public ObjectCondition(List<String> condition) {
        this.condition = condition;
    }

    public boolean getBoolean(Player player) {
        if (player == null) {
            return false;
        }
        boolean conditionTrueOrFasle = true;
        for (String singleCondition : condition){
            if (singleCondition.equals("none")) {
                return true;
            } else if (singleCondition.startsWith("world: ")) {
                int i = 0;
                for (String str : singleCondition.substring(7).split(";;")){
                    if (str.equals(player.getWorld().getName())){
                        break;
                    }
                    i ++;
                }
                if (i == singleCondition.substring(7).split(";;").length){
                    conditionTrueOrFasle = false;
                    break;
                }
            } else if (singleCondition.startsWith("permission: ")) {
                for (String str : singleCondition.substring(12).split(";;")) {
                    if (!player.hasPermission(str)) {
                        conditionTrueOrFasle = false;
                        break;
                    }
                }
            } else if (singleCondition.startsWith("placeholder: ")) {
                try {
                    if (singleCondition.split(";;").length == 3) {
                        String[] conditionString = singleCondition.substring(13).split(";;");
                        String placeholder = TextUtil.withPAPI(conditionString[0], player);
                        //Bukkit.getConsoleSender().sendMessage(placeholder);
                        String conditionValue = conditionString[1];
                        String value = conditionString[2];
                        if (conditionValue.equals("==")) {
                            if (!placeholder.equals(value)) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("!=")) {
                            if (placeholder.equals(value)) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("*=")) {
                            if (!placeholder.contains(value)) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("!*=")) {
                            if (placeholder.contains(value)) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals(">=")) {
                            if (!(Double.parseDouble(placeholder) >= Double.parseDouble(value))) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals(">")) {
                            if (!(Double.parseDouble(placeholder) > Double.parseDouble(value))) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("<=")) {
                            if (!(Double.parseDouble(placeholder) <= Double.parseDouble(value))) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("<")) {
                            if (!(Double.parseDouble(placeholder) < Double.parseDouble(value))) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                        if (conditionValue.equals("=")) {
                            if (!(Double.parseDouble(placeholder) == Double.parseDouble(value))) {
                                conditionTrueOrFasle = false;
                                break;
                            }
                        }
                    }
                    else {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your placeholder condition in totem configs can not being correctly load.");
                        return false;
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your placeholder condition in totem configs can not being correctly load.");
                    return false;
                }
            }
        }
        return conditionTrueOrFasle;
    }
}
