package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.editor.EditorManager;
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
            EditorManager.editorManager.openRoot(player);
            return;
        }

        EditorScope scope = EditorScope.ofName(args[1]);
        if (scope == null) {
            EditorLang.send(player, "editor.message.command-usage",
                    "&cUse /shop editor <category> <id>. Press Tab to list categories.");
            return;
        }

        EditorTarget target = EditorTarget.load(scope, args[2]);
        if (target == null) {
            EditorLang.send(player, "editor.message.target-missing", "&cCould not find {scope}: {id}",
                    "scope", EditorLang.scope(player, scope).toLowerCase(),
                    "id", args[2]);
            return;
        }

        EditorManager.editorManager.openTarget(player, target, "", 0);
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {
        List<String> result = new ArrayList<>();
        if (args.length == 2) {
            for (EditorScope scope : EditorScope.values()) {
                result.add(scope.getCommandName());
            }
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
