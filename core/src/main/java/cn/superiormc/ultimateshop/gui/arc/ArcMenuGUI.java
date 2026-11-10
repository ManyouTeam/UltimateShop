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

public abstract class ArcMenuGUI extends AbstractGUI {

    private static final float ITEM_DEPTH_SCALE = .01f;

    protected final ObjectMenu menu;

    protected final boolean bypass;

    private final ArcMenuRuntime runtime;

    protected final MenuUILayout layout;

    protected Map<Integer, AbstractButton> menuButtons = new LinkedHashMap<>();

    protected final Map<Integer, ItemStack> menuItems = new LinkedHashMap<>();

    protected String title;

    protected ArcMenuApplicationContext context;

    private double cursorDepth;

    private double cursorWidth = Double.POSITIVE_INFINITY;

    private double cursorHeight = Double.POSITIVE_INFINITY;

    protected ArcMenuSurface surface;

    protected ArcMenuPoint pointerPoint;

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

    private boolean stopped;

    private ArcMenuGUI sessionOwner = this;

    private ArcMenuGUI sessionView = this;

    private boolean skipOpeningAnimation;

    private final boolean titleBackgroundEnabled;

    private final int titleBackgroundColor;

    private final boolean titleDecorationEnabled;

    private final boolean decorationsEnabled;

    private final String decorationStyle;

    private final int decorationColor;

    private final int decorationCornerColor;

    private final double decorationThickness;

    private final double decorationCornerThickness;

    private final double decorationCornerLength;

    private final double decorationInset;

    private final boolean decorationDividersEnabled;

    protected ArcMenuGUI(Player player, ObjectMenu menu, boolean bypass, ArcMenuRuntime runtime) {
        super(player);
        this.menu = menu;
        this.bypass = bypass;
        this.runtime = runtime;
        this.layout = new MenuUILayout(menu.getConfig());
        titleBackgroundEnabled = layout.getUi().getBoolean("theme.title.background-enabled", false);
        titleBackgroundColor = color("title.background", color("background", 0xEF0C1223));
        titleDecorationEnabled = layout.getUi().getBoolean("theme.title.decoration-enabled", false);
        decorationsEnabled = layout.getUi().getBoolean("theme.decorations.enabled", true);
        String style = layout.getUi().getString("theme.decorations.style", "none");
        if (!List.of("frame", "corners", "both", "none").contains(style)) {
            ErrorManager.errorManager.sendErrorMessage("§cError: " + menu.getName()
                + ": ui.theme.decorations.style must be frame, corners, both or none");
            style = "none";
        }
        decorationStyle = style;
        decorationColor = color("decorations.color", color("accent", 0xFF65E8EF));
        decorationCornerColor = color("decorations.corner-color", color("accent", 0xFF65E8EF));
        decorationThickness = number("theme.decorations.thickness", 2, .5, 6);
        decorationCornerThickness = number("theme.decorations.corner-thickness", 2, .5, 8);
        decorationCornerLength = number("theme.decorations.corner-length", 20, 0, 80);
        decorationInset = number("theme.decorations.inset", 10, 0, 30);
        decorationDividersEnabled = layout.getUi().getBoolean("theme.decorations.dividers-enabled", true);
    }

    boolean tryOpen(boolean reopen) {
        if (!layout.isValid()) {
            return false;
        }
        GUIStatus previous = MenuStatusManager.menuStatusManager.getGUIStatus(player);
        skipOpeningAnimation = reopen && previous != null
            && previous.getGUI() instanceof ArcMenuGUI previousGUI
            && previousGUI.isActive() && previousGUI.getMenu().getName().equals(menu.getName());
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
                "§cError: ArcMenu did not attach menu "
                + getMenu().getName()
                + "; falling back to Inventory");
        } catch (RuntimeException | LinkageError exception) {
            ErrorManager.errorManager.sendErrorMessage(
                "§cError: Cannot open ArcMenu menu "
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

    @Override
    public ObjectMenu getMenu() {
        return menu;
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

    private void debug(String message) {
        if (ConfigManager.configManager.getBoolean("debug")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §7[ArcMenu Debug] player=" + player.getName()
                + ", menu=" + menu.getName() + ", " + message
                + ", active=" + active + ", contextActive=" + (context != null && context.isActive())
                + ", ownsGUI=" + (MenuStatusManager.menuStatusManager.getOpeningGUI(player) == this)
                + ", closing=" + closing + ", grounded=" + player.isOnGround()
                + ", mode=" + (context == null ? "none" : context.pointerMode())
                + ", velocity=" + player.getVelocity());
        }
    }

    private void tick() {
        if (!active || !context.isActive()) {
            return;
        }
        if (age % 100 == 0) {
            debug("tick, pointer=" + context.pointer() + ", hitCount=" + hits.size());
        }
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
        if (!usable()) {
            debug("click ignored: unusable, button=" + event.getButton() + ", point=" + event.getPoint());
            return;
        }
        if (System.nanoTime() < nextClick) {
            debug("click ignored: cooldown, button=" + event.getButton() + ", point=" + event.getPoint());
            return;
        }
        Hit hit = hit(event.getPoint());
        if (hit == null) {
            debug("click ignored: no hit, button=" + event.getButton() + ", point=" + event.getPoint()
                + ", hitCount=" + hits.size());
            return;
        }
        debug("click accepted, button=" + event.getButton() + ", hit=" + hit.id + ", point=" + event.getPoint());
        nextClick = System.nanoTime()
            + Math.max(0, ConfigManager.configManager.getLong("menu.cooldown.click", 5L)) * 50_000_000L;
        if (!canDisplayMenu()) {
            debug("click ignored: menu condition failed");
            closeGUI();
            return;
        }
        ClickType type = event.getButton() == ArcMenuPointerButton.RIGHT
            ? (event.getSneaking() ? ClickType.SHIFT_RIGHT : ClickType.RIGHT)
            : event.getButton() == ArcMenuPointerButton.MIDDLE
                ? ClickType.MIDDLE
                : (event.getSneaking() ? ClickType.SHIFT_LEFT : ClickType.LEFT);
        hit.action.accept(type);
        debug("click action completed, type=" + type + ", hit=" + hit.id);
        if (usable()) {
            render();
        }
    }

    private void highlight(Hit hit, boolean on) {
        Node node = nodes.get(hit.id + ":panel");
        if (node != null) {
            int background;
            if (nodes.containsKey(hit.id + ":icon")) {
                background = on ? color("card-hover", buttonColor(hit.id, true)) : color("card", 0xDC141E30);
            } else {
                background = buttonColor(hit.id, on);
            }
            setBackground(node, background);
            updateBevel(hit.id, background);
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
        renderHeader();

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
                clearDisplay(entry.getValue());
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

    private void renderHeader() {
        if (layout.getUi().getBoolean("theme.background-enabled", true)) {
            panel("window", 0, 0, layout.getWidth(), layout.getHeight(), color("background", 0xEF0C1223), 0);
        }
        if (decorationsEnabled) {
            renderDecorations("window-decoration", decorationInset, decorationInset,
                layout.getWidth() - decorationInset * 2, layout.getHeight() - decorationInset * 2);
        }
        for (MenuUILayout.Area area : layout.getAreas().values()) {
            MenuUILayout.Box box = area.bounds();
            String areaPath = "layout.areas." + area.name();
            if (layout.getUi().getBoolean(areaPath + ".background-enabled", area.type().equals("stack"))) {
                double padding = Math.max(0, layout.getUi().getDouble(areaPath + ".background-padding", 0));
                if (bevelsEnabled()) {
                    beveledPanel("area:" + area.name(), box.x() - padding, box.y() - padding,
                        box.width() + 2 * padding, box.height() + 2 * padding,
                        color("sidebar", 0xDE111B30), color("border", 0xFF25334D), 1);
                } else {
                    panel("area:" + area.name(), box.x() - padding, box.y() - padding,
                        box.width() + 2 * padding, box.height() + 2 * padding,
                        color("sidebar", 0xDE111B30), 1);
                }
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
                .replace("{content}", ConfigManager.configManager.getMenuContent(getMenu().getConfig(), "arcmenu"));
            value = headerText(value);
            if (settings.getBoolean("bold", false)) {
                value = "&l" + value;
            }
            double size = settings.getDouble("font-size", .8);
            double width = settings.getDouble("width", 100);
            double x = settings.getDouble("x");
            double y = settings.getDouble("y");
            double height = settings.getDouble("height", 27);
            if (id.equals("title")) {
                if (titleBackgroundEnabled) {
                    panel("header:title-background", x, y, width, height, titleBackgroundColor, 1);
                }
                if (titleDecorationEnabled && decorationsEnabled) {
                    renderDecorations("header:title-decoration", x, y, width, height);
                }
            }
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
                ArcMenuLayout.textCenterY(y, height, size),
                value,
                size,
                width);
        }
        if (decorationsEnabled && decorationDividersEnabled) {
            panel("header:accent", 0, 0, layout.getWidth(), decorationThickness, decorationColor, 2);
        }
    }

    private void renderDecorations(String id, double x, double y, double width, double height) {
        decorate(id, x, y, width, height, decorationStyle, decorationColor, decorationCornerColor,
            decorationThickness, decorationCornerThickness, decorationCornerLength);
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

    final boolean bevelsEnabled() {
        return layout.getUi().getBoolean("theme.bevel.enabled", false);
    }

    final String parse(String text) {
        String value = TextUtil.withPAPI(
            CommonUtil.parseLang(player, text == null ? "" : text), player);
        return TextUtil.parse(TextUtil.localizeVanillaNames(value, player));
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
        String type = buttonTheme(id);
        int fallback = hovered ? color("button-hover", 0xFF4D789C)
            : type.equals("buy") ? 0xFF21674D : type.equals("sell") ? 0xFF285A89
            : color("button", 0xFF354861);
        return color(type + (hovered ? ".hover" : ".background"), fallback);
    }

    private String buttonTheme(String id) {
        if (id.endsWith(":action:buy") || id.endsWith(":action:buy-or-sell")) {
            return "buy";
        }
        if (id.endsWith(":action:sell")) {
            return "sell";
        }
        if (id.equals("minus") || id.equals("plus")) {
            return "quantity";
        }
        if (id.startsWith("pagination:")) {
            return "pagination";
        }
        if (id.equals("extra:cancel")) {
            return "cancel";
        }
        return "button";
    }

    final String buttonText(String id, String title) {
        String textPath = buttonTheme(id) + ".text";
        return layout.getUi().contains("theme." + textPath)
            ? String.format("&#%06X", color(textPath, 0xFFFFFFFF) & 0xFFFFFF) + title : title;
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
        if (bevelsEnabled()) {
            beveledPanel(id, x, y, w, h, buttonColor(id, false), color("button-border", 0xFF6B829F), 8);
        } else {
            panel(id + ":edge", x - 1, y - 1, w + 2, h + 2, color("button-border", 0xFF6B829F), 8);
            panel(id + ":panel", x, y, w, h, buttonColor(id, false), 9);
        }
        double padding = Math.min(number("cards.padding", 9, 3, 16), w / 4);
        double fontSize = number("cards.button-font-size", 1, .5, 1.5);
        String align =
            id.startsWith("pagination:")
            || id.contains(":action:")
            || id.startsWith("more:")
            || id.equals("details:close")
            || id.equals("minus")
            || id.equals("plus")
            || id.startsWith("details:input:")
        ? "center"
        : "left";
        align = layout.getUi().getString("theme.button.text-align", align);
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
            buttonText(id, title),
            fontSize,
            w - 2 * padding,
            10);
        hits.add(new Hit(id, x, y, w, h, tooltip, action));
    }

    final void beveledPanel(String id, double x, double y, double width, double height,
                            int background, int border, double depth) {
        panel(id + ":shadow", x - 1, y + 2, width + 2, height + 1, color("bevel.shadow", 0xFF191A1B), depth);
        panel(id + ":edge", x - 1, y - 1, width + 2, height + 2, border, depth + .1);
        panel(id + ":panel", x, y, width, height, background, depth + .2);
        panel(id + ":bevel-top", x + 1, y, width - 2, 1,
            bevelColor(id, "top", 0x558E8F91, background), depth + .3);
        panel(id + ":bevel-left", x, y + 1, 1, height - 3,
            bevelColor(id, "left", 0x338E8F91, background), depth + .3);
        panel(id + ":bevel-right", x + width - 1, y + 1, 1, height - 3,
            bevelColor(id, "right", 0x33000000, background), depth + .3);
        panel(id + ":bevel-bottom", x, y + height - 2, width, 2,
            bevelColor(id, "bottom", 0x66000000, background), depth + .3);
    }

    private int bevelColor(String id, String side, int fallback, int background) {
        return ArcMenuLayout.compositeColor(
            color(buttonTheme(id) + ".bevel." + side, color("bevel." + side, fallback)), background);
    }

    private void updateBevel(String id, int background) {
        String[] sides = {"top", "left", "right", "bottom"};
        int[] defaults = {0x558E8F91, 0x338E8F91, 0x33000000, 0x66000000};
        for (int i = 0; i < sides.length; i++) {
            Node node = nodes.get(id + ":bevel-" + sides[i]);
            if (node != null) {
                setBackground(node, bevelColor(id, sides[i], defaults[i], background));
            }
        }
    }

    protected final void decorate(String id, double x, double y, double width, double height,
                                  String style, int color, int cornerColor, double thickness,
                                  double cornerThickness, double cornerLength) {
        if (style.equals("frame") || style.equals("both")) {
            panel(id + ":top", x, y, width, thickness, color, 1);
            panel(id + ":bottom", x, y + height - thickness, width, thickness, color, 1);
            panel(id + ":left", x, y, thickness, height, color, 1);
            panel(id + ":right", x + width - thickness, y, thickness, height, color, 1);
        }
        if (style.equals("corners") || style.equals("both")) {
            double length = Math.min(cornerLength, Math.min(width, height) / 2);
            double cornerWidth = Math.min(cornerThickness, length);
            if (length <= 0) {
                return;
            }
            for (int corner = 0; corner < 4; corner++) {
                double cornerX = x + (corner % 2 == 0 ? 0 : width - length);
                double cornerY = y + (corner / 2 == 0 ? 0 : height - cornerWidth);
                panel(id + ":corner:" + corner + ":horizontal", cornerX, cornerY, length, cornerWidth, cornerColor, 2);
                cornerX = x + (corner % 2 == 0 ? 0 : width - cornerWidth);
                cornerY = y + (corner / 2 == 0 ? 0 : height - length);
                panel(id + ":corner:" + corner + ":vertical", cornerX, cornerY, cornerWidth, length, cornerColor, 2);
            }
        }
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






    // Plugin adapters: configuration, assets and business lifecycle.

    private Object setting(String path, Object fallback) {
        String key = path.startsWith("theme.") ? path : path.startsWith("cards.")
                ? "products.card." + path.substring(6) : "renderers.arcmenu." + path;
        return layout.getUi().get(key, fallback);
    }

    private double getCanvasWidth() {
        return layout.getWidth();
    }

    private double getCanvasHeight() {
        return layout.getHeight();
    }

    private double getScreenScale() {
        return layout.getUi().getDouble("renderers.arcmenu.screen-scale",
                ConfigManager.configManager.getDouble("menu.ui.renderers.arcmenu.screen-scale", 1.5));
    }

    private String labelText(String value) {
        return "§x" + themeTextColor() + parse(value);
    }

    private ItemDisplay.ItemDisplayTransform itemTransform(ItemStack stack) {
        return ItemDisplay.ItemDisplayTransform.GUI;
    }

    private double nodeDepth(String id, double depth) {
        return id.startsWith("details") || id.startsWith("buy:") || id.startsWith("sell:")
                || id.startsWith("plus:") || id.startsWith("minus:") || id.equals("amount") ? depth + 20 : depth;
    }

    private double animationOffset(float factor) {
        return animations() ? (1 - factor) * 8.1 : 0;
    }

    private void initializeSession() {
        finished = false;
        age = skipOpeningAnimation ? animationDuration() + 10 : 0;
        closing = 0;
        prepareLanguage();
        debug("attached");
    }

    private void tickView() {
        tick();
    }

    private void sessionClosed() {
        finishGUI();
    }

    private void beforeReplace(ArcMenuGUI next) {
        finishGUI();
    }

    private void beforeShutdown() {
        finishGUI();
    }

    boolean canLeaveView() {
        return true;
    }

    boolean canReuseView(ArcMenuGUI next) {
        return true;
    }

    private void onHover(Hit previous, Hit next) {
        if (previous != null) {
            highlight(previous, false);
        }
        if (next != null) {
            highlight(next, true);
        }
    }

    private void scroll(ArcMenuScrollEvent event) {
        if (!usable() || event.getSteps() == 0) {
            return;
        }
        if (!handleScroll(event, hit(event.getPoint()))) {
            page = Math.max(0, Math.min(pageCount - 1, page + Integer.signum(event.getSteps())));
            render();
        }
    }

    // BEGIN ARCMENU SHARED configuration
    protected final double number(String path, double fallback, double minimum, double maximum) {
        Object configured = setting(path, fallback);
        double value = configured instanceof Number number ? number.doubleValue() : fallback;
        if (!Double.isFinite(value)) {
            return fallback;
        }
        return Math.max(minimum, Math.min(maximum, value));
    }

    protected final int color(String path, int fallback) {
        String key = switch (path) {
            case "button" -> "button.background";
            case "button-hover" -> "button.hover";
            case "button-border" -> "button.border";
            case "buy-button" -> "buy.background";
            case "buy-button-hover" -> "buy.hover";
            case "sell-button" -> "sell.background";
            case "sell-button-hover" -> "sell.hover";
            default -> path;
        };
        return parseColor(setting("theme." + key, null), fallback);
    }

    protected final int parseColor(Object value, int fallback) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text && text.matches("#[a-fA-F0-9]{6}([a-fA-F0-9]{2})?")) {
            long parsed = Long.parseLong(text.substring(1), 16);
            return text.length() == 7 ? (int) parsed | 0xFF000000 : (int) parsed;
        }
        return fallback;
    }
    // END ARCMENU SHARED configuration

    // BEGIN ARCMENU SHARED sessions
    ArcMenuApplicationSession attach(ArcMenuApplicationContext context) {
        this.context = context;
        sessionOwner = this;
        sessionView = this;
        surface = context.surface();
        cursorDepth = runtime.cursorDepth();
        cursorWidth = runtime.cursorExtent(player, false);
        cursorHeight = runtime.cursorExtent(player, true);
        active = true;
        stopped = false;
        age = 0;
        initializeSession();
        return new ArcMenuApplicationSession() {
            @Override
            public void onSurfaceChanged(ArcMenuSurfaceEvent event) {
                ArcMenuGUI gui = sessionView;
                if (!event.getInitial()) {
                    gui.nodes.clear();
                }
                gui.surface = event.getSurface();
                gui.render();
            }

            @Override
            public void onPointerMove(ArcMenuPointerMoveEvent event) {
                sessionView.pointer(event.getPoint());
            }

            @Override
            public void onActivate(ArcMenuActivateEvent event) {
                sessionView.activate(event);
            }

            @Override
            public void onScroll(ArcMenuScrollEvent event) {
                sessionView.scroll(event);
            }

            @Override
            public void onTick(ArcMenuTickEvent event) {
                ArcMenuGUI gui = sessionView;
                if (!gui.isActive()) {
                    return;
                }
                gui.age++;
                if (gui.age % 20 == 0 && context.pointerMode() == ArcMenuPointerMode.MOUSE) {
                    gui.cursorWidth = runtime.cursorExtent(player, false);
                    gui.cursorHeight = runtime.cursorExtent(player, true);
                }
                gui.tickView();
            }

            @Override
            public void onClose(ArcMenuApplicationCloseEvent event) {
                ArcMenuGUI gui = sessionView;
                gui.active = false;
                runtime.detached(gui);
                gui.nodes.clear();
                gui.hits.clear();
                gui.hovered = null;
                gui.sessionClosed();
            }
        };
    }

    public final boolean isActive() {
        return active && context != null && context.isActive();
    }

    final void replaceView(ArcMenuGUI next) {
        beforeReplace(next);
        next.context = context;
        next.surface = surface;
        next.cursorDepth = cursorDepth;
        next.cursorWidth = cursorWidth;
        next.cursorHeight = cursorHeight;
        next.pointerPoint = pointerPoint;
        next.age = age;
        next.sessionOwner = sessionOwner;
        next.nodes.putAll(nodes);
        nodes.clear();
        hits.clear();
        hovered = null;
        active = false;
        stopped = true;
        next.active = true;
        next.stopped = false;
        next.initializeSession();
        sessionOwner.sessionView = next;
        next.render();
    }

    void shutdown() {
        stopped = true;
        if (context == null || !context.execute(() -> {
            beforeShutdown();
            context.close();
        })) {
            beforeShutdown();
            sessionClosed();
        }
    }
    // END ARCMENU SHARED sessions

    // BEGIN ARCMENU SHARED pointer
    protected Hit hit(ArcMenuPoint point) {
        if (point == null) {
            return null;
        }
        double x = point.getX() / canvasScale() + getCanvasWidth() / 2;
        double y = getCanvasHeight() / 2 - point.getY() / canvasScale();
        for (int i = hits.size() - 1; i >= 0; i--) {
            Hit hit = hits.get(i);
            if (acceptsHit(hit.id) && ArcMenuLayout.contains(x, y, hit.x, hit.y, hit.w, hit.h)) {
                return hit;
            }
        }
        return null;
    }

    private void pointer(ArcMenuPoint point) {
        pointerPoint = point;
        if (!isActive()) {
            return;
        }
        Hit next = hit(point);
        if (hovered == null ? next != null : next == null || !hovered.id.equals(next.id)) {
            onHover(hovered, next);
        }
        hovered = next;
        context.setCursorInteractive(next != null);
        context.hideTooltip();
        renderTooltip();
    }

    private void renderTooltip() {
        if (hovered == null || hovered.tooltip.isEmpty() || pointerPoint == null
                || Boolean.FALSE.equals(setting("tooltip.enabled", true))) {
            clearTooltip();
            return;
        }
        double size = number("tooltip.font-size", 1.1, .5, 2);
        double padding = number("tooltip.padding", 8, 4, 20);
        double limit = Math.min(number("tooltip.width", 260, 100, 450), getCanvasWidth() - 24 - 2 * padding);
        List<String> content = new ArrayList<>();
        for (String line : hovered.tooltip) {
            content.add(parse(line));
        }
        List<String> lines = ArcMenuLayout.wrap(content, Math.max(1, (int) (limit / size)));
        size = Math.min(size, (getCanvasHeight() - 24 - 2 * padding) / (10 * Math.max(1, lines.size())));
        String text = String.join("\n§r", lines);
        double width = Math.max(40, Math.ceil(ArcMenuLayout.textWidth(text) * size) + 2 * padding);
        double height = lines.size() * 10 * size + 2 * padding;
        double[] position = ArcMenuLayout.place(pointerPoint.getX() / canvasScale() + getCanvasWidth() / 2,
                getCanvasHeight() / 2 - pointerPoint.getY() / canvasScale(), width, height,
                getCanvasWidth(), getCanvasHeight());
        panel("tooltip:edge", position[0] - 1, position[1] - 1, width + 2, height + 2,
                color("tooltip.border", 0xFF607B9E), ArcMenuLayout.TOOLTIP_DEPTH);
        panel("tooltip:panel", position[0], position[1], width, height,
                color("tooltip.background", 0xF0100010), ArcMenuLayout.TOOLTIP_DEPTH + 2);
        label("tooltip:text", position[0] + padding,
                ArcMenuLayout.textAnchorY(position[1], height, size, padding), text, size,
                width - 2 * padding, ArcMenuLayout.TOOLTIP_DEPTH + 4, false);
    }

    final void clearTooltip() {
        for (String id : List.of("tooltip:edge", "tooltip:panel", "tooltip:text")) {
            Node node = nodes.get(id);
            if (node != null && node.visible) {
                clearDisplay(node);
                node.visible = false;
                node.lastMatrix = null;
            }
        }
    }
    // END ARCMENU SHARED pointer

    // BEGIN ARCMENU SHARED display
    protected final void panel(String id, double x, double y, double w, double h, int color, double depth) {
        panel(id, x + w / 2, y + h / 2, w, h, color, depth, 0);
    }

    protected final void panel(String id, double x, double y, double w, double h, int color, double depth, double angle) {
        Node node = node(id, TextDisplay.class, x, y, depth);
        setText(node, " ");
        setBackground(node, color);
        node.scale = new Vector3f(ArcMenuLayout.panelScaleX(w, pixelsPerBlock()), ArcMenuLayout.panelScaleY(h, pixelsPerBlock()), 1);
        node.angle = angle;
        node.panelWidth = w;
        node.panelHeight = h;
        node.leftAligned = false;
        node.bitmap = false;
        transform(node, 1);
    }

    protected final void glyph(String id, double x, double y, double w, double h, char glyph,
                               int color, double depth, double angle) {
        if (w <= 0 || h <= 0 || (color >>> 24) == 0) {
            return;
        }
        Node node = node(id, TextDisplay.class, x, y, depth);
        TextDisplay display = (TextDisplay) node.display;
        setText(node, TextUtil.colorize("&#" + String.format("%06X", color & 0xFFFFFF)) + glyph + '\uEE65');
        display.setAlignment(TextDisplay.TextAlignment.CENTER);
        setBackground(node, 0);
        display.setLineWidth(32767);
        node.scale = new Vector3f((float) (w / (.2 * pixelsPerBlock())), (float) (h / (.2 * pixelsPerBlock())), 1);
        node.angle = angle;
        node.bitmap = true;
        node.panelWidth = 0;
        transform(node, 1);
        setOpacity(node, color >>> 24);
    }

    protected final void label(String id, double x, double y, String value, double size, double width) {
        label(id, x, y, value, size, width, 7);
    }

    protected final void label(String id, double x, double y, String value, double size, double width, double depth) {
        label(id, x, y, value, size, width, depth, true);
    }

    private void label(String id, double x, double y, String value, double size, double width, double depth, boolean truncate) {
        Node node = node(id, TextDisplay.class, x, y, depth);
        TextDisplay display = (TextDisplay) node.display;
        String[] lines = labelText(value).split("\n", -1);
        if (truncate) {
            for (int i = 0; i < lines.length; i++) {
                lines[i] = ArcMenuLayout.ellipsize(lines[i], Math.max(1, (int) Math.ceil(width / size)));
            }
        }
        String text = String.join("\n", lines);
        setText(node, text);
        if (node.alignment != TextDisplay.TextAlignment.LEFT) {
            display.setAlignment(TextDisplay.TextAlignment.LEFT);
            node.alignment = TextDisplay.TextAlignment.LEFT;
        }
        setBackground(node, 0);
        if (node.lineWidth != Integer.MAX_VALUE) {
            display.setLineWidth(Integer.MAX_VALUE);
            node.lineWidth = Integer.MAX_VALUE;
        }
        node.scale = new Vector3f(ArcMenuLayout.textScale(size, pixelsPerBlock()));
        node.leftAligned = true;
        node.panelWidth = 0;
        node.textWidth = ArcMenuLayout.textWidth(text);
        node.angle = 0;
        node.bitmap = false;
        transform(node, 1);
    }

    protected final void item(String id, double x, double y, double size, ItemStack stack, double depth) {
        if (stack != null) {
            item(id, x, y, size, stack, depth, stack.getAmount());
        }
    }

    protected final void item(String id, double x, double y, double size, ItemStack stack, double depth, int amount) {
        if (stack == null || stack.getType().isAir()) {
            return;
        }
        Node node = node(id, ItemDisplay.class, x, y, depth);
        ItemDisplay display = (ItemDisplay) node.display;
        ItemStack icon = stack.clone();
        icon.setAmount(1);
        if (node.item == null || !node.item.equals(icon)) {
            display.setItemStack(icon);
            node.item = icon;
        }
        ItemDisplay.ItemDisplayTransform mode = itemTransform(stack);
        display.setItemDisplayTransform(mode);
        node.guiItem = mode == ItemDisplay.ItemDisplayTransform.GUI;
        float scale = (float) ArcMenuLayout.worldUnits(size, pixelsPerBlock());
        node.scale = new Vector3f(scale, scale, scale * ITEM_DEPTH_SCALE);
        node.angle = 0;
        transform(node, 1);
        if (amount > 1) {
            String count = String.valueOf(amount);
            double fontSize = size / 32;
            double width = ArcMenuLayout.textWidth(count) * fontSize;
            double right = x + size / 2;
            double baseline = ArcMenuLayout.textAnchorY(y - size / 2, size, fontSize, 0);
            double amountDepth = depth + size / 2 + 2;
            label(id + ":amount-shadow", right - width + fontSize, baseline + fontSize,
                    "§0" + count, fontSize, width + fontSize, amountDepth);
            label(id + ":amount", right - width, baseline,
                    "§f" + count, fontSize, width + fontSize, amountDepth + .1);
        }
    }

    private <T extends Display> Node node(String id, Class<T> type, double x, double y, double depth) {
        used.add(id);
        Node node = nodes.get(id);
        if (node == null) {
            T display = type.cast(context.spawnPrivate(surface.origin(), type, (Consumer<T>) entity -> {
                entity.setPersistent(false);
                entity.setBillboard(Display.Billboard.FIXED);
                entity.setBrightness(new Display.Brightness(15, 15));
                entity.setShadowRadius(0);
                entity.setShadowStrength(0);
                entity.setInterpolationDuration(0);
                entity.setRotation(0, 0);
                if (entity instanceof TextDisplay text) {
                    text.setShadowed(false);
                    text.setDefaultBackground(false);
                    text.setSeeThrough(false);
                }
            }));
            node = new Node(display);
            nodes.put(id, node);
        } else if (!node.visible) {
            node.visible = true;
            node.hover = 1;
            node.lastMatrix = null;
        }
        double adjustedDepth = nodeDepth(id, depth);
        double safeDepth = context.pointerMode() == ArcMenuPointerMode.MOUSE
                ? ArcMenuLayout.cursorSafeDepth(adjustedDepth, cursorDepth, canvasScale()) : adjustedDepth;
        double perspective = ArcMenuLayout.overlayScale(safeDepth, pixelsPerBlock(), surface.getCanvas().getDesignDistance());
        node.position = surface.toWorld((x - getCanvasWidth() / 2) * perspective * canvasScale(),
                (getCanvasHeight() / 2 - y) * perspective * canvasScale(), safeDepth * canvasScale())
                .toVector().subtract(surface.origin().toVector());
        Vector right = surface.right();
        Vector up = surface.up();
        Vector normal = surface.normal();
        node.rotation = new Quaternionf().setFromNormalized(new Matrix3f(
                (float) right.getX(), (float) right.getY(), (float) right.getZ(),
                (float) up.getX(), (float) up.getY(), (float) up.getZ(),
                (float) normal.getX(), (float) normal.getY(), (float) normal.getZ()));
        node.overlayScale = (float) perspective;
        node.tooltip = id.startsWith("tooltip:");
        return node;
    }

    private void transform(Node node, float factor) {
        if (node.tooltip) {
            factor = 1;
        }
        float multiplier = Math.max(.01f, factor) * node.hover * node.overlayScale;
        Vector3f offset = new Vector3f();
        if (node.display instanceof TextDisplay && !node.bitmap) {
            if (node.panelWidth > 0) {
                offset.x = -.0125f * node.scale.x * multiplier;
                offset.y = -.125f * node.scale.y * multiplier;
            } else {
                if (node.leftAligned) {
                    offset.x = (float) ((node.textWidth / 2 - 1) * .025 * node.scale.x * multiplier);
                }
                offset.y = -.10f * node.scale.y * node.overlayScale;
            }
        }
        if (!node.tooltip) {
            offset.y += (float) ArcMenuLayout.worldUnits(animationOffset(factor), pixelsPerBlock());
        }
        Quaternionf rotation = new Quaternionf(node.rotation).rotateZ((float) -node.angle);
        Vector3f translation = new Vector3f((float) node.position.getX(), (float) node.position.getY(), (float) node.position.getZ())
                .add(rotation.transform(offset));
        Vector3f scale = new Vector3f(node.scale).mul(multiplier);
        Matrix4f matrix = new Matrix4f().translation(translation).rotate(rotation).scale(scale);
        if (node.display instanceof ItemDisplay && node.guiItem) {
            matrix.rotateY((float) Math.PI);
        }
        if (!matrix.equals(node.lastMatrix)) {
            node.display.setInterpolationDelay(0);
            if (node.display instanceof ItemDisplay) {
                node.display.setTransformationMatrix(matrix);
            } else {
                node.display.setTransformation(new Transformation(translation, rotation, scale, new Quaternionf()));
            }
            node.lastMatrix = matrix;
        }
        if (node.display instanceof TextDisplay) {
            setOpacity(node, Math.max(4, (int) (255 * factor)));
        }
    }

    protected final double canvasScale() {
        boolean mouse = context != null && context.pointerMode() == ArcMenuPointerMode.MOUSE;
        double width = mouse ? Math.min(surface.getCanvas().getWidth(), cursorWidth) : surface.getCanvas().getWidth();
        double height = mouse ? Math.min(surface.getCanvas().getHeight(), cursorHeight) : surface.getCanvas().getHeight();
        return ArcMenuLayout.canvasScale(width, height,
                getCanvasWidth(), getCanvasHeight(), surface.getCanvas().getPixelsPerBlock(), getScreenScale());
    }

    private double pixelsPerBlock() {
        return surface.getCanvas().getPixelsPerBlock() / canvasScale();
    }

    private static void setText(Node node, String value) {
        if (!value.equals(node.text)) {
            ((TextDisplay) node.display).setText(value);
            node.text = value;
        }
    }

    private static void setBackground(Node node, int color) {
        if (node.backgroundColor == null || node.backgroundColor != color) {
            ((TextDisplay) node.display).setBackgroundColor(Color.fromARGB(color));
            node.backgroundColor = color;
        }
    }

    private static void setOpacity(Node node, int opacity) {
        if (node.opacity != opacity) {
            ((TextDisplay) node.display).setTextOpacity((byte) opacity);
            node.opacity = opacity;
        }
    }

    private static void clearDisplay(Node node) {
        if (node.display instanceof TextDisplay) {
            setText(node, "");
            setBackground(node, 0);
            setOpacity(node, 0);
        } else if (node.display instanceof ItemDisplay item) {
            item.setItemStack(new ItemStack(Material.AIR));
            node.item = null;
        }
    }
    // END ARCMENU SHARED display

    // BEGIN ARCMENU SHARED types
    protected static final class Node {

        private final Display display;
        private Vector position = new Vector();
        private Vector3f scale = new Vector3f(1);
        private Quaternionf rotation = new Quaternionf();
        private Matrix4f lastMatrix;
        private String text;
        private ItemStack item;
        private boolean guiItem;
        private boolean visible = true;
        private Integer backgroundColor;
        private int lineWidth = -1;
        private TextDisplay.TextAlignment alignment;
        private int opacity = -1;
        private double panelWidth;
        private double panelHeight;
        private double textWidth;
        private boolean leftAligned;
        private boolean bitmap;
        private double angle;
        private float overlayScale = 1;
        private float hover = 1;
        private int delay;
        private boolean tooltip;

        private Node(Display display) {
            this.display = display;
        }
    }

    protected static final class Hit {

        final String id;
        private final double x;
        private final double y;
        private final double w;
        private final double h;
        private final List<String> tooltip;
        private final Consumer<ClickType> action;

        Hit(String id, double x, double y, double w, double h, List<String> tooltip, Consumer<ClickType> action) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.tooltip = tooltip;
            this.action = action;
        }
    }
    // END ARCMENU SHARED types

}
