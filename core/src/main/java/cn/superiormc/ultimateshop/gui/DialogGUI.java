package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.dialog.DialogAction;
import cn.superiormc.ultimateshop.gui.dialog.DialogResponse;
import cn.superiormc.ultimateshop.gui.dialog.DialogView;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;

public abstract class DialogGUI extends AbstractGUI {

    protected DialogView dialog;

    private long generation;

    protected DialogGUI(Player owner) {
        super(owner);
    }

    protected String getDialogText(String path, String... replacements) {
        return CommonUtil.modifyString(player,
                ConfigManager.configManager.getString("menu.dialog." + path, ""), replacements);
    }

    @Override
    public void openGUI(boolean reopen) {
        GUIStatus previousStatus = MenuStatusManager.menuStatusManager.getGUIStatus(player);
        if (!MenuStatusManager.menuStatusManager.canOpenGUI(player, this, reopen)) {
            return;
        }
        constructGUI();
        generation++;
        player.closeInventory();
        if (dialog != null && UltimateShop.methodUtil.showDialog(player, this, dialog)) {
            if (getMenu() != null) {
                getMenu().doOpenAction(player, reopen);
            }
        } else if (previousStatus == null) {
            MenuStatusManager.menuStatusManager.removeGUIStatus(player);
        } else {
            MenuStatusManager.menuStatusManager.setGUIStatus(player, previousStatus);
        }
    }

    @Override
    public void updateGUI() {
        openGUI(true);
    }

    @Override
    public void closeGUI() {
        UltimateShop.methodUtil.closeDialog(player);
        finishGUI();
    }

    public boolean handleAction(String actionId, DialogResponse response, long expectedGeneration) {
        if (expectedGeneration != generation || dialog == null) {
            return false;
        }
        for (DialogAction action : dialog.getActions()) {
            if (action.getId().equals(actionId)) {
                finishGUI();
                action.execute(response == null ? DialogResponse.empty() : response);
                return true;
            }
        }
        return false;
    }

    public DialogView getDialog() {
        return dialog;
    }

    public long getGeneration() {
        return generation;
    }
}
