package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;

public class FormCommonGUI extends FormGUI {

    private ObjectMenu commonMenu;

    private final boolean bypass;

    public FormCommonGUI(Player owner, ObjectMenu menu, boolean bypass) {
        super(owner);
        this.bypass = bypass;
        this.commonMenu = menu;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        if (!bypass && !commonMenu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player,
                    "menu-condition-not-meet",
                    "menu",
                    commonMenu.getName());
            return;
        }
        menuButtons = commonMenu.getMenu();
        SimpleForm.Builder tempVal2 = SimpleForm.builder();
        for (int slot : menuButtons.keySet()) {
            AbstractButton button = menuButtons.get(slot);
            ObjectDisplayItemStack displayItem = button.getDisplayItem(player, 1);
            ButtonComponent tempVal1 = displayItem.parseToBedrockButton();
            if (tempVal1 != null) {
                tempVal2.button(tempVal1);
            }
            menuItems.put(tempVal1, slot);
        }
        tempVal2.title(TextUtil.parse(player, commonMenu.getString("title", "Shop")));
        tempVal2.validResultHandler(response -> {
            menuButtons.get(menuItems.get(response.clickedButton())).clickEvent(ClickType.LEFT, player);
            removeOpenGUIStatus();
        });
        tempVal2.closedOrInvalidResultHandler(response -> removeOpenGUIStatus());
        if (commonMenu.getString("bedrock.content", null) != null) {
            tempVal2.content(TextUtil.parse(player, getMenu().getString("bedrock.content", "")));
        }
        form = tempVal2.build();
    }

    @Override
    public ObjectMenu getMenu() {
        return commonMenu;
    }

}
