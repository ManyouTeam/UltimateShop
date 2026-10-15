package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.MenuUILayout;
import cn.superiormc.ultimateshop.methods.GenerateDialogPack;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.dialog.DialogFontUtil;
import cn.superiormc.ultimateshop.objects.dialog.ObjectDialogPackUsage;
import cn.superiormc.ultimateshop.objects.dialog.ObjectDialogTemplate;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class DialogPackManager extends AbstractManager {

    public static DialogPackManager dialogPackManager;

    private final AtomicLong requests = new AtomicLong();

    private final Object generationLock = new Object();

    private volatile boolean stopped;

    private volatile ObjectDialogPackUsage publishedUsage;

    public DialogPackManager() {
        dialogPackManager = this;
    }

    @Override
    public void onInit() {
        regenerate(null, null);
    }

    @Override
    public void onPluginReload() {
        requests.incrementAndGet();
    }

    @Override
    public void onPluginDisable() {
        stopped = true;
        requests.incrementAndGet();
    }

    public static boolean usesOre(ConfigurationSection config) {
        return config != null
            && ConfigManager.configManager.getMenuUIMode(config, "java").equals("ore");
    }

    public static boolean showsItemIcon() {
        ConfigurationSection ore = ConfigManager.configManager.getSection("menu.ui.renderers.ore");
        return ore == null || ore.getBoolean("show-item-icon.enabled", true);
    }

    public static boolean hidesOneIconAmount() {
        ConfigurationSection ore = ConfigManager.configManager.getSection("menu.ui.renderers.ore");
        return ore == null || ore.getBoolean("show-item-icon.auto-hide-one", true);
    }

    public void regenerate(CommandSender sender, Integer requestedFormat) {
        long request = requests.incrementAndGet();
        if (UltimateShop.freeVersion
            || !UltimateShop.methodUtil.methodID().equals("paper")
            || !CommonUtil.getMinorVersion(21, 9)) {
            if (sender != null) {
                TextUtil.sendMessage(
                    sender instanceof Player player ? player : null,
                    TextUtil.pluginPrefix()
                    + " §eOre resource pack generation requires enabled Dialog menus on"
                    + " Paper 1.21.9+.");
            }
            return;
        }
        // Resolve mappings before collecting sprites, otherwise the first generated pack
        // would omit vanilla icons while the lazy mapping download is still running.
        boolean oreUsed =
            ObjectMenu.commonMenus.values().stream().anyMatch(menu -> usesOre(menu.menuConfigs))
            || ConfigManager.configManager.getShops().stream()
            .anyMatch(
            shop ->
            shop.getShopMenuObject() != null
            && usesOre(
            shop.getShopMenuObject()
            .menuConfigs));
        if (oreUsed && showsItemIcon()) {
            CompletableFuture<Map<String, String>> mapping =
                MinecraftResourceManager.minecraftResourceManager.requestMaterialMapping();
            if (!mapping.isDone()) {
                mapping.whenComplete(
                    (result, exception) -> {
                        if (stopped || requests.get() != request) {
                            return;
                        }
                        SchedulerUtil.runSync(
                            () -> {
                                if (stopped || requests.get() != request) {
                                    return;
                                }
                                if (exception == null) {
                                    regenerate(sender, requestedFormat);
                                } else {
                                    reportPreparationFailure(sender, exception);
                                }
                            });
                    });
                return;
            }
            if (mapping.isCompletedExceptionally()) {
                reportPreparationFailure(
                    sender, new IllegalStateException("Minecraft item mapping is unavailable"));
                return;
            }
        }
        ObjectDialogPackUsage usage = new ObjectDialogPackUsage();
        try {
            for (ObjectMenu menu : new ArrayList<>(ObjectMenu.commonMenus.values())) {
                if (usesOre(menu.menuConfigs)) {
                    collectMenu(menu, usage, false);
                }
            }
            for (ObjectShop shop : ConfigManager.configManager.getShops()) {
                ObjectMenu menu = shop.getShopMenuObject();
                if (menu != null && usesOre(menu.menuConfigs)) {
                    collectMenu(menu, usage, true);
                }
            }
            if (usage.getTemplates().isEmpty()) {
                if (sender != null) {
                    TextUtil.sendMessage(
                        sender instanceof Player player ? player : null,
                        TextUtil.pluginPrefix()
                        + " §eNo loaded menu uses ui.mode.java: ore; pack generation"
                        + " skipped.");
                }
                return;
            }
        } catch (Exception exception) {
            String message =
                "§cError: Failed to prepare Ore resource pack: " + exception.getMessage();
            ErrorManager.errorManager.sendErrorMessage(message);
            if (sender instanceof Player player) {
                TextUtil.sendMessage(player, TextUtil.pluginPrefix() + " " + message);
            }
            return;
        }
        Path data = UltimateShop.instance.getDataFolder().toPath();
        int configured = ConfigManager.configManager.getInt("menu.ui.renderers.ore.pack-format", 0);
        int format = requestedFormat != null ? requestedFormat : configured;
        SchedulerUtil.runTaskAsynchronously(
            () -> {
                synchronized (generationLock) {
                    if (stopped || requests.get() != request) {
                        return;
                    }
                    try {
                        int resolved = format == 0 ? CommonUtil.getPackFormat() : format;
                        Path textures =
                            MinecraftResourceManager.minecraftResourceManager
                            .getTextureDirectory();
                        // Keep explicitly supplied textures in their established location.
                        cacheCustomIcons(
                            data.resolve("textures"), textures, usage.getTextures());
                        cacheVanillaIcons(textures, usage.getTextures());
                        if (stopped || requests.get() != request) {
                            return;
                        }
                        boolean published =
                            GenerateDialogPack.generateFolder(
                            data.resolve("pack"),
                            usage,
                            resolved,
                            textures,
                            () -> !stopped && requests.get() == request);
                        if (published) {
                            publishedUsage = usage;
                            String message =
                                TextUtil.pluginPrefix()
                            + " §aGenerated Ore resource pack in "
                                + data.resolve("pack")
                            + " (format "
                                + resolved
                            + "). Load the pack folder or merge pack/assets"
                                + " into your server pack.";
                            TextUtil.sendMessage(null, message);
                            if (sender instanceof Player player) {
                                SchedulerUtil.runSync(
                                    player, () -> TextUtil.sendMessage(player, message));
                            }
                        }
                    } catch (Exception exception) {
                        String message =
                            "§cError: Failed to generate Ore resource pack: "
                            + exception.getMessage();
                        SchedulerUtil.runSync(
                            () -> ErrorManager.errorManager.sendErrorMessage(message));
                        if (sender instanceof Player player) {
                            SchedulerUtil.runSync(
                                player,
                                () ->
                                TextUtil.sendMessage(
                                player,
                                TextUtil.pluginPrefix() + " " + message));
                        }
                    }
                }
            });
    }

    private void collectMenu(ObjectMenu menu, ObjectDialogPackUsage usage, boolean shop) {
        MenuUILayout layout = new MenuUILayout(menu.getConfig());
        if (!layout.isValid()) {
            return;
        }
        for (Map.Entry<Integer, List<AbstractButton>> entry :
            menu.getPossibleUiButtons("ore").entrySet()) {
            for (AbstractButton button : entry.getValue()) {
                MenuUILayout.Entry candidate =
                    new MenuUILayout.Entry(
                    entry.getKey(),
                    button.getButtonConfig(),
                    button instanceof ObjectItem);
                for (MenuUILayout.Element element : layout.arrange(List.of(candidate))) {
                    ObjectDialogTemplate template =
                        ObjectDialogTemplate.fromUi(
                        menu.getConfig(), element, button.getButtonConfig());
                    if (!template.isValid()) {
                        continue;
                    }
                    Set<Integer> offsets = getVerticalOffsets(template, layout);
                    boolean card = element.appearance().equals("card");
                    usage.addBackground(template, card, true, offsets);
                    usage.addBackground(template, card, false, offsets);
                    if (card && showsItemIcon()) {
                        if (button instanceof ObjectItem item) {
                            for (String sprite : item.getDisplayItemObject().getPossibleSprites()) {
                                usage.addIcon(
                                    template, DialogFontUtil.getIconTexture(sprite), offsets);
                            }
                        } else {
                            usage.collectDisplayIcons(
                                template,
                                button.getButtonConfig()
                                .getConfigurationSection("display-item"),
                                offsets);
                        }
                    }
                }
            }
        }
        for (MenuUILayout.Element element : layout.getPaginationElements()) {
            ObjectDialogTemplate template = ObjectDialogTemplate.fromUi(menu.getConfig(), element);
            usage.addBackground(template, false, true, getVerticalOffsets(template, layout));
            usage.addBackground(template, false, false, getVerticalOffsets(template, layout));
        }
        ConfigurationSection header = layout.getUi().getConfigurationSection("layout.header");
        if (header != null) {
            for (String key : header.getKeys(false)) {
                if (key.equals("title")) {
                    continue;
                }
                ConfigurationSection settings = header.getConfigurationSection(key);
                if (settings == null || !settings.getBoolean("enabled", true)) {
                    continue;
                }
                MenuUILayout.Element element =
                    new MenuUILayout.Element(
                    -1,
                    "header:" + key,
                    "button",
                    new MenuUILayout.Box(
                    settings.getInt("x"),
                    settings.getInt("y"),
                    settings.getInt("width", 108),
                    27),
                    -1);
                ObjectDialogTemplate template =
                    ObjectDialogTemplate.fromUi(menu.getConfig(), element);
                usage.addBackground(template, false, true, getVerticalOffsets(template, layout));
                usage.addBackground(template, false, false, getVerticalOffsets(template, layout));
            }
        }
    }

    private Set<Integer> getVerticalOffsets(ObjectDialogTemplate template, MenuUILayout layout) {
        Set<Integer> offsets = new HashSet<>();
        for (int position = 0;
            position + template.getHeight() <= layout.getHeight();
            position += 9) {
            offsets.add(position);
        }
        return offsets;
    }

    public boolean hasIcon(String id, int position) {
        ObjectDialogPackUsage usage = publishedUsage;
        return usage != null && usage.hasIcon(id, position);
    }

    private void reportPreparationFailure(CommandSender sender, Throwable exception) {
        String message = "§cUnable to prepare Ore resources: " + exception.getMessage();
        ErrorManager.errorManager.sendErrorMessage(message);
        if (sender instanceof Player player) {
            TextUtil.sendMessage(player, TextUtil.pluginPrefix() + " " + message);
        }
    }

    private void cacheCustomIcons(Path supplied, Path cache, Set<String> icons) throws IOException {
        for (String icon : icons) {
            if (icon.startsWith("minecraft:")) {
                continue;
            }
            String id = DialogFontUtil.getIconId(icon) + ".png";
            Path source = supplied.resolve(id).normalize();
            Path destination = cache.resolve(id).normalize();
            if (!source.startsWith(supplied) || !destination.startsWith(cache)) {
                throw new IOException("Invalid icon path");
            }
            if (Files.isRegularFile(source)) {
                Files.createDirectories(destination.getParent());
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void cacheVanillaIcons(Path textures, Set<String> icons) throws Exception {
        Map<String, Path> requested = new TreeMap<>();
        for (String icon : icons) {
            requested.put(icon, textures.resolve(DialogFontUtil.getIconId(icon) + ".png"));
        }
        MinecraftResourceManager.minecraftResourceManager.cacheTextures(requested);
    }
}
