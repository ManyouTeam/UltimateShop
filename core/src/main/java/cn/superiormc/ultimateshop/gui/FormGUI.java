package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class FormGUI extends AbstractGUI {

    protected Form form;

    public Map<Integer, AbstractButton> menuButtons = new TreeMap<>();

    public Map<ButtonComponent, Integer> menuItems = new LinkedHashMap<>();

    public FormGUI(Player owner) {
        super(owner);
    }

    public void openGUI(boolean reopen) {
        if (!super.canOpenGUI(reopen)) {
            return;
        }
        if (form != null) {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
            if (getMenu() != null) {
                getMenu().doOpenAction(player, reopen);
            }
        }
    }

    public Form getForm() {
        return form;
    }
}
