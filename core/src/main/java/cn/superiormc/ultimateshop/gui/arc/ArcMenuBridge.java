package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.managers.AbstractManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.menus.MenuType;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ArcMenuBridge extends AbstractManager {

    public static ArcMenuBridge arcMenuBridge;

    private ArcMenuRuntime runtime;

    public ArcMenuBridge() {
        arcMenuBridge = this;
    }

    public boolean enabled(ObjectMenu menu, Player player) {
        return runtime != null
            && CommonUtil.checkPluginLoad("ArcMenu")
            && !UltimateShop.freeVersion
            && menu != null
            && menu.getConfig() != null
            && menu.getType() != MenuType.ItemSell
            && menu.getType() != MenuType.More
            && ConfigManager.configManager
            .getMenuUIMode(menu.getConfig(), "java")
            .equals("arcmenu")
            && !CommonUtil.isBedrockPlayer(player);
    }

    public boolean open(Player player, ObjectMenu menu, ObjectShop shop, boolean bypass, boolean reopen) {
        return open(player, menu, shop, bypass, reopen, false);
    }

    public boolean open(Player player, ObjectMenu menu, ObjectShop shop, boolean bypass, boolean reopen,
        boolean editing) {
        return enabled(menu, player) && runtime.open(player, menu, shop, bypass, reopen, editing);
    }

    public boolean openInfo(Player player, ObjectItem item) {
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
                "§cArcMenu integration unavailable: " + exception.getMessage());
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
