package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;

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
                String tempVal3 = TextUtil.parse(
                        CommonUtil.getItemName(menuButtons.get(slot).getDisplayItem(
                                owner.getPlayer(), 1)));
                if (tempVal3.length() == 0) {
                    continue;
                }
                ButtonComponent tempVal1 = ButtonComponent.of(tempVal3);
                tempVal2.button(tempVal1);
                menuItems.put(tempVal1, slot);
            }
        }
        tempVal2.title(TextUtil.parse(commonMenu.getString("title", "Shop")));
        tempVal2.validResultHandler(response -> {
            menuButtons.get(menuItems.get(response.clickedButton())).clickEvent(ClickType.LEFT, owner.getPlayer());
        });
        form = tempVal2.build();
    }

}
