package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.gui.inv.FavouriteGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteResultButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class ArcFavouriteGUI extends ArcCommonGUI {
    ArcFavouriteGUI(FavouriteGUI source, ArcMenuRuntime runtime) {
        super(source, runtime);
    }

    @Override
    protected ObjectItem product(int slot) {
        if (((FavouriteGUI) source).isEditing()) {
            return null;
        }
        return buttons.get(slot) instanceof ObjectFavouriteResultButton result ? result.getItem() : super.product(slot);
    }

    @Override
    protected boolean isProductCard(int slot) {
        return super.isProductCard(slot) || buttons.get(slot) instanceof ObjectFavouriteResultButton;
    }

    private boolean isEditing(int slot) {
        return buttons.get(slot) instanceof ObjectFavouriteResultButton favourite && favourite.isEditing();
    }

    @Override
    protected double cardActionHeight(int slot) {
        return isEditing(slot) ? number("cards.button-height", 27, 18, 36)
            + number("cards.buttons.inset-bottom", 6, 3, 16) : 0;
    }

    @Override
    protected void renderCardActions(int slot, double x, double y, double width, double height) {
        if (!isEditing(slot)) {
            return;
        }
        String[] keys = {"forward", "backward", "remove"};
        String[] fallbacks = {"Move Forward", "Move Backward", "Remove"};
        ClickType[] clicks = {ClickType.LEFT, ClickType.RIGHT, ClickType.DROP};
        double inset = number("cards.buttons.inset-x", 4, 2, 16);
        double gap = number("cards.buttons.gap", 3, 2, 12);
        double buttonWidth = (width - inset * 2 - gap * 2) / 3;
        double buttonHeight = number("cards.button-height", 27, 18, 36);
        for (int i = 0; i < keys.length; i++) {
            ClickType click = clicks[i];
            String label = ConfigManager.configManager.getMenuRendererText(
                getMenu().getConfig(), player, "form", "favourite-edit.buttons." + keys[i], fallbacks[i]);
            button("slot:" + slot + ":edit:" + keys[i],
                x + inset + i * (buttonWidth + gap),
                y + height - buttonHeight - number("cards.buttons.inset-bottom", 6, 3, 16),
                buttonWidth, buttonHeight,
                text("favourite-edit." + keys[i], label), List.of(label),
                type -> click(slot, click));
        }
    }
}
