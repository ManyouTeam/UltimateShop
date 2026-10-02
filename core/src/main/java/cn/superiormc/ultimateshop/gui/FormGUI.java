package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class FormGUI extends AbstractGUI {

    protected Form form;

    private Form consumedForm;

    public Map<Integer, AbstractButton> menuButtons = new TreeMap<>();

    public Map<ButtonComponent, Integer> menuItems = new LinkedHashMap<>();

    public FormGUI(Player owner) {
        super(owner);
    }

    @Override
    public void updateGUI() {
        form = null;
        menuItems.clear();
        constructGUI();
        if (form != null) {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
        }
    }

    @Override
    public void openGUI(boolean reopen) {
        GUIStatus previousStatus = MenuStatusManager.menuStatusManager.getGUIStatus(player);
        if (!MenuStatusManager.menuStatusManager.canOpenGUI(player, this, reopen)) {
            return;
        }
        if (form != null && form == consumedForm) {
            form = null;
            menuItems.clear();
            constructGUI();
        }
        if (form != null) {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
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
    public void closeGUI() {
        // Empty...
    }

    public Form getForm() {
        return form;
    }

    protected void handleResponse(Form submittedForm, Runnable action) {
        SchedulerUtil.runSync(player, () -> {
            if (!player.isOnline() || submittedForm == null || submittedForm != form || submittedForm == consumedForm
                    || MenuStatusManager.menuStatusManager.getOpeningGUI(player) != this) {
                return;
            }
            consumedForm = submittedForm;
            action.run();
        });
    }
}
