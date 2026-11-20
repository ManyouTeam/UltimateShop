package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.editor.EditorManager;
import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.arc.ArcMenuGUI;
import cn.superiormc.ultimateshop.gui.Prompt;
import cn.superiormc.ultimateshop.gui.PromptUtil;
import cn.superiormc.ultimateshop.gui.inv.CommonGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommandUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MenuStatusManager extends AbstractManager {

    public static MenuStatusManager menuStatusManager;

    private static final int MAX_GUI_HISTORY_SIZE = 32;

    private final Map<UUID, Prompt> prompts = new ConcurrentHashMap<>();

    private final Map<UUID, GUIStatus> openGuis = new ConcurrentHashMap<>();

    private final Map<UUID, GUIHistory> guiHistories = new ConcurrentHashMap<>();

    private final Set<UUID> returningBack = ConcurrentHashMap.newKeySet();

    public MenuStatusManager() {
        menuStatusManager = this;
    }

    public GUIStatus getGUIStatus(Player player) {
        if (player == null) {
            return null;
        }
        return openGuis.get(player.getUniqueId());
    }

    public AbstractGUI getOpeningGUI(Player player) {
        GUIStatus guiStatus = getGUIStatus(player);
        if (guiStatus == null) {
            return null;
        }
        return guiStatus.getGUI();
    }

    public ObjectMenu getOpeningMenu(Player player) {
        AbstractGUI gui = getOpeningGUI(player);
        if (gui == null) {
            return null;
        }
        return gui.getMenu();
    }

    public boolean hasOpeningGUI(Player player) {
        return getGUIStatus(player) != null;
    }

    public void setGUIStatus(Player player, GUIStatus guiStatus) {
        if (player == null) {
            return;
        }
        if (guiStatus == null) {
            openGuis.remove(player.getUniqueId());
            return;
        }
        openGuis.put(player.getUniqueId(), guiStatus);
    }

    public void removeGUIStatus(Player player) {
        if (player == null) {
            return;
        }
        openGuis.remove(player.getUniqueId());
    }

    public boolean canOpenGUI(Player player, AbstractGUI gui, boolean reopen) {
        if (player == null || gui == null) {
            return false;
        }
        GUIStatus guiStatus = getGUIStatus(player);
        if (guiStatus != null && guiStatus.getGUI() instanceof ArcMenuGUI arcMenu
            && !arcMenu.isActive()
            && guiStatus.getStatus() != GUIStatus.Status.ALREADY_IN_COOLDOWN) {
            removeGUIStatus(player);
            guiStatus = null;
        }
        if (!reopen && guiStatus != null && ConfigManager.configManager.getLong("menu.cooldown.reopen", -1L) > 0L) {
            return false;
        }
        UUID uuid = player.getUniqueId();
        if (returningBack.remove(uuid)) {
            setGUIStatus(player, GUIStatus.of(gui,
                reopen ? GUIStatus.Status.ACTION_OPEN_MENU : GUIStatus.Status.CAN_REOPEN));
            return true;
        }
        if (guiStatus != null) {
            guiHistories.computeIfAbsent(uuid, ignored -> new GUIHistory())
                .record(guiStatus.getGUI(), gui);
        }
        setGUIStatus(player, GUIStatus.of(gui, reopen ? GUIStatus.Status.ACTION_OPEN_MENU : GUIStatus.Status.CAN_REOPEN));
        return true;
    }

    public boolean openPreviousGUI(Player player) {
        return openPreviousGUI(player, null);
    }

    public boolean openPreviousGUI(Player player, String fallbackMenu) {
        return openPreviousGUI(player, fallbackMenu, null);
    }

    public boolean openPreviousGUI(Player player, String fallbackMenu, String fallbackShop) {
        if (player == null) {
            return false;
        }
        UUID uuid = player.getUniqueId();
        GUIHistory history = guiHistories.get(uuid);
        AbstractGUI current = getOpeningGUI(player);
        AbstractGUI previous = history == null ? null : history.pollPrevious(current);
        if (history != null && history.isEmpty()) {
            guiHistories.remove(uuid, history);
        }
        if (previous == null) {
            if (fallbackShop != null && !fallbackShop.trim().isEmpty()) {
                ObjectShop shop = ConfigManager.configManager.getShop(fallbackShop);
                if (shop == null || shop.getShopMenuObject() == null
                    || shop.getShopMenuObject().menuConfigs == null) {
                    return false;
                }
                returningBack.add(uuid);
                ShopGUI.openGUI(player, shop, false, true);
                return true;
            }
            if (fallbackMenu == null || fallbackMenu.trim().isEmpty()) {
                return false;
            }
            ObjectMenu menu = ObjectMenu.commonMenus.get(fallbackMenu);
            if (menu == null || menu.menuConfigs == null) {
                return false;
            }
            returningBack.add(uuid);
            CommonGUI.openGUI(player, fallbackMenu, false, true);
            return true;
        }
        returningBack.add(uuid);
        if (previous instanceof InvGUI invGUI) {
            invGUI.openGUI(true);
        } else if (previous instanceof FormGUI formGUI) {
            formGUI.openGUI(true);
        } else if (previous instanceof DialogGUI dialogGUI) {
            dialogGUI.openGUI(true);
        } else {
            previous.openGUI(true);
        }
        return true;
    }

    public void removeOpenGUIStatus(Player player, AbstractGUI gui) {
        if (player == null || gui == null) {
            return;
        }
        long time = ConfigManager.configManager.getLong("menu.cooldown.reopen", 3L);
        GUIStatus guiStatus = getGUIStatus(player);
        if (guiStatus == null || guiStatus.getGUI() != gui) {
            return;
        }
        if (time > 0L && guiStatus.getStatus() != GUIStatus.Status.ALREADY_IN_COOLDOWN) {
            setGUIStatus(player, GUIStatus.of(gui, GUIStatus.Status.ALREADY_IN_COOLDOWN));
            GUIStatus closingStatus = getGUIStatus(player);
            SchedulerUtil.runTaskLater(() -> openGuis.remove(player.getUniqueId(), closingStatus), time);
        }
    }

    public void clear(Player player) {
        if (player == null) {
            return;
        }
        cancelPrompt(player);
        CommandUtil.cancelGUIUpdate(player);
        UUID uuid = player.getUniqueId();
        EditorManager.editorManager.clear(player);
        openGuis.remove(uuid);
        guiHistories.remove(uuid);
        returningBack.remove(uuid);
    }

    public void clearForReload(Player player) {
        if (player == null) {
            return;
        }
        AbstractGUI opening = getOpeningGUI(player);
        if (opening instanceof ArcMenuGUI) {
            opening.closeGUI();
        }
        player.closeInventory();
        clear(player);
    }

    @Override
    public void onPluginReload() {
        Bukkit.getOnlinePlayers().forEach(this::clearForReload);
    }

    @Override
    public void onPluginDisable() {
        Bukkit.getOnlinePlayers().forEach(this::clearForReload);
    }

    public boolean hasPrompt(Player player) {
        return prompts.containsKey(player.getUniqueId());
    }

    public void startPrompt(Player player, Prompt prompt) {
        prompts.put(player.getUniqueId(), prompt);
        AbstractGUI opening = getOpeningGUI(player);
        if (opening instanceof ArcMenuGUI) {
            opening.closeGUI();
        }
        player.closeInventory();
        EditorLang.send(player, "editor.prompt.description", "&f{value}", "value", prompt.getDescription());
        EditorLang.send(player, "editor.prompt.cancel-tip", "&7Type &f{value} &7to abort this edit.",
            "value", PromptUtil.getCancelKeyword(player));
    }

    public void cancelPrompt(Player player) {
        Prompt prompt = prompts.remove(player.getUniqueId());
        if (prompt != null) {
            prompt.cancel(player);
        }
    }

    public boolean cancelPromptAndShouldReopen(Player player) {
        Prompt prompt = prompts.remove(player.getUniqueId());
        if (prompt == null) {
            return false;
        }
        prompt.cancel(player);
        return prompt.shouldReopenOnCancel();
    }

    public void submitPrompt(Player player, String input) {
        Prompt prompt = prompts.remove(player.getUniqueId());
        if (prompt != null) {
            prompt.handle(player, input);
        }
    }

    private static final class GUIHistory {

        private final Deque<AbstractGUI> entries = new ArrayDeque<>();

        private synchronized void record(AbstractGUI current, AbstractGUI next) {
            if (current == null || isSamePage(current, next)) {
                return;
            }
            if (!isSamePage(entries.peekLast(), current)) {
                entries.addLast(current);
            }
            while (entries.size() > MAX_GUI_HISTORY_SIZE) {
                entries.pollFirst();
            }
        }

        private synchronized AbstractGUI pollPrevious(AbstractGUI current) {
            AbstractGUI previous;
            do {
                previous = entries.pollLast();
            }
            while (previous != null && isSamePage(previous, current));
            return previous;
        }

        private synchronized boolean isEmpty() {
            return entries.isEmpty();
        }

        private static boolean isSamePage(AbstractGUI first, AbstractGUI second) {
            if (first == second) {
                return first != null;
            }
            if (first == null || second == null || first.getClass() != second.getClass()) {
                return false;
            }
            ObjectMenu firstMenu = first.getMenu();
            ObjectMenu secondMenu = second.getMenu();
            if (firstMenu == null || secondMenu == null) {
                return false;
            }
            return firstMenu == secondMenu || Objects.equals(firstMenu.getName(), secondMenu.getName());
        }
    }
}
