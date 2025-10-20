package cn.superiormc.ultimateshop.utils;

import cn.superiormc.mythicchanger.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketInventoryUtil extends PacketListenerAbstract {

    public static PacketInventoryUtil packetInventoryUtil;

    private final Map<UUID, Integer> WINDOW_IDS = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> WINDOW_TYPES = new ConcurrentHashMap<>();
    private final Map<UUID, SchedulerUtil> RUNNING_ANIMATIONS = new ConcurrentHashMap<>();


    public PacketInventoryUtil() {
        PacketEvents.getAPI().getEventManager().registerListener(this);
        packetInventoryUtil = this;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow wrapper = new WrapperPlayServerOpenWindow(event);
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();

            WINDOW_IDS.put(uuid, wrapper.getContainerId());
            WINDOW_TYPES.put(uuid, wrapper.getType());
        }
    }

    public void updateTitle(Player player, String newTitle, InvGUI gui) {
        UUID uuid = player.getUniqueId();
        Integer windowId = WINDOW_IDS.get(uuid);
        Integer windowType = WINDOW_TYPES.get(uuid);

        if (windowId == null || windowType == null) {
            return;
        }

        WrapperPlayServerOpenWindow packet = new WrapperPlayServerOpenWindow(windowId, windowType, PaperTextUtil.modernParse(newTitle));

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        if (ConfigManager.configManager.getBoolean("menu.title-update.resend-items-pack")) {
            ArrayList<ItemStack> items = new ArrayList<>();
            for (org.bukkit.inventory.ItemStack bukkitItem : gui.getInv().getContents()) {
                items.add(SpigotConversionUtil.fromBukkitItemStack(bukkitItem));
            }

            WrapperPlayServerWindowItems itemsPacket = new WrapperPlayServerWindowItems(windowId, 0, items, null);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, itemsPacket);
        }
        player.updateInventory();
    }

    public void clear(Player player) {
        UUID uuid = player.getUniqueId();
        WINDOW_IDS.remove(uuid);
        WINDOW_TYPES.remove(uuid);
        stopAnimation(player);
    }

    public void startAnimation(Player player, List<String> frames, long interval, InvGUI gui) {
        stopAnimation(player);

        if (frames == null || frames.isEmpty()) {
            return;
        }

        UUID uuid = player.getUniqueId();

        AtomicInteger index = new AtomicInteger(0);
        SchedulerUtil task = SchedulerUtil.runTaskTimer(() -> {
            if (!player.isOnline()) {
                stopAnimation(player);
                return;
            }

            gui.title = frames.get(index.getAndIncrement() % frames.size());
            updateTitle(player, gui.title, gui);
        }, 0L, interval);
        RUNNING_ANIMATIONS.put(uuid, task);
    }

    public void stopAnimation(Player player) {
        SchedulerUtil task = RUNNING_ANIMATIONS.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

}
