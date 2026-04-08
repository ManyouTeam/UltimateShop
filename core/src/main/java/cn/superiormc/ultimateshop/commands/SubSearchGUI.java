package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.gui.inv.SearchGUI;
import cn.superiormc.ultimateshop.objects.menus.MenuType;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubSearchGUI extends AbstractCommand {

    public SubSearchGUI() {
        this.id = "searchgui";
        this.requiredPermission = "ultimateshop." + id;
        this.onlyInGame = true;
        this.premiumOnly = true;
        this.requiredArgLength = new Integer[]{1, 2};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        SearchGUI.openGUI(player, args.length > 1 ? args[1] : "search");
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {
        List<String> result = new ArrayList<>();
        if (args.length != 2) {
            return result;
        }
        for (ObjectMenu menu : ObjectMenu.commonMenus.values()) {
            if (menu.getType() == MenuType.Search) {
                result.add(menu.getName());
            }
        }
        return result;
    }
}
