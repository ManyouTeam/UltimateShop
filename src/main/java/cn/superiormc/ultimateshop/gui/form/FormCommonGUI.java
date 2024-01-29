package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.Objects;

public class FormCommonGUI extends FormGUI {

    private ObjectMenu commonMenu = null;

    private String fileName;

    public FormCommonGUI(Player owner, String fileName) {
        super(owner);
        this.fileName = fileName;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        commonMenu = ObjectMenu.commonMenus.get(fileName);
        if (commonMenu == null) {
            LanguageManager.languageManager.sendStringText(owner,
                    "error.menu-not-found",
                    "menu",
                    fileName);
            return;
        }
        menuButtons = commonMenu.getMenu();
        SimpleForm.Builder tempVal2 = SimpleForm.builder();
        if (Objects.isNull(form)) {
            for (int slot : menuButtons.keySet()) {
                AbstractButton button = menuButtons.get(slot);
                String tempVal3 = TextUtil.parse(
                        CommonUtil.getItemName(button.getDisplayItem(
                                owner.getPlayer(), 1)));
                if (tempVal3.length() == 0) {
                    continue;
                }
                ButtonComponent tempVal6 = null;
                String icon = button.getButtonConfig().getString("bedrock-icon");
                if (icon != null && icon.split(";;").length == 2) {
                    String type = icon.split(";;")[0].toLowerCase();
                    if (type.equals("url")) {
                        tempVal6 = ButtonComponent.of(tempVal3, FormImage.Type.URL, icon.split(";;")[1]);
                    } else if (type.equals("path")) {
                        tempVal6 = ButtonComponent.of(tempVal3, FormImage.Type.PATH, icon.split(";;")[1]);
                    }
                } else {
                    tempVal6 = ButtonComponent.of(tempVal3);
                }
                if (tempVal6 != null) {
                    tempVal2.button(tempVal6);
                }
                menuItems.put(tempVal6, slot);
            }
        }
        tempVal2.title(TextUtil.parse(commonMenu.getString("title", "Shop")));
        tempVal2.validResultHandler(response -> {
            menuButtons.get(menuItems.get(response.clickedButton())).clickEvent(ClickType.LEFT, owner.getPlayer());
        });
        form = tempVal2.build();
    }

}
