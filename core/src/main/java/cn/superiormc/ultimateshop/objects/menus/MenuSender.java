package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.gui.dialog.DialogView;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import org.bukkit.entity.Player;

import java.util.Objects;

public class MenuSender {

    public static final MenuSender empty = new MenuSender();

    private Player player;

    private final boolean isStatic;

    private final String presentation;

    private MenuSender(Player player, String presentation) {
        this.isStatic = player == null;
        this.player = player;
        this.presentation = presentation;
    }

    private MenuSender() {
        this.isStatic = true;
        this.presentation = null;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public static MenuSender of(Player player) {
        return player == null ? empty : new MenuSender(player, "inventory");
    }

    public static MenuSender of(Player player, String presentation) {
        return new MenuSender(player, presentation);
    }

    public String getPresentation() {
        return presentation;
    }

    public static String dialogPresentation(String layout) {
        return "ore".equalsIgnoreCase(layout) ? "ore"
                : "item-action-list".equalsIgnoreCase(layout) ? "item-action-list" : "dialog";
    }

    public static String getOpeningPresentation(Player player) {
        if (player == null || MenuStatusManager.menuStatusManager == null) {
            return null;
        }
        var gui = MenuStatusManager.menuStatusManager.getOpeningGUI(player);
        if (gui == null) {
            return null;
        }
        if (gui instanceof FormGUI) {
            return "form";
        }
        if (gui instanceof DialogGUI dialogGUI) {
            var view = dialogGUI.getDialog();
            if (view != null) {
                return view.getScreen() != null ? "ore"
                        : view.getLayout() == DialogView.Layout.ITEM_ACTION_LIST
                        ? "item-action-list" : "dialog";
            }
            return dialogPresentation(gui.getMenu() == null ? null : gui.getMenu().getString("dialog.layout", "multi-action"));
        }
        return "inventory";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuSender)) {
            return false;
        }

        MenuSender that = (MenuSender) o;

        if (!Objects.equals(presentation, that.presentation)) {
            return false;
        }
        if (isStatic && that.isStatic) {
            return true;
        }

        return !isStatic && !that.isStatic &&
                player.getUniqueId().equals(that.player.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isStatic ? null : player.getUniqueId(), presentation);
    }
}
