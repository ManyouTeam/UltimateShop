package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ArcMenuRenderer;
import cn.superiormc.ultimateshop.managers.AbstractManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class ArcMenuBridge extends AbstractManager {

    public static ArcMenuBridge arcMenuBridge;

    private ArcMenuRuntime runtime;

    private Plugin rendererOwner;

    private ArcMenuRenderer renderer;

    public ArcMenuBridge() {
        arcMenuBridge = this;
    }

    /** Lets an addon present ArcMenu menus; menus it does not handle keep the built-in renderer. */
    public void registerRenderer(Plugin owner, ArcMenuRenderer renderer) {
        this.rendererOwner = owner;
        this.renderer = renderer;
    }

    public void unregisterRenderer(Plugin owner) {
        if (rendererOwner == owner) {
            rendererOwner = null;
            renderer = null;
        }
    }

    private ArcMenuRenderer renderer() {
        return renderer != null && rendererOwner != null && rendererOwner.isEnabled() ? renderer : null;
    }

    public boolean enabled(ObjectMenu menu, Player player) {
        return (runtime != null || renderer() != null)
            && CommonUtil.checkPluginLoad("ArcMenu")
            && !UltimateShop.freeVersion
            && menu != null
            && menu.getConfig() != null
            && menu.supportsGraphicalRenderer("arcmenu")
            && ConfigManager.configManager.getMenuUIMode(menu.getConfig(), "java").equals("arcmenu")
            && !CommonUtil.isBedrockPlayer(player);
    }

    public boolean open(Player player, ObjectMenu menu, ObjectShop shop, boolean bypass, boolean reopen) {
        return open(player, menu, shop, bypass, reopen, false);
    }

    public boolean open(Player player, ObjectMenu menu, ObjectShop shop, boolean bypass, boolean reopen,
        boolean editing) {
        if (!enabled(menu, player)) {
            return false;
        }
        ArcMenuRenderer addon = renderer();
        if (addon != null && addon.handles(menu, player)) {
            return addon.open(player, menu, shop, bypass, reopen, editing);
        }
        return runtime != null && runtime.open(player, menu, shop, bypass, reopen, editing);
    }

    public boolean openInfo(Player player, ObjectItem item) {
        ArcMenuRenderer addon = renderer();
        if (addon != null && addon.openInfo(player, item)) {
            return true;
        }
        if (MenuStatusManager.menuStatusManager.getOpeningGUI(player) instanceof ArcCommonGUI gui
            && gui.isActive()) {
            gui.openInfo(item);
            return true;
        }
        return false;
    }

    @Override
    public void onInit() {
        if (!CommonUtil.checkPluginLoad("ArcMenu")) {
            return;
        }
        try {
            runtime = ArcMenuRuntime.create();
        } catch (LinkageError | RuntimeException exception) {
            ErrorManager.errorManager.sendErrorMessage(
                "§cError: ArcMenu integration unavailable: " + exception.getMessage());
        }
    }

    @Override
    public void onPluginReload() {
        onPluginDisable();
        onInit();
    }

    @Override
    public void onPluginDisable() {
        if (runtime != null) {
            try {
                runtime.close();
            } finally {
                runtime = null;
            }
        }
    }
}
