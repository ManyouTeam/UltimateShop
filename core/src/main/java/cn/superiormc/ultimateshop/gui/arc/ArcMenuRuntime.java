package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import com.fentai.arcmenu.api.ArcMenuApi;
import com.fentai.arcmenu.api.ArcMenuApplicationHandle;
import com.fentai.arcmenu.api.ArcMenuApplicationOptions;
import com.fentai.arcmenu.api.ArcMenuApplicationSession;
import com.fentai.arcmenu.api.ArcMenuCanvas;
import com.fentai.arcmenu.api.ArcMenuCapability;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class ArcMenuRuntime implements AutoCloseable {

    private static final String APPLICATION = "ultimateshop:shop";

    // BEGIN ARCMENU SHARED runtime
    private final ArcMenuApi api;

    private final ArcMenuApplicationHandle handle;

    private final Map<UUID, ArcMenuGUI> pending = new ConcurrentHashMap<>();

    private final Map<UUID, ArcMenuGUI> sessions = new ConcurrentHashMap<>();

    private boolean closing;

    static ArcMenuRuntime create() {
        ArcMenuApi api = Bukkit.getServicesManager().load(ArcMenuApi.class);
        if (api == null || !api.capabilities().containsAll(Set.of(
                ArcMenuCapability.APPLICATION_SESSIONS,
                ArcMenuCapability.SCREEN_SURFACE,
                ArcMenuCapability.POINTER_EVENTS)) || !supports(api)) {
            return null;
        }
        return new ArcMenuRuntime(api);
    }

    private ArcMenuRuntime(ArcMenuApi api) {
        this.api = api;
        handle = api.registerApplication(plugin(), APPLICATION, options(), context -> {
            ArcMenuGUI gui = pending.remove(context.getPlayer().getUniqueId());
            if (gui == null) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Open this application through " + plugin().getName());
                context.close();
                return new ArcMenuApplicationSession() {
                };
            }
            sessions.put(gui.getPlayer().getUniqueId(), gui);
            return gui.attach(context);
        });
    }

    double cursorDepth() {
        var plugin = Bukkit.getPluginManager().getPlugin("ArcMenu");
        double depth = plugin instanceof JavaPlugin javaPlugin ? javaPlugin.getConfig().getDouble("mouse.cursor.z", 5) : 5;
        return Double.isFinite(depth) ? depth : 5;
    }

    double cursorExtent(Player player, boolean vertical) {
        var plugin = Bukkit.getPluginManager().getPlugin("ArcMenu");
        double sensitivity = plugin instanceof JavaPlugin javaPlugin
                ? javaPlugin.getConfig().getDouble(vertical ? "mouse.cursor.sensitivity-y" : "mouse.cursor.sensitivity-x", 2) : 2;
        double margin = plugin instanceof JavaPlugin javaPlugin
                ? javaPlugin.getConfig().getDouble("mouse.cursor.clamp-margin", 3) : 3;
        double multiplier = api.preferences().get(player).getSensitivityMultiplier();
        return Math.max(1, (vertical ? 180 : 360) * sensitivity * multiplier - 2 * Math.max(0, margin));
    }

    boolean canOpen(Player player) {
        ArcMenuGUI gui = sessions.get(player.getUniqueId());
        return !closing && (gui != null && gui.isActive() || player.isOnGround());
    }

    boolean open(ArcMenuGUI gui) {
        if (!canOpen(gui.getPlayer())) {
            return false;
        }
        UUID id = gui.getPlayer().getUniqueId();
        ArcMenuGUI previous = sessions.get(id);
        if (previous != null && previous.isActive()) {
            if (previous == gui) {
                return true;
            }
            if (!previous.canLeaveView()) {
                return false;
            }
            if (previous.canReuseView(gui)) {
                previous.replaceView(gui);
                sessions.put(id, gui);
                return gui.isActive();
            }
        }
        pending.put(id, gui);
        try {
            boolean opened = api.openApplication(gui.getPlayer(), APPLICATION, List.of());
            if (opened && !gui.isActive()) {
                api.close(gui.getPlayer());
            }
            return opened && gui.isActive();
        } finally {
            pending.remove(id, gui);
        }
    }

    boolean detached(ArcMenuGUI gui) {
        return sessions.remove(gui.getPlayer().getUniqueId(), gui);
    }

    boolean closeCurrent(Player player) {
        ArcMenuGUI gui = sessions.get(player.getUniqueId());
        if (gui == null) {
            return false;
        }
        gui.closeGUI();
        return true;
    }

    void closePlayer(Player player) {
        ArcMenuGUI gui = sessions.get(player.getUniqueId());
        if (gui != null) {
            gui.shutdown();
        }
    }

    @Override
    public void close() {
        closing = true;
        for (ArcMenuGUI gui : new ArrayList<>(sessions.values())) {
            gui.shutdown();
        }
        handle.unregister();
        sessions.clear();
        pending.clear();
    }
    // END ARCMENU SHARED runtime

    // Plugin adapters: registration identity and required capabilities.

    private static JavaPlugin plugin() {
        return UltimateShop.instance;
    }

    private static ArcMenuApplicationOptions options() {
        return ArcMenuApplicationOptions.builder()
                .captureMouseScroll(true)
                .inheritCanvas(false)
                .canvas(new ArcMenuCanvas(
                        plugin().getConfig().getInt("menu.ui.layout.canvas.width", 648),
                        plugin().getConfig().getInt("menu.ui.layout.canvas.height", 396),
                        ArcMenuLayout.designPixelsPerBlock(plugin().getConfig().getDouble("menu.ui.renderers.arcmenu.screen-scale", 1.5)), 3))
                .build();
    }

    private static boolean supports(ArcMenuApi api) {
        if (api.capabilities().stream().noneMatch(capability -> capability.name().equals("INLINE_TEXT_INPUT"))) {
            ErrorManager.errorManager.sendErrorMessage("§cError: The built-in ArcMenu renderer requires native text input support; update ArcMenu");
            return false;
        }
        return true;
    }
}
