package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.commands.AbstractCommand;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditorCommand extends AbstractCommand {

    public EditorCommand() {
        this.id = "editor";
        this.requiredPermission = "ultimateshop." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1, 3};
        this.premiumOnly = true;
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        if (!ConfigManager.configManager.isEditorConfirmedEnabled()) {
            File confirmFile = ConfigManager.configManager.getEditorEnableConfirmFile();
            EditorLang.send(player, "editor.message.enable-confirm-required",
                    "&cEditor is still experimental. Read and confirm every item in &f{file}&c before using it.",
                    "file", confirmFile.getAbsolutePath());
            EditorLang.send(player, "editor.message.enable-confirm-details",
                    "&7Back up data, read the wiki, accept possible errors, then set every confirmation in &f{file} &7to true.",
                    "file", confirmFile.getAbsolutePath());
            return;
        }
        if (args.length == 1) {
            EditorManager.editorManager.openRoot(player);
            return;
        }

        EditorScope scope = EditorScope.ofName(args[1]);
        if (scope == null) {
            EditorLang.send(player, "editor.message.command-usage", "&cUse /shop editor [shop|menu] <id>");
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
            result.add("shop");
            result.add("menu");
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
