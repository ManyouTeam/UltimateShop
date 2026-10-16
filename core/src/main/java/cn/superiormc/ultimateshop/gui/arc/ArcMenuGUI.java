package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.gui.MenuUILayout;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.managers.MinecraftResourceManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import com.fentai.arcmenu.api.ArcMenuActivateEvent;
import com.fentai.arcmenu.api.ArcMenuApplicationCloseEvent;
import com.fentai.arcmenu.api.ArcMenuApplicationContext;
import com.fentai.arcmenu.api.ArcMenuApplicationSession;
import com.fentai.arcmenu.api.ArcMenuPoint;
import com.fentai.arcmenu.api.ArcMenuPointerButton;
import com.fentai.arcmenu.api.ArcMenuPointerMode;
import com.fentai.arcmenu.api.ArcMenuPointerMoveEvent;
import com.fentai.arcmenu.api.ArcMenuScrollEvent;
import com.fentai.arcmenu.api.ArcMenuSurface;
import com.fentai.arcmenu.api.ArcMenuSurfaceEvent;
import com.fentai.arcmenu.api.ArcMenuTickEvent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ArcMenuGUI extends AbstractGUI {

    private static final float ITEM_DEPTH_SCALE = .01f;

    private static final Pattern VANILLA_TRANSLATION =
        Pattern.compile("<(?:lang|translate|translatable):((?:item|block)\\.minecraft\\.[a-z0-9_.]+)>");

    protected final ObjectMenu menu;

    protected final boolean bypass;

    private final ArcMenuRuntime runtime;

    protected final MenuUILayout layout;

    protected Map<Integer, AbstractButton> menuButtons = new LinkedHashMap<>();

    protected final Map<Integer, ItemStack> menuItems = new LinkedHashMap<>();

    protected String title;

    protected ArcMenuApplicationContext context;

    private double cursorDepth;

    private ArcMenuSurface surface;

    private final Map<String, Node> nodes = new LinkedHashMap<>();

    private final Set<String> used = new HashSet<>();

    private final List<Hit> hits = new ArrayList<>();

    protected Map<Integer, AbstractButton> buttons = Map.of();

    private Hit hovered;

    private int page;

    private int pageCount = 1;

    private long nextClick;

    private int age;

    private int closing;

    private boolean finished;

    private boolean active;

    protected ArcMenuGUI(Player player, ObjectMenu menu, boolean bypass, ArcMenuRuntime runtime) {
        super(player);
        this.menu = menu;
        this.bypass = bypass;
        this.runtime = runtime;
        this.layout = new MenuUILayout(menu.getConfig());
    }

    boolean tryOpen(boolean reopen) {
        if (!layout.isValid()) {
            return false;
        }
        GUIStatus previous = MenuStatusManager.menuStatusManager.getGUIStatus(player);
        if (!MenuStatusManager.menuStatusManager.canOpenGUI(player, this, reopen)) {
            return true;
        }
        try {
            if (!canDisplayMenu()) {
                restore(previous);
                return true;
            }
            constructGUI();
            player.closeInventory();
            if (runtime.open(this) && isActive()) {
                getMenu().doOpenAction(player, reopen);
                return true;
            }
            ErrorManager.errorManager.sendErrorMessage(
                "§cArcMenu did not attach menu "
                + getMenu().getName()
                + "; falling back to Inventory");
        } catch (RuntimeException | LinkageError exception) {
            ErrorManager.errorManager.sendErrorMessage(
                "§cCannot open ArcMenu menu "
                + getMenu().getName()
                + ": "
                + exception.getMessage());
        }
        restore(previous);
        return false;
    }

    private void restore(GUIStatus previous) {
        if (MenuStatusManager.menuStatusManager.getOpeningGUI(player) == this) {
            MenuStatusManager.menuStatusManager.setGUIStatus(player, previous);
        }
    }

    ArcMenuApplicationSession attach(ArcMenuApplicationContext context) {
        this.context = context;
        cursorDepth = runtime.cursorDepth();
        surface = context.surface();
        active = true;
        finished = false;
        age = 0;
        closing = 0;
        prepareLanguage();
        return new ArcMenuApplicationSession() {
            @Override
            public void onSurfaceChanged(ArcMenuSurfaceEvent event) {
                // ArcMenu destroys the old tracked displays when reanchoring its surface.
                if (!event.getInitial()) {
                    nodes.clear();
                }
                surface = event.getSurface();
                render();
            }

            @Override
            public void onPointerMove(ArcMenuPointerMoveEvent event) {
                pointer(event.getPoint());
            }

            @Override
            public void onActivate(ArcMenuActivateEvent event) {
                activate(event);
            }

            @Override
            public void onScroll(ArcMenuScrollEvent event) {
                if (!usable() || event.getSteps() == 0) {
                    return;
                }
                if (!handleScroll(event, hit(event.getPoint()))) {
                    page = Math.max(0, Math.min(pageCount - 1,
                        page + Integer.signum(event.getSteps())));
                    render();
                }
            }

            @Override
            public void onTick(ArcMenuTickEvent event) {
                tick();
            }

            @Override
            public void onClose(ArcMenuApplicationCloseEvent event) {
                active = false;
                nodes.clear();
                hits.clear();
                hovered = null;
                finishGUI();
            }
        };
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }

    public boolean isActive() {
        return active && context != null && context.isActive();
    }

    protected boolean canDisplayMenu() {
        return bypass || menu.getCondition().getAllBoolean(new ObjectThingRun(player));
    }

    protected void prepareMenu() {
        title = menu.getString("title", "Shop");
        menuButtons = new LinkedHashMap<>(menu.getMenu(MenuSender.of(player, "arcmenu")));
        menuItems.clear();
        for (Map.Entry<Integer, AbstractButton> entry : menuButtons.entrySet()) {
            menuItems.put(entry.getKey(), entry.getValue().getDisplayItem(player, 1).getItemStack());
        }
    }

    @Override
    public void constructGUI() {
        if (!canDisplayMenu()) {
            if (active) {
                closeGUI();
            }
            return;
        }
        prepareMenu();
        if (active) {
            render();
        }
    }

    @Override
    public void updateGUI() {
        if (usable()) {
            constructGUI();
        }
    }

    @Override
    public void openGUI(boolean reopen) {
        tryOpen(reopen);
    }

    @Override
    public void closeGUI() {
        if (context != null && active) {
            context.close();
        } else {
            finishGUI();
        }
    }

    @Override
    public void finishGUI() {
        if (finished) {
            return;
        }
        finished = true;
        super.finishGUI();
        if (ConfigManager.configManager.getLong("menu.cooldown.reopen", 3L) <= 0
            && MenuStatusManager.menuStatusManager.getOpeningGUI(player) == this) {
            MenuStatusManager.menuStatusManager.removeGUIStatus(player);
        }
    }

    final boolean usable() {
        return active
            && closing == 0
            && context.isActive()
            && player.isOnline()
            && MenuStatusManager.menuStatusManager.getOpeningGUI(player) == this;
    }

    private void tick() {
        if (!active || !context.isActive()) {
            return;
        }
        age++;
        if (closing > 0) {
            if (++closing >= animationDuration() + 1) {
                context.close();
                return;
            }
            animate();
            return;
        }
        if (animations() && (age <= animationDuration() + 10 || hasDetails() && age % 4 == 0)) {
            animate();
        }
        int updateInterval = Math.max(5,
            ConfigManager.configManager.getInt("menu.ui.renderers.arcmenu.update-interval", 20));
        if (age % updateInterval == 0 && usable()) {
            constructGUI();
        }
        // A command can open another presentation while ArcMenu still owns the camera.
        if (MenuStatusManager.menuStatusManager.getOpeningGUI(player) != this) {
            closeGUI();
        }
    }

    private int animationDuration() {
        return (int) number("animation.duration", 8, 1, 40);
    }

    private String themeTextColor() {
        String hex = String.format("%06x", color("text", 0xFFFFFFFF) & 0xFFFFFF);
        StringBuilder result = new StringBuilder();
        for (char digit : hex.toCharArray()) {
            result.append('§').append(digit);
        }
        return result.toString();
    }

    private boolean animations() {
        return getMenu().getConfig().getBoolean("ui.renderers.arcmenu.animation.enabled", true);
    }

    private void requestClose() {
        clearTooltip();
        if (!animations()) {
            closeGUI();
            return;
        }
        closing = 1;
        context.hideTooltip();
        context.setCursorInteractive(false);
    }

    private void activate(ArcMenuActivateEvent event) {
        if (!usable() || System.nanoTime() < nextClick) {
            return;
        }
        Hit hit = hit(event.getPoint());
        if (hit == null) {
            return;
        }
        nextClick = System.nanoTime()
            + Math.max(0, ConfigManager.configManager.getLong("menu.cooldown.click", 5L)) * 50_000_000L;
        if (!canDisplayMenu()) {
            closeGUI();
            return;
        }
        ClickType type = event.getButton() == ArcMenuPointerButton.RIGHT
            ? (event.getSneaking() ? ClickType.SHIFT_RIGHT : ClickType.RIGHT)
            : event.getButton() == ArcMenuPointerButton.MIDDLE
                ? ClickType.MIDDLE
                : (event.getSneaking() ? ClickType.SHIFT_LEFT : ClickType.LEFT);
        hit.action.accept(type);
        if (usable()) {
            render();
        }
    }

    private Hit hit(ArcMenuPoint point) {
        if (point == null) {
            return null;
        }
        double x = point.getX() / canvasScale() + layout.getWidth() / 2.0;
        double y = layout.getHeight() / 2.0 - point.getY() / canvasScale();
        for (int i = hits.size() - 1; i >= 0; i--) {
            Hit hit = hits.get(i);
            if (!acceptsHit(hit.id)) {
                continue;
            }
            if (ArcMenuLayout.contains(x, y, hit.x, hit.y, hit.w, hit.h)) {
                return hit;
            }
        }
        return null;
    }

    private void pointer(ArcMenuPoint point) {
        if (!usable()) {
            return;
        }
        Hit next = hit(point);
        if (hovered != next) {
            if (hovered != null) {
                highlight(hovered, false);
            }
            hovered = next;
            if (next != null) {
                highlight(next, true);
            }
        }
        context.setCursorInteractive(next != null);
        context.hideTooltip();
        if (next != null && !next.tooltip.isEmpty()) {
            showTooltip(point, next.tooltip);
        } else {
            clearTooltip();
        }
    }

    private void highlight(Hit hit, boolean on) {
        Node node = nodes.get(hit.id + ":panel");
        if (node != null) {
            ((TextDisplay) node.display)
                .setBackgroundColor(
                Color.fromARGB(
                on ? buttonColor(hit.id, true) : buttonColor(hit.id, false)));
        }
    }

    private void showTooltip(ArcMenuPoint point, List<String> content) {
        if (!getMenu().getConfig().getBoolean("ui.renderers.arcmenu.tooltip.enabled", true)) {
            clearTooltip();
            return;
        }
        double size = number("tooltip.font-size", .9, .5, 1.2);
        double padding = number("tooltip.padding", 8, 4, 20);
        List<String> lines =
            ArcMenuLayout.wrap(
            content.stream().map(this::parse).toList(),
            (int) (number("tooltip.width", 260, 100, 450) / size));
        lines =
            lines.subList(
            0,
            Math.min(
            lines.size(),
            (int) ((layout.getHeight() - 24 - 2 * padding) / (10 * size))));
        double width =
            Math.max(
            70,
            lines.stream().mapToInt(ArcMenuLayout::textWidth).max().orElse(0) * size
            + 2 * padding);
        double height = lines.size() * 10 * size + 2 * padding;
        ArcMenuLayout.Box box =
            ArcMenuLayout.place(
            point.getX() / canvasScale() + layout.getWidth() / 2.0,
            layout.getHeight() / 2.0 - point.getY() / canvasScale(),
            width,
            height,
            layout.getWidth(),
            layout.getHeight());
        panel(
            "tooltip:edge",
            box.x - 1,
            box.y - 1,
            width + 2,
            height + 2,
            0xFF607B9E,
            ArcMenuLayout.TOOLTIP_DEPTH);
        panel(
            "tooltip:panel",
            box.x,
            box.y,
            width,
            height,
            0xFF0D1424,
            ArcMenuLayout.TOOLTIP_DEPTH + 2);
        label(
            "tooltip:text",
            box.x + padding,
            ArcMenuLayout.textAnchorY(box.y, height, size, padding),
            String.join("\n", lines),
            size,
            width - 2 * padding,
            ArcMenuLayout.TOOLTIP_DEPTH + 4);
    }

    final void clearTooltip() {
        for (String id : List.of("tooltip:edge", "tooltip:panel", "tooltip:text")) {
            Node node = nodes.get(id);
            if (node != null && node.visible) {
                clearDisplay(node.display);
                node.visible = false;
                node.lastTransform = null;
            }
        }
    }

    final void render() {
        if (!active || !context.isActive()) {
            return;
        }
        used.clear();
        hits.clear();
        hovered = null;
        int accent = color("accent", 0xFF65E8EF);
        renderHeader(accent);

        buttons = new LinkedHashMap<>(getMenu().getUiMenu(MenuSender.of(player, "arcmenu")));
        buttons.putAll(menuButtons);
        prepareRender();
        List<MenuUILayout.Entry> entries = new ArrayList<>();
        for (var entry : buttons.entrySet()) {
            AbstractButton button = entry.getValue();
            if (!shouldRenderSlot(entry.getKey())) {
                continue;
            }
            if (button.isVisibleInMenu("arcmenu")
                && button.canDisplay(MenuSender.of(player, "arcmenu"))) {
                entries.add(new MenuUILayout.Entry(
                    entry.getKey(), button.getButtonConfig(), isProductCard(entry.getKey())));
            }
        }
        List<MenuUILayout.Element> elements = layout.arrange(entries);
        if (!layout.isValid()) {
            closeGUI();
            return;
        }
        pageCount = Math.max(1,
            elements.stream().mapToInt(MenuUILayout.Element::page).max().orElse(0) + 1);
        page = Math.min(page, pageCount - 1);
        for (MenuUILayout.Element element : elements) {
            if (element.page() < 0 || element.page() == page) {
                renderElement(element, accent);
            }
        }
        renderPagination();
        renderOverlays(accent);
        // ArcMenu retains tracked entities until close. Reuse a bounded pool rather than
        // spawning new tracked entities every time a page or transient label returns.
        for (Map.Entry<String, Node> entry : nodes.entrySet()) {
            if (!used.contains(entry.getKey()) && entry.getValue().visible) {
                // ArcMenu owns the visibility grant. Hiding through UltimateShop does
                // not revoke that grant, so clear unused pooled displays themselves.
                clearDisplay(entry.getValue().display);
                entry.getValue().visible = false;
            }
        }
        pointer(context.pointer());
        animate();
    }

    protected abstract void renderElement(MenuUILayout.Element element, int accent);

    protected abstract boolean isProductCard(int slot);

    protected boolean shouldRenderSlot(int slot) {
        return true;
    }

    protected abstract void prepareRender();

    protected abstract void renderOverlays(int accent);

    protected abstract boolean hasDetails();

    protected abstract boolean acceptsHit(String id);

    protected abstract boolean handleScroll(ArcMenuScrollEvent event, Hit target);

    protected String headerText(String value) {
        return value;
    }

    final void addHit(String id, double x, double y, double w, double h,
                      List<String> tooltip, Consumer<ClickType> action) {
        hits.add(new Hit(id, x, y, w, h, tooltip, action));
    }

    private void renderHeader(int accent) {
        panel(
            "window",
            0,
            0,
            layout.getWidth(),
            layout.getHeight(),
            color("background", 0xEF0C1223),
            0);
        for (MenuUILayout.Area area : layout.getAreas().values()) {
            MenuUILayout.Box box = area.bounds();
            if (area.type().equals("stack")) {
                panel(
                    "area:" + area.name(),
                    box.x(),
                    box.y(),
                    box.width(),
                    box.height(),
                    color("sidebar", 0xDE111B30),
                    1);
            }
        }
        var header = layout.getUi().getConfigurationSection("layout.header");
        if (header == null) {
            return;
        }
        for (String id : header.getKeys(false)) {
            var settings = header.getConfigurationSection(id);
            if (settings == null || !settings.getBoolean("enabled", true)) {
                continue;
            }
            String value =
                settings.getString("text", "")
                .replace("{title}", title == null ? "" : title)
                .replace("{content}", getMenu().getString("content", ""));
            value = headerText(value);
            double size = settings.getDouble("font-size", .8);
            double width = settings.getDouble("width", 100);
            double x = settings.getDouble("x");
            String align = settings.getString("align", "left");
            int measured = ArcMenuLayout.textWidth(parse(value));
            if (align.equals("center")) {
                x += Math.max(0, (width - measured * size) / 2);
            } else if (align.equals("right")) {
                x += Math.max(0, width - measured * size);
            }
            label(
                "header:" + id,
                x,
                ArcMenuLayout.textCenterY(settings.getDouble("y"), 27, size),
                value,
                size,
                width);
        }
        panel("header:accent", 0, 0, layout.getWidth(), 2, accent, 2);
    }

    private void renderPagination() {
        if (pageCount <= 1) {
            return;
        }
        for (MenuUILayout.Element element : layout.getPaginationElements()) {
            MenuUILayout.Box box = element.bounds();
            String key = element.area().substring(11);
            if (key.equals("indicator")) {
                String title = (page + 1) + " / " + pageCount;
                label(
                    "pagination:indicator",
                    box.x()
                    + Math.max(
                    0,
                    (box.width() - ArcMenuLayout.textWidth(title) * .75) / 2),
                    ArcMenuLayout.textCenterY(box.y(), box.height(), .75),
                    title,
                    .75,
                    box.width() - 18);
            } else {
                button(
                    element.area(),
                    box.x(),
                    box.y(),
                    box.width(),
                    box.height(),
                    key.equals("previous") ? "‹" : "›",
                    List.of(),
                    type ->
                    page =
                    Math.max(
                    0,
                    Math.min(
                    pageCount - 1,
                    page + (key.equals("previous") ? -1 : 1))));
            }
        }
    }

    final String text(String key, String fallback) {
        return layout.getUi().getString("text." + key, fallback);
    }

    final double number(String path, double fallback, double minimum, double maximum) {
        String key = path.startsWith("cards.")
            ? "products.card." + path.substring(6) : "renderers.arcmenu." + path;
        double value = layout.getUi().getDouble(key, fallback);
        if (!Double.isFinite(value)) {
            return fallback;
        }
        return Math.max(minimum, Math.min(maximum, value));
    }

    final int color(String path, int fallback) {
        String themePath = switch (path) {
            case "button" -> "button.background";
            case "button-hover" -> "button.hover";
            case "button-border" -> "button.border";
            case "buy-button" -> "buy.background";
            case "buy-button-hover" -> "buy.hover";
            case "sell-button" -> "sell.background";
            case "sell-button-hover" -> "sell.hover";
            default -> path;
        };
        Object value = layout.getUi().get("theme." + themePath);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text && text.matches("#[a-fA-F0-9]{6}([a-fA-F0-9]{2})?")) {
            long parsed = Long.parseLong(text.substring(1), 16);
            return text.length() == 7 ? (int) parsed | 0xFF000000 : (int) parsed;
        }
        return fallback;
    }

    final String parse(String text) {
        String value = TextUtil.withPAPI(
            CommonUtil.parseLang(player, text == null ? "" : text), player);
        MinecraftResourceManager resources = MinecraftResourceManager.minecraftResourceManager;
        if (resources != null) {
            Matcher translations = VANILLA_TRANSLATION.matcher(value);
            StringBuilder localized = new StringBuilder();
            while (translations.find()) {
                String name = resources.getLocalizedName(translations.group(1), player.getLocale());
                translations.appendReplacement(
                    localized,
                    Matcher.quoteReplacement(name == null ? translations.group() : name));
            }
            translations.appendTail(localized);
            value = localized.toString();
        }
        return TextUtil.parse(value);
    }

    private void prepareLanguage() {
        MinecraftResourceManager resources = MinecraftResourceManager.minecraftResourceManager;
        if (resources == null) {
            return;
        }
        String locale = player.getLocale();
        resources.requestLanguage(locale).thenRun(() -> SchedulerUtil.runSync(player, () -> {
            if (player.isOnline() && usable() && locale.equals(player.getLocale())) {
                render();
            }
        }));
    }

    final List<String> lore(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return List.of();
        }
        List<String> lore = UltimateShop.methodUtil.getItemLore(stack.getItemMeta());
        return lore == null ? List.of() : lore.stream().map(this::parse).toList();
    }

    final int buttonColor(String id, boolean hovered) {
        String type = id.endsWith(":action:buy") || id.endsWith(":action:buy-or-sell")
            ? "buy-button" : id.endsWith(":action:sell") ? "sell-button" : "button";
        int fallback =
            type.equals("buy-button")
        ? 0xFF21674D
        : type.equals("sell-button") ? 0xFF285A89 : 0xFF354861;
        return color(type + (hovered ? "-hover" : ""), hovered ? 0xFF4D789C : fallback);
    }

    final void button(
        String id,
        double x,
        double y,
        double w,
        double h,
        String title,
        List<String> tooltip,
        Consumer<ClickType> action) {
        panel(id + ":edge", x - 1, y - 1, w + 2, h + 2, color("button-border", 0xFF6B829F), 8);
        panel(id + ":panel", x, y, w, h, buttonColor(id, false), 9);
        double padding = Math.min(number("cards.padding", 9, 3, 16), w / 4);
        double fontSize = number("cards.button-font-size", 1, .5, 1.5);
        String align =
            id.startsWith("pagination:")
            || id.contains(":action:")
            || id.startsWith("more:")
            || id.equals("details:close")
            || id.equals("minus")
            || id.equals("plus")
        ? "center"
        : "left";
        if (id.matches("slot:[0-9]+")) {
            AbstractButton sourceButton = buttons.get(Integer.parseInt(id.substring(5)));
            if (sourceButton != null && sourceButton.getButtonConfig() != null) {
                align = sourceButton.getButtonConfig().getString("ui.text-align", align);
                fontSize =
                    Math.max(
                    .5,
                    Math.min(
                    1.5,
                    sourceButton
                    .getButtonConfig()
                    .getDouble("ui.font-size", fontSize)));
            }
        }
        double labelX = x + padding;
        double measured = ArcMenuLayout.textWidth(parse(title)) * fontSize;
        if (align.equals("center")) {
            labelX = x + Math.max(padding, (w - measured) / 2);
        } else if (align.equals("right")) {
            labelX = x + Math.max(padding, w - padding - measured);
        }
        label(
            id + ":text",
            labelX,
            ArcMenuLayout.textCenterY(y, h, fontSize),
            title,
            fontSize,
            w - 2 * padding,
            10);
        hits.add(new Hit(id, x, y, w, h, tooltip, action));
    }

    final void panel(String id, double x, double y, double w, double h, int color, double depth) {
        Node node = node(id, TextDisplay.class, x + w / 2, y + h / 2, depth);
        TextDisplay display = (TextDisplay) node.display;
        display.setText(" ");
        display.setBackgroundColor(Color.fromARGB(color));
        // Calibrate native TextDisplay world units to the ArcMenu logical canvas.
        node.scale =
            new Vector3f(
            ArcMenuLayout.panelScaleX(w, pixelsPerBlock()),
            ArcMenuLayout.panelScaleY(h, pixelsPerBlock()),
            1);
        node.panelWidth = w;
        node.panelHeight = h;
        transform(node, 1);
    }

    final void label(String id, double x, double y, String value, double size, double width) {
        label(id, x, y, value, size, width, 7);
    }

    final void label(
        String id, double x, double y, String value, double size, double width, double depth) {
        Node node = node(id, TextDisplay.class, x, y, depth);
        TextDisplay display = (TextDisplay) node.display;
        int lineWidth = Math.max(1, (int) (width / size));
        String[] lines = ("§x" + themeTextColor() + parse(value)).split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            lines[i] = ArcMenuLayout.ellipsize(lines[i], lineWidth);
        }
        display.setText(String.join("\n", lines));
        display.setAlignment(TextDisplay.TextAlignment.LEFT);
        display.setBackgroundColor(Color.fromARGB(0));
        display.setLineWidth(lineWidth);
        // Minecraft's native font pixel is 0.025 world units before display scaling.
        node.scale = new Vector3f(ArcMenuLayout.textScale(size, pixelsPerBlock()));
        node.leftAligned = true;
        node.textWidth =
            Math.min(display.getLineWidth(), ArcMenuLayout.textWidth(display.getText()));
        transform(node, 1);
    }

    final void item(String id, double x, double y, double size, ItemStack stack, double depth) {
        Node node = node(id, ItemDisplay.class, x, y, depth);
        ItemDisplay display = (ItemDisplay) node.display;
        ItemStack icon = stack.clone();
        icon.setAmount(1);
        display.setItemStack(icon);
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
        float iconScale = (float) ArcMenuLayout.worldUnits(size, pixelsPerBlock());
        node.scale = new Vector3f(iconScale, iconScale, iconScale * ITEM_DEPTH_SCALE);
        transform(node, 1);
    }

    private <T extends Display> Node node(
        String id, Class<T> type, double x, double y, double depth) {
        used.add(id);
        if (id.startsWith("details")
            || id.startsWith("buy:")
            || id.startsWith("sell:")
            || id.startsWith("plus:")
            || id.startsWith("minus:")
            || id.equals("amount")) {
            depth += 20;
        }
        Node node = nodes.get(id);
        boolean tooltip = id.startsWith("tooltip:");
        if (context.pointerMode() == ArcMenuPointerMode.MOUSE) {
            depth = ArcMenuLayout.cursorSafeDepth(depth, cursorDepth, canvasScale());
        }
        double perspectiveScale = ArcMenuLayout.overlayScale(
            depth, pixelsPerBlock(), surface.getCanvas().getDesignDistance());
        Location location = surface.toWorld(
            (x - layout.getWidth() / 2.0) * perspectiveScale * canvasScale(),
            (layout.getHeight() / 2.0 - y) * perspectiveScale * canvasScale(),
            depth * canvasScale());
        if (node == null) {
            T display = type.cast(context.spawnPrivate(
                location, type, (Consumer<T>) entity -> {
                    entity.setPersistent(false);
                    entity.setBillboard(Display.Billboard.FIXED);
                    entity.setBrightness(new Display.Brightness(15, 15));
                    // ArcMenu encodes mouse displays into shader space after each
                    // callback.
                    // Interpolating between encoded and ordinary matrices tears the
                    // layers.
                    entity.setInterpolationDuration(0);
                    if (entity instanceof TextDisplay text) {
                        text.setShadowed(false);
                        text.setDefaultBackground(false);
                        text.setSeeThrough(false);
                    }
                }));
            node = new Node(display);
            nodes.put(id, node);
        }
        if (!node.visible) {
            node.visible = true;
            node.hover = 1;
            node.lastTransform = null;
            node.lastMatrix = null;
        }
        if (!location.toVector().equals(node.location)) {
            node.display.teleport(location);
            node.location = location.toVector();
        }
        Vector right = surface.right();
        Vector up = surface.up();
        Vector normal = surface.normal();
        node.rotation = new Quaternionf().setFromNormalized(new Matrix3f(
            (float) right.getX(), (float) right.getY(), (float) right.getZ(),
            (float) up.getX(), (float) up.getY(), (float) up.getZ(),
            (float) normal.getX(), (float) normal.getY(), (float) normal.getZ()));
        // The plane basis already supplies rotation; clear Bukkit's location rotation.
        node.display.setRotation(0, 0);
        node.overlayScale = (float) perspectiveScale;
        node.tooltip = tooltip;
        return node;
    }

    private void animate() {
        float factor = closing > 0
            ? Math.max(.01f, 1 - (closing - 1) / (float) animationDuration())
            : animations()
                ? ArcMenuLayout.easeOut(Math.min(1, age / (float) animationDuration())) : 1;
        for (Node node : nodes.values()) {
            if (!node.visible) {
                continue;
            }
            float stagger = closing > 0 || !animations()
                ? factor : ArcMenuLayout.easeOut(
                    Math.max(0, Math.min(1, (age - node.delay) / (float) animationDuration())));
            transform(node, stagger);
        }
    }

    private void transform(Node node, float factor) {
        if (node.tooltip) {
            factor = 1;
        }
        float scale = Math.max(.01f, factor) * node.hover * node.overlayScale;
        Vector3f translation = new Vector3f();
        if (node.display instanceof TextDisplay text) {
            if (node.panelWidth > 0) {
                translation.x = -.0125f * node.scale.x * scale;
                translation.y = -.125f * node.scale.y * scale;
            } else {
                if (node.leftAligned) {
                    translation.x =
                        (float) ((node.textWidth / 2 - 1) * .025 * node.scale.x * scale);
                }
                translation.y = -.10f * node.scale.y * node.overlayScale;
            }
        }
        if (animations() && !node.tooltip) {
            translation.y += (float) ArcMenuLayout.worldUnits((1 - factor) * 8.1, pixelsPerBlock());
        }
        Quaternionf rotation = new Quaternionf(node.rotation);
        node.rotation.transform(translation);
        if (node.display instanceof ItemDisplay) {
            Matrix4f matrix =
                new Matrix4f()
                .translation(translation)
                .rotate(rotation)
                .scale(new Vector3f(node.scale).mul(scale))
                .rotateY((float) Math.PI);
            if (!matrix.equals(node.lastMatrix)) {
                node.display.setInterpolationDelay(0);
                node.display.setTransformationMatrix(matrix);
                node.lastMatrix = matrix;
            }
            return;
        }
        Transformation transformation =
            new Transformation(
            translation,
            rotation,
            new Vector3f(node.scale).mul(scale),
            new Quaternionf());
        if (!transformation.equals(node.lastTransform)) {
            node.display.setInterpolationDelay(0);
            node.display.setTransformation(transformation);
            node.lastTransform = transformation;
        }
        if (node.display instanceof TextDisplay text) {
            text.setTextOpacity((byte) Math.max(4, (int) (255 * factor)));
        }
    }

    final double canvasScale() {
        double global = ConfigManager.configManager.getDouble(
            "menu.ui.renderers.arcmenu.screen-scale", 1.5);
        double local = layout.getUi().getDouble("renderers.arcmenu.screen-scale", global);
        double fit = Math.min(
            surface.getCanvas().getWidth() / (double) layout.getWidth(),
            surface.getCanvas().getHeight() / (double) layout.getHeight());
        return fit * ArcMenuLayout.designPixelsPerBlock(global)
            / ArcMenuLayout.designPixelsPerBlock(local);
    }

    private double pixelsPerBlock() {
        return surface.getCanvas().getPixelsPerBlock() / canvasScale();
    }

    private static final class Node {

        private final Display display;

        private Vector3f scale = new Vector3f(1);

        private Quaternionf rotation = new Quaternionf();

        private float hover = 1;

        private boolean leftAligned;

        private double textWidth;

        private double panelWidth;

        private double panelHeight;

        private int delay;

        private Vector location;

        private boolean visible = true;

        private Transformation lastTransform;

        private Matrix4f lastMatrix;

        private float overlayScale = 1;

        private boolean tooltip;

        private Node(Display display) {
            this.display = display;
        }
    }

    static final class Hit {

        private final String id;

        private final double x;

        private final double y;

        private final double w;

        private final double h;

        private final List<String> tooltip;

        private final Consumer<ClickType> action;

        private Hit(
            String id,
            double x,
            double y,
            double w,
            double h,
            List<String> tooltip,
            Consumer<ClickType> action) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.tooltip = tooltip;
            this.action = action;
        }
    }

    private static void clearDisplay(Display display) {
        if (display instanceof TextDisplay text) {
            text.setText("");
            text.setBackgroundColor(Color.fromARGB(0));
            text.setTextOpacity((byte) 0);
        } else if (display instanceof ItemDisplay item) {
            item.setItemStack(new ItemStack(Material.AIR));
        }
    }
}
