package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectConditionalPlaceholder;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubGetPlaceholderValue extends AbstractCommand {

    public SubGetPlaceholderValue() {
        this.id = "getplaceholdervalue";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = true;
        this.premiumOnly = true;
        this.requiredArgLength = new Integer[]{2};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        LanguageManager.languageManager.sendStringText(player, "parsed-value", "text", args[1]);
    }

    @Override
    public List<String> getTabResult(String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                for (ObjectRandomPlaceholder tempVal2: ConfigManager.configManager.getRandomPlaceholders()) {
                    tempVal1.add("{random_" + tempVal2.getID() + "}");
                    tempVal1.add("{random-times_" + tempVal2.getID() + "}");
                }
                for (ObjectConditionalPlaceholder tempVal3 : ConfigManager.configManager.getConditionalPlaceholders()) {
                    tempVal1.add("{conditional_" + tempVal3.getID() + "}");
                }
                tempVal1.add("{compare_50_100}");
                tempVal1.add("{math_5*6-20}");
                break;
        }
        return tempVal1;
    }
}
