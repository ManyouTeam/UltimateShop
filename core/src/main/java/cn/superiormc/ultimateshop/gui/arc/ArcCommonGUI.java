package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.MenuUILayout;
import cn.superiormc.ultimateshop.gui.ProductUIModel;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectCopyItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import com.fentai.arcmenu.api.ArcMenuScrollEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArcCommonGUI extends ArcMenuGUI {
    final ProductUIModel controls;
    private final ArcInfoGUI info;

    ArcCommonGUI(InvGUI source, ArcMenuRuntime runtime) {
        super(source, runtime);
        controls = new ProductUIModel(source.getMenu(), source.getPlayer());
        info = new ArcInfoGUI(this);
    }

    @Override
    protected void prepareRender() {
        info.prepareRender();
    }

    @Override
    protected void renderOverlays(int accent) {
        info.render(accent);
    }

    @Override
    protected boolean hasDetails() {
        return info.hasDetails();
    }

    @Override
    protected boolean acceptsHit(String id) {
        return info.acceptsHit(id);
    }

    @Override
    protected boolean handleScroll(ArcMenuScrollEvent event, Hit target) {
        return info.handleScroll(event);
    }

    @Override
    protected boolean isProductCard(int slot) {
        return product(slot) != null || buttons.get(slot) instanceof ObjectCopyItem;
    }

    protected ItemStack cardItem(int slot) {
        return slot < source.getInventory().getSize()
            ? source.getInventory().getItem(slot)
            : buttons.get(slot).getDisplayItem(player, 1).getItemStack();
    }

    protected int cardAmount(int slot, ItemStack stack) {
        return stack.getAmount();
    }

    protected boolean showAmount(int slot, int amount) {
        return amount > 1;
    }

    protected double cardActionHeight(int slot) {
        return 0;
    }

    protected void renderCardActions(int slot, double x, double y, double width, double height) {
    }

    protected List<String> cardLore(int slot, ItemStack stack) {
        return lore(stack);
    }

    protected ObjectItem product(int slot) {
        return buttons.get(slot) instanceof ObjectItem item ? item : null;
    }

    void openInfo(ObjectItem item) {
        for (int slot : buttons.keySet()) {
            if (product(slot) == item) {
                info.open(slot, item);
                return;
            }
        }
        info.open(-1, item);
    }

    protected void dispatchClick(int slot, ClickType type) {
        if (slot < source.getInventory().getSize()) {
            source.clickEventHandle(source.getInventory(), type, slot);
        } else if (buttons.get(slot) != null) {
            buttons.get(slot).clickEvent(type, player);
        }
    }

    final boolean visible(ObjectItem item) {
        return source.canDisplayMenu()
            && item.isVisibleInMenu("arcmenu")
            && item.canDisplay(MenuSender.of(player, "arcmenu"))
            && item.getShopObject().getProductNotHidden(MenuSender.of(player, "arcmenu"), item)
        != null;
    }

    @Override
    protected void renderElement(MenuUILayout.Element element, int accent) {
        int slot = element.slot();
        MenuUILayout.Box box = element.bounds();
        double x = box.x();
        double y = box.y();
        double cw = box.width();
        double ch = box.height();
        boolean showcase = true;
        ItemStack stack = cardItem(slot);
        if (stack == null || stack.getType().isAir()) {
            return;
        }
        if (!shouldRenderSlot(slot)) {
            return;
        }
        String id = "slot:" + slot;
        if (element.appearance().equals("button")) {
            button(
                id,
                x,
                y,
                cw,
                ch,
                ItemUtil.getItemName(stack),
                lore(stack),
                type -> click(slot, type));
            return;
        }
        boolean isSelected = info.isSelected(slot);
        panel(
            id + ":edge",
            x - 1,
            y - 1,
            cw + 2,
            ch + 2,
            isSelected ? accent : color("border", 0xFF25334D),
            2);
        panel(id + ":panel", x, y, cw, ch, color("card", 0xDC141E30), 3);
        item(
            id + ":icon",
            x + cw / 2,
            y + (showcase ? ch * .22 : 16),
            showcase ? Math.min(number("cards.icon-size", 28, 12, 38), ch * .30) : 21,
            stack,
            5);
        if (showcase) {
            int displayAmount = cardAmount(slot, stack);
            if (showAmount(slot, displayAmount)) {
                label(id + ":count", x + cw / 2, y + 14, "&b×" + displayAmount, .65, cw / 2 - 5);
            }
            label(
                id + ":name",
                x + 5,
                y + ch * .46,
                ItemUtil.getItemName(stack),
                number("cards.name-size", .8, .5, 1.2),
                cw - 10);
            ObjectItem product = product(slot);
            double fontSize = number("cards.price-size", .65, .5, 1);
            if (product == null) {
                double top = y + ch * .58;
                double bottom = y + ch - 6;
                bottom -= cardActionHeight(slot);
                List<String> lines = ArcMenuLayout.wrap(cardLore(slot, stack), Math.max(1, (int) ((cw - 10) / fontSize)));
                int limit = Math.max(0, (int) ((bottom - top) / (10 * fontSize)));
                if (limit > 0 && !lines.isEmpty()) {
                    boolean truncated = lines.size() > limit;
                    lines = new ArrayList<>(lines.subList(0, Math.min(limit, lines.size())));
                    if (truncated) {
                        int last = lines.size() - 1;
                        lines.set(last, ArcMenuLayout.ellipsize(lines.get(last) + "…",
                            Math.max(1, (int) ((cw - 10) / fontSize))));
                    }
                    label(id + ":description", x + 5,
                        ArcMenuLayout.textAnchorY(top, lines.size() * 10 * fontSize, fontSize, 0),
                        String.join("\n", lines), fontSize, cw - 10);
                }
            } else if (!info.priceLine(product).isEmpty()) {
                label(
                    id + ":price",
                    x + 5,
                    y + ch * .62,
                    info.priceLine(product),
                    fontSize,
                    cw - 10);
            }
        }
        addHit(id, x, y, cw, ch,
            product(slot) == null ? cardLore(slot, stack) : controls.getTooltip(product(slot)),
            type -> click(slot, type));
        renderCardActions(slot, x, y, cw, ch);
        if (showcase && product(slot) != null) {
            info.renderProductActions(slot, x, y, cw, ch);
        }
    }

    protected void click(int slot, ClickType type) {
        AbstractButton button = buttons.get(slot);
        if (button != null
            && (!button.isVisibleInMenu("arcmenu")
            || !button.canDisplay(MenuSender.of(player, "arcmenu")))) {
            return;
        }
        ObjectItem product = product(slot);
        if (product != null && !visible(product)) {
            return;
        }
        dispatchClick(slot, type);
        if (usable()) {
            source.constructGUI();
        }
    }
}
