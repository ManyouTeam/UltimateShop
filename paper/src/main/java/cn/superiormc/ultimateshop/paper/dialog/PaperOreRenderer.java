package cn.superiormc.ultimateshop.paper.dialog;

import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.gui.dialog.DialogResponse;
import cn.superiormc.ultimateshop.gui.dialog.DialogView;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.DialogPackManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.managers.MinecraftResourceManager;
import cn.superiormc.ultimateshop.objects.dialog.DialogFontUtil;
import cn.superiormc.ultimateshop.objects.dialog.ObjectDialogScreen;
import cn.superiormc.ultimateshop.objects.dialog.ObjectDialogTemplate;
import cn.superiormc.ultimateshop.objects.dialog.ObjectDialogWidget;
import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PaperOreRenderer {

    private static final Key SPACING_FONT = Key.key("ultimateshop:ore/spacing");

    private static final Key TEXT_FONT = Key.key("minecraft:default");

    public static Dialog create(Player player, DialogGUI gui, DialogView view) {
        ObjectDialogScreen screen = view.getScreen();
        long generation = gui.getGeneration();
        List<ObjectDialogWidget> ordered =
            screen.getWidgets().stream()
            .sorted(Comparator.comparingInt(ObjectDialogWidget::getX))
            .toList();
        refreshAfterLanguageLoad(player, gui, view, generation, ordered);
        Map<ObjectDialogWidget, Map<Integer, List<Glyph>>> text = new HashMap<>();
        for (ObjectDialogWidget widget : ordered) {
            text.put(widget, getText(player, widget));
        }
        Component content =
            Component.empty()
            .color(NamedTextColor.WHITE)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, false)
            .append(getBackground(screen, ordered));
        for (int y = 0; y < screen.getHeight(); y += 9) {
            content = content.append(Component.newline());
            int end = 0;
            for (ObjectDialogWidget widget : ordered) {
                if (y < widget.getY() || y >= widget.getY() + widget.getTemplate().getHeight()) {
                    continue;
                }
                content = content.append(spacing(widget.getX() - end));
                int row = (y - widget.getY()) / 9;
                content =
                    content.append(
                    getWidgetRow(
                    player,
                    gui,
                    generation,
                    widget,
                    row,
                    text.get(widget).get(row)));
                end = widget.getX() + widget.getTemplate().getWidth();
            }
            content = content.append(spacing(screen.getWidth() - end));
        }
        List<DialogBody> body = new ArrayList<>();
        for (String line : view.getBody()) {
            body.add(DialogBody.plainMessage(PaperTextUtil.modernParse(line, player)));
        }
        body.add(DialogBody.plainMessage(content, screen.getWidth() + 16));
        List<ActionButton> buttons =
            PaperDialogFactory.createButtons(player, gui, view, generation);
        return Dialog.create(
            builder ->
            builder.empty()
            .base(
            DialogBase.builder(
            PaperTextUtil.modernParse(
            view.getTitle(), player))
            .body(body)
            .pause(false)
            .canCloseWithEscape(false)
            .afterAction(DialogBase.DialogAfterAction.NONE)
            .build())
            .type(PaperDialogFactory.createType(player, gui, view, buttons)));
    }

    private static Component getBackground(
        ObjectDialogScreen screen, List<ObjectDialogWidget> widgets) {
        Component result = Component.empty();
        int end = 0;
        for (ObjectDialogWidget widget : widgets) {
            ObjectDialogTemplate template = widget.getTemplate();
            String frame =
                template.getResourceId()
                + (template.isCard() ? "_product" : "_button")
                + (widget.getActionId() != null ? "_enabled" : "_disabled")
                + (!widget.isProduct()
                && widget.isSelected()
                && widget.getActionId() != null
                ? "_selected"
                : "")
                + "_background";
            result = result.append(spacing(widget.getX() - end));
            for (int top = 0, verticalTile = 0;
                top < template.getHeight();
                top += DialogFontUtil.BACKGROUND_TILE_HEIGHT, verticalTile++) {
                if (top != 0) {
                    result = result.append(spacing(-template.getWidth()));
                }
                for (int tile = 0;
                    tile < DialogFontUtil.getTileCount(template.getWidth());
                    tile++) {
                    if (tile != 0) {
                        result = result.append(spacing(-1));
                    }
                    result =
                        result.append(
                        Component.text(
                        DialogFontUtil.getBackgroundCharacter(
                        widget.getY(), verticalTile, tile))
                        .font(Key.key("ultimateshop:" + frame))
                        .color(NamedTextColor.WHITE)
                        .shadowColor(ShadowColor.none()));
                }
            }
            end = widget.getX() + template.getWidth();
        }
        result = result.append(spacing(-end));
        end = 0;
        for (ObjectDialogWidget widget : widgets) {
            ObjectDialogTemplate template = widget.getTemplate();
            String icon = DialogFontUtil.getIconTexture(widget.getIcon());
            if (!hasEnlargedIcon(widget)) {
                continue;
            }
            int x = widget.getX() + template.getPadding();
            String id =
                DialogFontUtil.getIconFontId(
                icon,
                template.getIconSize(),
                template.getBackground(),
                template.getLoreRow());
            result =
                result.append(spacing(x - end))
                .append(
                Component.text(
                DialogFontUtil.getBackgroundCharacter(
                widget.getY(), 0, 0))
                .font(Key.key("ultimateshop:" + id))
                .color(NamedTextColor.WHITE)
                .shadowColor(ShadowColor.none()));
            end = x + template.getIconSize() + 2;
        }
        return result.append(spacing(screen.getWidth() - end));
    }

    private static Component getWidgetRow(
        Player player,
        DialogGUI gui,
        long generation,
        ObjectDialogWidget widget,
        int row,
        List<Glyph> line) {
        ObjectDialogTemplate template = widget.getTemplate();
        boolean details = widget.isProduct() && row >= template.getDetailsRow();
        Component result = Component.empty().font(TEXT_FONT);
        if (details && !widget.getToolbar().isEmpty()) {
            double cellWidth = (double) template.getWidth() / widget.getToolbar().size();
            for (ObjectDialogWidget.ToolbarAction action : widget.getToolbar()) {
                Component cell = spacing(cellWidth);
                if (row == template.getDetailsTextRow()) {
                    List<List<Glyph>> wrapped =
                        wrap(
                        PaperTextUtil.modernParse(action.label(), player),
                        (int) cellWidth - 16,
                        1,
                        player,
                        true);
                    List<Glyph> glyphs = wrapped.isEmpty() ? List.of() : wrapped.get(0);
                    double measured = glyphs.stream().mapToDouble(Glyph::width).sum();
                    double padding = 12;
                    cell =
                        spacing(padding)
                        .append(label(glyphs))
                        .append(spacing(cellWidth - padding - measured));
                }
                cell =
                    cell.clickEvent(
                    ClickEvent.callback(
                    audience -> {
                        if (audience instanceof Player actor
                            && actor.getUniqueId()
                            .equals(player.getUniqueId())) {
                            SchedulerUtil.runSync(
                                player,
                                () ->
                                gui.handleAction(
                                action.id(),
                                DialogResponse.empty(),
                                generation));
                        }
                    },
                    ClickCallback.Options.builder()
                    .uses(ClickCallback.UNLIMITED_USES)
                    .build()));
                result = result.append(cell);
            }
            return result;
        }
        int width = template.getWidth();
        boolean enlargedIcon = hasEnlargedIcon(widget);
        if (line != null && !line.isEmpty()) {
            double measured = line.stream().mapToDouble(Glyph::width).sum();
            int textLeft =
                enlargedIcon && row < template.getDetailsRow()
            ? template.getPadding() + template.getIconSize() + 8
            : template.getPadding();
            double left = textLeft;
            Component label = label(line);
            result =
                result.append(spacing(left))
                .append(label)
                .append(spacing(width - left - measured));
        } else {
            result = result.append(spacing(width));
        }
        int iconTextRow =
            template.getLoreRow()
            + Math.max(0, Math.round((template.getIconSize() - 7) / 9.0f));
        String iconText = widget.getIconText();
        if (enlargedIcon
            && iconText != null
            && !iconText.isEmpty()
            && row == iconTextRow
            && !("1".equals(iconText) && DialogPackManager.hidesOneIconAmount())) {
            int textWidth = iconText.codePoints().map(PaperOreRenderer::glyphWidth).sum();
            int right = template.getPadding() + template.getIconSize() + 1;
            result =
                result.append(spacing(right - textWidth - width))
                .append(
                Component.text(iconText, NamedTextColor.WHITE)
                .font(TEXT_FONT)
                .decoration(TextDecoration.BOLD, false)
                .decoration(TextDecoration.ITALIC, false)
                .shadowColor(ShadowColor.shadowColor(0xff3f3f3f)))
                .append(spacing(width - right));
        }
        Component hint =
            PaperTextUtil.modernParse(
            widget.getName() + "\n" + String.join("\n", widget.getHoverLore()), player);
        result = result.hoverEvent(hint);
        if (widget.canClickRow(row)) {
            result =
                result.clickEvent(
                ClickEvent.callback(
                audience -> {
                    if (audience instanceof Player actor
                        && actor.getUniqueId()
                        .equals(player.getUniqueId())) {
                        SchedulerUtil.runSync(
                            player,
                            () ->
                            gui.handleAction(
                            widget.getActionId(),
                            DialogResponse.empty(),
                            generation));
                    }
                },
                ClickCallback.Options.builder()
                .uses(ClickCallback.UNLIMITED_USES)
                .build()));
        }
        return result;
    }

    private static Map<Integer, List<Glyph>> getText(Player player, ObjectDialogWidget widget) {
        ObjectDialogTemplate template = widget.getTemplate();
        int width = template.getWidth() - template.getPadding() * 2;
        Map<Integer, List<Glyph>> result = new HashMap<>();
        boolean enlargedIcon = hasEnlargedIcon(widget);
        Component name = PaperTextUtil.modernParse(widget.getName(), player);
        boolean showSprite = PaperTextUtil.shouldShowSprite(widget.getShowSprite(), enlargedIcon);
        if (!showSprite) {
            name = PaperTextUtil.withoutObjects(name);
        } else if (widget.isProduct()
            && DialogFontUtil.getIconTexture(widget.getIcon()) != null
            && !containsObject(name)
            && (Boolean.TRUE.equals(widget.getShowSprite())
            || ConfigManager.configManager
            .getBoolean("menu.dialog.auto-add-sprite.enabled"))) {
            name =
                PaperTextUtil.modernParse(widget.getIcon(), player)
                .append(Component.space())
                .append(name);
        }
        int contentWidth = enlargedIcon ? width - template.getIconSize() - 8 : width;
        List<List<Glyph>> title =
            wrap(name, contentWidth, template.getNameLines(widget.isProduct()), player, true);
        int nameRow = template.getNameRow(title.size());
        for (int i = 0; i < title.size(); i++) {
            result.put(nameRow + i, title.get(i));
        }
        int maximum = template.getLoreLines();
        if (widget.isProduct()) {
            maximum = Math.min(maximum, template.getDetailsRow() - template.getLoreRow());
        } else {
            maximum = Math.min(maximum, template.getHeight() / 9 - template.getLoreRow());
        }
        if (maximum > 0) {
            Component lore = PaperTextUtil.modernParse(String.join("\n", widget.getLore()), player);
            List<List<Glyph>> lines = wrap(lore, contentWidth, maximum, player);
            for (int i = 0; i < lines.size(); i++) {
                result.put(template.getLoreRow() + i, lines.get(i));
            }
        }
        if (widget.isProduct()) {
            List<List<Glyph>> details =
                wrap(
                PaperTextUtil.modernParse(widget.getActionLabel(), player),
                width,
                1,
                player);
            if (!details.isEmpty()) {
                result.put(template.getDetailsTextRow(), details.get(0));
            }
        }
        return result;
    }

    private static boolean containsObject(Component component) {
        return component instanceof ObjectComponent
            || component.children().stream().anyMatch(PaperOreRenderer::containsObject);
    }

    private static boolean hasEnlargedIcon(ObjectDialogWidget widget) {
        ObjectDialogTemplate template = widget.getTemplate();
        String icon = DialogFontUtil.getIconTexture(widget.getIcon());
        if (icon == null || template.getIconSize() == 0) {
            return false;
        }
        String id =
            DialogFontUtil.getIconFontId(
            icon,
            template.getIconSize(),
            template.getBackground(),
            template.getLoreRow());
        return DialogPackManager.dialogPackManager == null
            || DialogPackManager.dialogPackManager.hasIcon(id, widget.getY());
    }

    private static List<List<Glyph>> wrap(
        Component component, int width, int maximum, Player player) {
        return wrap(component, width, maximum, player, false);
    }

    private static List<List<Glyph>> wrap(
        Component component, int width, int maximum, Player player, boolean trimLeading) {
        List<Glyph> glyphs = new ArrayList<>();
        flatten(localizeVanillaNames(component, player), Style.empty(), glyphs);
        if (trimLeading) {
            while (!glyphs.isEmpty()
                && glyphs.get(0).component() instanceof TextComponent text
                && text.content().isBlank()) {
                glyphs.remove(0);
            }
        }
        List<List<Glyph>> result = new ArrayList<>();
        List<Glyph> line = new ArrayList<>();
        if (maximum <= 0 || width <= 0) {
            return result;
        }
        double used = 0;
        for (int i = 0; i < glyphs.size(); i++) {
            Glyph glyph = glyphs.get(i);
            int end = i + 1;
            double tokenWidth = glyph.width();
            if (isEnglishWordGlyph(glyph)) {
                while (end < glyphs.size() && isEnglishWordGlyph(glyphs.get(end))) {
                    tokenWidth += glyphs.get(end).width();
                    end++;
                }
            }
            if (glyph.newline() || (!line.isEmpty() && used + tokenWidth > width)) {
                while (!line.isEmpty()
                    && line.get(line.size() - 1).component() instanceof TextComponent text
                    && text.content().isBlank()) {
                    line.remove(line.size() - 1);
                }
                result.add(line);
                if (result.size() == maximum) {
                    return result;
                }
                line = new ArrayList<>();
                used = 0;
                if (glyph.newline()) {
                    continue;
                }
                if (glyph.component() instanceof TextComponent text && text.content().isBlank()) {
                    continue;
                }
            }
            for (int index = i; index < end; index++) {
                Glyph part = glyphs.get(index);
                // Keep client-resolved components intact, even when their estimate exceeds the line.
                Glyph fitted =
                    part.width() > width ? new Glyph(part.component(), width, false) : part;
                line.add(fitted);
                used += fitted.width();
            }
            i = end - 1;
        }
        if (!line.isEmpty() || result.isEmpty()) {
            result.add(line);
        }
        return result;
    }

    private static boolean isEnglishWordGlyph(Glyph glyph) {
        return glyph.component() instanceof TextComponent text
            && text.content().length() == 1
            && TextUtil.isEnglishWordCharacter(text.content().charAt(0));
    }

    private static void flatten(Component component, Style inherited, List<Glyph> result) {
        Style style =
            component
            .style()
            .merge(inherited, Style.Merge.Strategy.IF_ABSENT_ON_TARGET)
            .font(TEXT_FONT)
            .clickEvent(null)
            .hoverEvent(null)
            .decoration(TextDecoration.OBFUSCATED, false);
        if (component instanceof TextComponent literal) {
            for (int code : literal.content().codePoints().toArray()) {
                if (code == '\r') {
                    continue;
                }
                String value = Character.toString(code);
                double boldOffset = code > 255 ? .5 : 1;
                double advance =
                    glyphWidth(code)
                    + (style.decoration(TextDecoration.BOLD)
                    == TextDecoration.State.TRUE
                    ? boldOffset
                    : 0);
                result.add(new Glyph(Component.text(value).style(style), advance, code == '\n'));
            }
        } else if (component instanceof ObjectComponent object) {
            int advance =
                8
                + (style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE
                ? 1
                : 0);
            result.add(new Glyph(object.children(List.of()).style(style), advance, false));
        } else {
            Component value = component.children(List.of()).style(style);
            String plain = PlainTextComponentSerializer.plainText().serialize(value);
            int advance = plain.codePoints().map(PaperOreRenderer::glyphWidth).sum();
            result.add(new Glyph(value, Math.min(120, Math.max(9, advance)), false));
        }
        for (Component child : component.children()) {
            flatten(child, style, result);
        }
    }

    private static boolean hasVanillaTranslation(Component component) {
        return component instanceof TranslatableComponent translated
            && translated.arguments().isEmpty()
            && (translated.key().startsWith("item.minecraft.")
            || translated.key().startsWith("block.minecraft."))
            || component.children().stream().anyMatch(PaperOreRenderer::hasVanillaTranslation);
    }

    private static Component localizeVanillaNames(Component component, Player player) {
        List<Component> children =
            component.children().stream()
            .map(child -> localizeVanillaNames(child, player))
            .toList();
        MinecraftResourceManager resources = MinecraftResourceManager.minecraftResourceManager;
        if (resources != null
            && player != null
            && component instanceof TranslatableComponent translated
            && translated.arguments().isEmpty()
            && (translated.key().startsWith("item.minecraft.")
            || translated.key().startsWith("block.minecraft."))) {
            String name = resources.getLocalizedName(translated.key(), player.getLocale());
            if (name != null) {
                return Component.text(name).style(component.style()).children(children);
            }
        }
        return component.children(children);
    }

    private static void refreshAfterLanguageLoad(
        Player player,
        DialogGUI gui,
        DialogView view,
        long generation,
        List<ObjectDialogWidget> widgets) {
        MinecraftResourceManager resources = MinecraftResourceManager.minecraftResourceManager;
        if (resources == null
            || widgets.stream()
            .noneMatch(
            widget ->
            hasVanillaTranslation(
            PaperTextUtil.modernParse(
            widget.getName(), player)))) {
            return;
        }
        String language = player.getLocale();
        CompletableFuture<?> loading = resources.requestLanguage(language);
        if (loading.isDone()) {
            return;
        }
        loading.whenComplete(
            (result, exception) -> {
                if (exception != null) {
                    return;
                }
                SchedulerUtil.runSync(
                    player,
                    () -> {
                        if (player.isOnline()
                            && gui.getGeneration() == generation
                            && gui.getDialog() == view
                            && language.equalsIgnoreCase(player.getLocale())
                            && MenuStatusManager.menuStatusManager.getOpeningGUI(player)
                            == gui) {
                            gui.updateGUI();
                        }
                    });
            });
    }

    private static int glyphWidth(int codePoint) {
        if (codePoint == '\n') {
            return 0;
        }
        if (codePoint == ' ') {
            return 4;
        }
        if (codePoint < 128 && "!.,:;i|".indexOf(codePoint) != -1 || codePoint == '\'') {
            return 2;
        }
        if (codePoint == '`' || codePoint == 'l') {
            return 3;
        }
        if (codePoint < 128 && "\"()*I[]t{}".indexOf(codePoint) != -1) {
            return 4;
        }
        if (codePoint < 128 && "<>fk".indexOf(codePoint) != -1) {
            return 5;
        }
        if (codePoint == '@' || codePoint == '~') {
            return 7;
        }
        return codePoint < 256 ? 6 : 9;
    }

    private static Component label(List<Glyph> glyphs) {
        Component result = Component.empty();
        for (Glyph glyph : glyphs) {
            result = result.append(glyph.component());
        }
        return result;
    }

    private static Component spacing(double pixels) {
        return Component.text(DialogFontUtil.getSpace(pixels)).font(SPACING_FONT);
    }

    private record Glyph(Component component, double width, boolean newline) {

    }
}
