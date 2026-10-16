package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.menus.ObjectFavouriteMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectSearchMenu;
import org.bukkit.entity.Player;
import cn.superiormc.ultimateshop.managers.ErrorManager;

import com.fentai.arcmenu.api.ArcMenuApi;
import com.fentai.arcmenu.api.ArcMenuApplicationHandle;
import com.fentai.arcmenu.api.ArcMenuApplicationOptions;
import com.fentai.arcmenu.api.ArcMenuApplicationSession;
import com.fentai.arcmenu.api.ArcMenuCanvas;
import com.fentai.arcmenu.api.ArcMenuCapability;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class ArcMenuRuntime implements AutoCloseable {

    private static final String APPLICATION = "ultimateshop:shop";

    private final ArcMenuApi api;

    private final ArcMenuApplicationHandle handle;

    private final Map<UUID, ArcMenuGUI> pending = new ConcurrentHashMap<>();

    double cursorDepth() {
        var plugin = Bukkit.getPluginManager().getPlugin("ArcMenu");
        double depth =
            plugin instanceof JavaPlugin javaPlugin
        ? javaPlugin.getConfig().getDouble("mouse.cursor.z", 5)
        : 5;
        return Double.isFinite(depth) ? depth : 5;
    }

    static ArcMenuRuntime create() {
        ArcMenuApi api = Bukkit.getServicesManager().load(ArcMenuApi.class);
        if (api == null
            || !api.capabilities()
            .containsAll(
            Set.of(
            ArcMenuCapability.APPLICATION_SESSIONS,
            ArcMenuCapability.SCREEN_SURFACE,
            ArcMenuCapability.POINTER_EVENTS))) {
            return null;
        }
        return new ArcMenuRuntime(api);
    }

    private ArcMenuRuntime(ArcMenuApi api) {
        this.api = api;
        handle =
            api.registerApplication(
            UltimateShop.instance,
            APPLICATION,
            ArcMenuApplicationOptions.builder()
            .captureMouseScroll(true)
            .inheritCanvas(false)
            .canvas(
            new ArcMenuCanvas(
            UltimateShop.instance
            .getConfig()
            .getInt("menu.ui.layout.canvas.width", 648),
            UltimateShop.instance
            .getConfig()
            .getInt(
            "menu.ui.layout.canvas.height",
            396),
            ArcMenuLayout.designPixelsPerBlock(
            UltimateShop.instance
            .getConfig()
            .getDouble(
            "menu.ui.renderers.arcmenu.screen-scale",
            1.5)),
            3))
            .build(),
            context -> {
                ArcMenuGUI gui = pending.remove(context.getPlayer().getUniqueId());
                if (gui == null) {
                    ErrorManager.errorManager.sendErrorMessage(
                        "§cOpen this application through UltimateShop");
                    context.close();
                    return new ArcMenuApplicationSession() {

                    };
                }
                return gui.attach(context);
            });
    }

    boolean open(Player player, ObjectMenu menu, ObjectShop shop, boolean bypass, boolean reopen, boolean editing) {
        // ArcMenu defers airborne opens; this synchronous adapter must retain its exact factory.
        if (!player.isOnGround()) {
            return false;
        }
        try {
            ArcMenuGUI gui;
            if (menu instanceof ObjectSearchMenu search) {
                gui = new ArcSearchGUI(player, search, bypass, this);
            } else if (menu instanceof ObjectFavouriteMenu favourite) {
                gui = new ArcFavouriteGUI(player, favourite, bypass, editing, this);
            } else if (shop != null) {
                gui = new ArcShopGUI(player, menu, shop, bypass, this);
            } else {
                gui = new ArcCommonGUI(player, menu, bypass, this);
            }
            return gui.tryOpen(reopen);
        } catch (IllegalArgumentException exception) {
            ErrorManager.errorManager.sendErrorMessage(
                "§cInvalid UI layout in " + menu.getName() + ": " + exception.getMessage());
            return false;
        }
    }

    boolean open(ArcMenuGUI gui) {
        UUID id = gui.getPlayer().getUniqueId();
        pending.put(id, gui);
        try {
            boolean opened = api.openApplication(gui.getPlayer(), APPLICATION, List.of());
            if (opened && !gui.isActive()) {
                // A deferred open cannot use a factory after the synchronous pending entry expires.
                api.close(gui.getPlayer());
            }
            return opened;
        } finally {
            pending.remove(id, gui);
        }
    }

    @Override
    public void close() {
        handle.unregister();
        pending.clear();
    }
}
