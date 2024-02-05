package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

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

    @Override
    public void openGUI() {
        if (form != null) {
            FloodgatePlayer player = FloodgateApi.getInstance().getPlayer(owner.getUniqueId());
            player.sendForm(form);
        }
    }

    public Form getForm() {
        return form;
    }
}
