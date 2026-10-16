package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.editor.EditorScope;
import cn.superiormc.ultimateshop.editor.EditorTarget;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubEditor extends AbstractCommand {

    public SubEditor() {
        this.id = "editor";
        this.requiredPermission = "ultimateshop." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1, 3};
        this.premiumOnly = true;
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        if (!ConfigManager.configManager.getBoolean("editor.enabled")) {
            EditorLang.send(player, "editor.message.disabled",
                    "&cSet &feditor.enabled &cto true in &fconfig.yml &cand reload the plugin to enable the editor.");
            EditorLang.send(player, "editor.message.backup-required",
                    "&7Back up the plugin configuration before every use of the editor.");
            return;
        }
        if (args.length == 1) {
            MenuStatusManager.menuStatusManager.openRoot(player);
            return;
        }

        EditorScope scope = EditorScope.ofName(args[1]);
        if (scope == null) {
            EditorLang.send(player, "editor.message.command-usage",
                    "&cUse /shop editor [shop|menu|shop_menu_template|buy_more_menu|favourite_menu|search_menu|item_sell_menu] <id>");
            return;
        }

        EditorTarget target = EditorTarget.load(scope, args[2]);
        if (target == null) {
            EditorLang.send(player, "editor.message.target-missing", "&cCould not find {scope}: {id}",
                    "scope", EditorLang.scope(player, scope).toLowerCase(),
                    "id", args[2]);
            return;
        }

        MenuStatusManager.menuStatusManager.openTarget(player, target, "", 0);
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {
        List<String> result = new ArrayList<>();
        if (args.length == 2) {
            result.add("shop");
            result.add("menu");
            result.add("shop_menu_template");
            result.add("buy_more_menu");
            result.add("favourite_menu");
            result.add("search_menu");
            result.add("item_sell_menu");
            return result;
        }
        if (args.length == 3) {
            EditorScope scope = EditorScope.ofName(args[1]);
            if (scope != null) {
                result.addAll(scope.listIds());
            }
        }
        return result;
    }
}
