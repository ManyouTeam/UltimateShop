package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

public class FormCommonGUI extends FormGUI {

    private ObjectMenu commonMenu = null;

    private final String fileName;

    private final boolean bypass;

    public FormCommonGUI(Player owner, String fileName, boolean bypass) {
        super(owner);
        this.fileName = fileName;
        this.bypass = bypass;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        commonMenu = ObjectMenu.commonMenus.get(fileName);
        if (commonMenu == null || commonMenu.menuConfigs == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.menu-not-found",
                    "menu",
                    fileName);
            return;
        }
        if (!bypass && !commonMenu.getCondition().getBoolean(player.getPlayer())) {
            LanguageManager.languageManager.sendStringText(player,
                    "menu-condition-not-meet",
                    "menu",
                    fileName);
            return;
        }
        menuButtons = commonMenu.getMenu();
        SimpleForm.Builder tempVal2 = SimpleForm.builder();
        for (int slot : menuButtons.keySet()) {
            AbstractButton button = menuButtons.get(slot);
            ItemStack displayItem = button.getDisplayItem(player, 1);
            if (ItemUtil.getItemNameWithoutVanilla(displayItem).trim().isEmpty() ||
                    button.getButtonConfig().getBoolean("bedrock.hide", false)) {
                continue;
            }
            String icon = button.getButtonConfig().getString("bedrock.icon",
                    button.getButtonConfig().getString("bedrock-icon"));
            String tempVal3 = TextUtil.parse(ItemUtil.getItemName(displayItem), player);
            ButtonComponent tempVal1 = null;
            if (icon != null && icon.split(";;").length == 2) {
                String type = icon.split(";;")[0].toLowerCase();
                if (type.equals("url")) {
                    tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.URL, icon.split(";;")[1]);
                } else if (type.equals("path")) {
                    tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.PATH, icon.split(";;")[1]);
                }
            } else {
                tempVal1 = ButtonComponent.of(tempVal3);
            }
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
        form = tempVal2.build();
    }

    public ObjectMenu getMenu() {
        return commonMenu;
    }

}
