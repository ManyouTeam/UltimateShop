package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.Prompt;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ActionPrompt extends AbstractRunAction {

    public ActionPrompt() {
        super("prompt");
        setRequiredArgs("actions");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        if (player == null) {
            return;
        }

        ConfigurationSection submitSection = singleAction.getSection().getConfigurationSection("actions");
        if (submitSection == null) {
            return;
        }

        String description = getDescription(singleAction, player, thingRun.getAmount());
        if (description == null || description.isEmpty()) {
            return;
        }

        ConfigurationSection cancelSection = singleAction.getSection().getConfigurationSection("cancel-actions");
        boolean reopenOnSubmit = singleAction.getBoolean("reopen-on-submit", false);
        boolean reopenOnCancel = singleAction.getBoolean("reopen-on-cancel", true);
        AbstractGUI previousGUI = getPreviousGUI(player);

        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
                description,
                (p, input) -> {
                    PromptArguments promptArguments = new PromptArguments(input);
                    ObjectAction action = singleAction.createNestedAction(
                            copySectionWithPromptArgs(submitSection, promptArguments));
                    action.runAllActions(thingRun);
                    if (reopenOnSubmit) {
                        reopenGUI(previousGUI);
                    }
                },
                p -> {
                    if (cancelSection != null) {
                        ObjectAction cancelAction = singleAction.createNestedAction(
                                copySectionWithPromptArgs(cancelSection, new PromptArguments("")));
                        cancelAction.runAllActions(thingRun);
                    }
                    if (reopenOnCancel) {
                        reopenGUI(previousGUI);
                    }
                },
                false
        ));
    }

    private String getDescription(ObjectSingleAction singleAction, Player player, double amount) {
        if (singleAction.contains("description")) {
            return singleAction.getString("description", player, amount);
        }
        if (singleAction.contains("prompt")) {
            return singleAction.getString("prompt", player, amount);
        }
        return null;
    }

    private AbstractGUI getPreviousGUI(Player player) {
        GUIStatus guiStatus = AbstractGUI.playerList.get(player);
        if (guiStatus == null) {
            return null;
        }
        return guiStatus.getGUI();
    }

    private void reopenGUI(AbstractGUI gui) {
        if (gui instanceof InvGUI invGUI) {
            invGUI.openGUI(true);
            return;
        }
        if (gui instanceof FormGUI formGUI) {
            formGUI.openGUI(true);
        }
    }

    private ConfigurationSection copySectionWithPromptArgs(ConfigurationSection source, PromptArguments promptArguments) {
        MemoryConfiguration configuration = new MemoryConfiguration();
        copySection(configuration, source, promptArguments);
        return configuration;
    }

    private void copySection(ConfigurationSection target, ConfigurationSection source, PromptArguments promptArguments) {
        for (String key : source.getKeys(false)) {
            Object value = source.get(key);
            if (value instanceof ConfigurationSection childSection) {
                ConfigurationSection newChild = target.createSection(key);
                copySection(newChild, childSection, promptArguments);
                continue;
            }
            if (value instanceof List<?> listValue) {
                target.set(key, copyList(listValue, promptArguments));
                continue;
            }
            if (value instanceof String stringValue) {
                target.set(key, promptArguments.apply(stringValue));
                continue;
            }
            target.set(key, value);
        }
    }

    private List<Object> copyList(List<?> source, PromptArguments promptArguments) {
        List<Object> result = new ArrayList<>();
        for (Object value : source) {
            if (value instanceof String stringValue) {
                result.add(promptArguments.apply(stringValue));
                continue;
            }
            if (value instanceof List<?> listValue) {
                result.add(copyList(listValue, promptArguments));
                continue;
            }
            result.add(value);
        }
        return result;
    }

    private static class PromptArguments {

        private final String rawInput;

        private final List<String> splitArguments;

        private PromptArguments(String input) {
            this.rawInput = input == null ? "" : input;
            String trimmedInput = rawInput.trim();
            this.splitArguments = new ArrayList<>();
            if (!trimmedInput.isEmpty()) {
                for (String value : trimmedInput.split("\\s+")) {
                    splitArguments.add(value);
                }
            }
        }

        private String apply(String content) {
            String result = content.replace("{arg}", rawInput);
            for (int i = 0; i < splitArguments.size(); i++) {
                result = result.replace("{arg_" + (i + 1) + "}", splitArguments.get(i));
            }
            return clearUnusedArguments(result);
        }

        private String clearUnusedArguments(String content) {
            return content.replaceAll("\\{arg_\\d+}", "");
        }
    }
}
