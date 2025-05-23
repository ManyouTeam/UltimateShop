package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand {

    protected String id;

    protected String requiredPermission;

    protected boolean onlyInGame;

    protected Integer[] requiredArgLength;

    protected Integer[] requiredConsoleArgLength;

    protected boolean premiumOnly = false;

    public AbstractCommand() {
        // EMPTY
    }

    public abstract void executeCommandInGame(String[] args, Player player);

    public void executeCommandInConsole(String[] args) {
        LanguageManager.languageManager.sendStringText("error.in-game");
    }

    public String getId() {
        return id;
    }

    public boolean getOnlyInGame() {
        return onlyInGame;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public boolean getLengthCorrect(int length, CommandSender sender) {
        if (requiredConsoleArgLength == null || requiredConsoleArgLength.length == 0) {
            requiredConsoleArgLength = requiredArgLength;
        }
        if (sender instanceof Player) {
            for (int number : requiredArgLength) {
                if (number == length) {
                    return true;
                }
            }
        } else {
            for (int number : requiredConsoleArgLength) {
                if (number == length) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getTabResult(String[] args) {
        return new ArrayList<>();
    }
}
