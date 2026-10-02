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

    private long finishedGeneration = Long.MIN_VALUE;

    private long consumedGeneration = Long.MIN_VALUE;

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

    @Override
    public void finishGUI() {
        consumedGeneration = generation;
        if (finishedGeneration == generation) {
            return;
        }
        finishedGeneration = generation;
        super.finishGUI();
    }

    public boolean handleAction(String actionId, DialogResponse response, long expectedGeneration) {
        if (!canHandleResponse(expectedGeneration)) {
            return false;
        }
        for (DialogAction action : dialog.getActions()) {
            if (action.getId().equals(actionId)) {
                consumedGeneration = expectedGeneration;
                boolean keepOpen = dialog.keepOpenAfterAction();
                if (!keepOpen) {
                    finishGUI();
                }
                action.execute(response == null ? DialogResponse.empty() : response);
                // A persistent dialog needs a fresh generation for its next intentional submission.
                if (keepOpen && generation == expectedGeneration && finishedGeneration != expectedGeneration
                        && player.isOnline() && MenuStatusManager.menuStatusManager.getOpeningGUI(player) == this) {
                    updateGUI();
                }
                return true;
            }
        }
        return false;
    }

    public void closeGUI(long expectedGeneration) {
        if (canHandleResponse(expectedGeneration)) {
            closeGUI();
        }
    }

    private boolean canHandleResponse(long expectedGeneration) {
        return player.isOnline() && expectedGeneration == generation && expectedGeneration != consumedGeneration
                && dialog != null && MenuStatusManager.menuStatusManager.getOpeningGUI(player) == this;
    }

    public DialogView getDialog() {
        return dialog;
    }

    public long getGeneration() {
        return generation;
    }
}
