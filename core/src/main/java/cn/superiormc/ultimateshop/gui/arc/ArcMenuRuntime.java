package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.FavouriteGUI;
import cn.superiormc.ultimateshop.gui.inv.SearchGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
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

    boolean open(InvGUI source, boolean reopen) {
        // ArcMenu defers airborne opens; this synchronous adapter must retain its exact factory.
        if (!source.getPlayer().isOnGround()) {
            return false;
        }
        try {
            ArcMenuGUI gui;
            if (source instanceof SearchGUI search) {
                gui = new ArcSearchGUI(search, this);
            } else if (source instanceof FavouriteGUI favourite) {
                gui = new ArcFavouriteGUI(favourite, this);
            } else if (source instanceof ShopGUI shop) {
                gui = new ArcShopGUI(shop, this);
            } else {
                gui = new ArcCommonGUI(source, this);
            }
            return gui.tryOpen(reopen);
        } catch (IllegalArgumentException exception) {
            ErrorManager.errorManager.sendErrorMessage(
                "§cInvalid UI layout in "
                + source.getMenu().getName()
                + ": "
                + exception.getMessage());
            return false;
        }
    }

    boolean open(ArcMenuGUI gui) {
        UUID id = gui.getPlayer().getUniqueId();
        pending.put(id, gui);
        try {
            boolean opened = api.openApplication(gui.getPlayer(), APPLICATION, List.of());
            if (opened && !gui.isActivePresentation()) {
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
