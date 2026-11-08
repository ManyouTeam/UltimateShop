package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.ProductUIModel;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;

import com.fentai.arcmenu.api.ArcMenuPoint;
import com.fentai.arcmenu.api.ArcMenuScrollEvent;
import com.fentai.arcmenu.api.ArcMenuTextInput;
import com.fentai.arcmenu.api.ArcMenuTextInputEndReason;
import com.fentai.arcmenu.api.ArcMenuTextInputHandle;
import com.fentai.arcmenu.api.ArcMenuTextInputListener;
import com.fentai.arcmenu.api.ArcMenuTextInputOptions;
import com.fentai.arcmenu.api.ArcMenuTextInputResult;
import org.bukkit.Bukkit;

import java.util.List;

final class ArcInfoGUI implements ArcMenuTextInputListener {

    private final ArcCommonGUI gui;

    private ObjectItem selected;

    private int selectedSlot = -1;

    private int amount = 1;

    private boolean amountChanged;

    private ArcMenuTextInputHandle amountInput;

    private ObjectItem inputItem;

    private String inputValue = "";

    private boolean inputEditing;

    private int moreSlot = -1;

    private ObjectItem moreItem;

    private int morePage;

    private double moreX;

    private double moreY;

    private double moreW;

    private double moreH;

    ArcInfoGUI(ArcCommonGUI gui) {
        this.gui = gui;
    }

    void prepareRender() {
        if (selected != null && (!gui.visible(selected)
            || selectedSlot >= 0 && gui.product(selectedSlot) != selected)) {
            cancelInput();
            selected = null;
            selectedSlot = -1;
        }
    }

    boolean isSelected(int slot) {
        return selectedSlot == slot;
    }

    boolean hasDetails() {
        return selected != null;
    }

    boolean acceptsHit(String id) {
        if (amountInput != null) {
            return id.startsWith("details:input:") || id.equals("details:close");
        }
        return moreSlot < 0 || id.startsWith("extra:");
    }

    boolean handleScroll(ArcMenuScrollEvent event) {
        if (moreSlot >= 0 || amountInput != null) {
            return true;
        }
        ArcMenuPoint point = event.getPoint();
        if (selected != null && canSelectAmount() && point != null && point.getX() / gui.canvasScale() > 118) {
            changeAmount(-event.getSteps());
            return true;
        }
        return false;
    }

    void render(int accent) {
        if (selected != null) {
            renderDetails(accent);
        }
        if (moreSlot >= 0) {
            renderMoreMenu();
        }
    }

    void open(int slot, ObjectItem product) {
        if (product == null || !gui.visible(product)
            || !product.getBuyMore() || product.getBuyMoreMenu() == null) {
            return;
        }
        if (selected != product) {
            cancelInput();
            selected = product;
            amount = product.getBuyPrice().empty ? product.getDefaultSellAmount() : product.getDefaultBuyAmount();
            amount = ArcMenuLayout.clampAmount(amount, maxAmount());
            amountChanged = false;
        }
        selectedSlot = slot;
        closeMoreMenu();
        gui.clearTooltip();
    }

    private boolean canSelectAmount() {
        return selected != null && selected.getBuyMore() && selected.getBuyMoreMenu() != null
            && ConfigManager.configManager.containsClickAction("select-amount", selected, gui.getPlayer());
    }

    private int buyAmount() {
        return amountChanged ? amount : selected.getDefaultBuyAmount();
    }

    private int sellAmount() {
        return amountChanged ? amount : selected.getDefaultSellAmount();
    }

    void renderProductActions(int slot, double x, double y, double cw, double ch) {
        String id = "slot:" + slot;
        ObjectItem product = gui.product(slot);
        List<ProductUIModel.Action> primary = gui.controls.getPrimaryActions(product);
        double inset = gui.number("cards.buttons.inset-x", 4, 2, 16);
        double buttonGap = gui.number("cards.buttons.gap", 3, 2, 12);
        double bottom = gui.number("cards.buttons.inset-bottom", 6, 3, 16);
        double bw = (cw - 2 * inset - 2 * buttonGap) / 3;
        for (int actionIndex = 0; actionIndex < primary.size(); actionIndex++) {
            ProductUIModel.Action action = primary.get(actionIndex);
            gui.button(
                id + ":action:" + action.id(),
                x + inset + actionIndex * (bw + buttonGap),
                y + ch - gui.number("cards.button-height", 27, 18, 36) - bottom,
                bw,
                gui.number("cards.button-height", 27, 18, 36),
                action.label(),
                List.of(),
                type ->
                execute(
                product,
                action,
                product.getDefaultBuyAmount(),
                product.getDefaultSellAmount()));
        }
        gui.button(
            "more:" + slot,
            x + cw - bw - inset,
            y + ch - gui.number("cards.button-height", 27, 18, 36) - bottom,
            bw,
            gui.number("cards.button-height", 27, 18, 36),
            "…",
            List.of(),
            type -> {
                openMoreMenu(slot);
            });
    }

    private void renderDetails(int accent) {
        gui.addHit("details:shield", 453, 58, 177, 291, List.of(), type -> {

        });
        if (gui.bevelsEnabled()) {
            gui.beveledPanel("details", 453, 58, 177, 291,
                gui.color("details.background", 0xFF162035), gui.color("border", 0xFF25334D), 2);
        } else {
            gui.panel("details", 453, 58, 177, 291, gui.color("details.background", 0xFF162035), 2);
        }
        gui.panel("details:accent", 463, 70, 25, 2, 0xFF000000 | accent, 3);
        if (selected == null) {
            gui.label("details:hint", 469, 169, gui.text("select-product", "Select a product"), .85, 145);
            return;
        }
        gui.item("details:icon", 541, 110, 57, selected.getDisplayItem(gui.getPlayer()), 5);
        gui.label("details:name", 467, 154, selected.getDisplayName(gui.getPlayer(), false), .85, 149);
        List<String> lines =
            ArcMenuLayout.wrap(
            gui.controls.getHoverLore(selected, buyAmount(), sellAmount(), "arcmenu").stream()
            .map(gui::parse)
            .toList(),
            220);
        lines = lines.subList(0, Math.min(7, lines.size()));
        gui.label(
            "details:lore",
            467,
            ArcMenuLayout.textAnchorY(174, lines.size() * 10 * .65, .65, 0),
            String.join("\n", lines),
            .65,
            149);
        gui.button(
            "details:close",
            596,
            70,
            20,
            18,
            "X",
            List.of(),
            type -> {
                cancelInput();
                selected = null;
                selectedSlot = -1;
            });
        if (canSelectAmount()) {
            gui.button("minus", 467, 263, 26, 25, "-", List.of(), type -> changeAmount(-1));
            gui.button("plus", 590, 263, 26, 25, "+", List.of(), type -> changeAmount(1));
        }
        if (canSelectAmount() && Bukkit.getServicesManager().load(ArcMenuTextInput.class) != null) {
            gui.button("details:input:open", 500, 263, 84, 25,
                inputEditing ? inputValue + "_" : "× " + amount,
                List.of(gui.text("amount-input", "Enter amount")), type -> openInput());
        } else {
            gui.label("amount", 504, 277, "× " + amount, .85, 68);
        }
        List<ProductUIModel.Action> actions = gui.controls.getPrimaryActions(selected);
        for (int i = 0; i < actions.size(); i++) {
            ProductUIModel.Action action = actions.get(i);
            gui.button(
                "details:action:" + action.id(),
                467 + i * 79,
                298,
                70,
                26,
                action.label(),
                List.of(),
                type -> execute(selected, action, buyAmount(), sellAmount()));
        }
        if (inputEditing) {
            renderInputEditor();
        }
    }

    private void renderInputEditor() {
        double x = (gui.layout.getWidth() - 300) / 2.0;
        double y = (gui.layout.getHeight() - 117) / 2.0;
        double depth = ArcMenuLayout.ACTIONS_DEPTH;
        gui.panel("details:input:screen", x, y, 300, 117,
            gui.color("input.background", gui.color("sidebar", 0xFF303132)), depth);
        gui.label("details:input:title", x + 16, y + 25,
            gui.text("amount-input", "Enter amount"), 1.1, 268, depth + 1);
        gui.panel("details:input:field-border", x + 15, y + 38, 270, 34,
            gui.color("input.border", gui.color("accent", 0xFF588F3C)), depth + 1);
        gui.panel("details:input:field", x + 17, y + 40, 266, 30,
            gui.color("input.field", 0xFF1E1E1F), depth + 2);
        gui.label("details:input:value", x + 26, y + 60, inputValue + "_", 1.2, 248, depth + 3);
        gui.label("details:input:hint", x + 16, y + 97,
            LanguageManager.languageManager.getStringText(gui.getPlayer(), "menu.arcmenu.amount-input-done",
                "Type a quantity, then press Esc to finish."), .75, 268, depth + 3);
    }

    private int maxAmount() {
        if (selected == null) {
            return 1;
        }
        return canSelectAmount()
            ? Math.max(1, selected.getBuyMoreMenu().getSection().getInt("max-amount", 64))
            : Math.max(1, Math.max(selected.getDefaultBuyAmount(), selected.getDefaultSellAmount()));
    }

    private void changeAmount(int change) {
        if (!canSelectAmount()) {
            return;
        }
        amount = ArcMenuLayout.clampAmount((long) amount + change, maxAmount());
        amountChanged = true;
        gui.render();
    }

    private void openInput() {
        if (!canSelectAmount() || !gui.usable() || !gui.visible(selected)) {
            return;
        }
        if (amountInput != null) {
            amountInput.edit();
            return;
        }
        ArcMenuTextInput service = Bukkit.getServicesManager().load(ArcMenuTextInput.class);
        if (service == null) {
            return;
        }
        inputItem = selected;
        inputValue = String.valueOf(amount);
        inputEditing = true;
        amountInput = service.request(UltimateShop.instance, gui.getPlayer(),
            ArcMenuTextInputOptions.builder()
                .title(gui.parse(gui.text("amount-input", "Enter amount")))
                .initialValue(String.valueOf(amount))
                .maxLength(10)
                .build(), this);
        if (amountInput == null) {
            inputItem = null;
            inputEditing = false;
        }
        if (gui.usable()) {
            gui.render();
        }
    }

    private static Long parseInputAmount(String value) {
        String number = value.trim();
        if (!number.matches("-?[0-9]{1,10}")) {
            return null;
        }
        return Long.parseLong(number);
    }

    private void cancelInput() {
        ArcMenuTextInputHandle input = amountInput;
        amountInput = null;
        inputItem = null;
        inputEditing = false;
        if (input != null) {
            input.cancel();
        }
    }

    @Override
    public void onChanged(String value) {
        inputValue = value;
        if (gui.usable()) {
            gui.render();
        }
    }

    @Override
    public void onEditing(boolean editing) {
        inputEditing = editing;
        ArcMenuTextInputHandle input = amountInput;
        if (editing) {
            if (gui.usable()) {
                gui.render();
            }
            return;
        }
        if (input == null) {
            return;
        }
        if (parseInputAmount(input.value()) != null && gui.usable() && selected == inputItem
            && canSelectAmount() && gui.visible(selected)) {
            input.submit();
        } else {
            cancelInput();
            if (gui.usable()) {
                gui.render();
            }
        }
    }

    @Override
    public void onComplete(ArcMenuTextInputResult result) {
        if (amountInput == null || !amountInput.getToken().equals(result.getToken())) {
            return;
        }
        ObjectItem item = inputItem;
        amountInput = null;
        inputItem = null;
        inputEditing = false;
        if (!gui.usable()) {
            return;
        }
        if (result.getReason() == ArcMenuTextInputEndReason.SUBMITTED && selected == item
            && canSelectAmount() && gui.visible(item)) {
            Long value = parseInputAmount(result.getValue());
            if (value != null) {
                amount = ArcMenuLayout.clampAmount(value, maxAmount());
                amountChanged = true;
            }
        }
        gui.render();
    }

    private void execute(
        ObjectItem item, ProductUIModel.Action action, int buyAmount, int sellAmount) {
        if (!gui.visible(item)) {
            return;
        }
        if (action.id().equals("select-amount")) {
            gui.openInfo(item);
            return;
        }
        action.execute(item, gui.getPlayer(), buyAmount, sellAmount);
        if (gui.usable()) {
            gui.constructGUI();
        }
    }

    String priceLine(ObjectItem item) {
        return gui.controls.getPriceLine(item);
    }

    private void openMoreMenu(int slot) {
        moreSlot = slot;
        moreItem = gui.product(slot);
        morePage = 0;
        ArcMenuPoint pointer = gui.context.pointer();
        moreX = pointer == null ? 320 : pointer.getX() / gui.canvasScale() + gui.layout.getWidth() / 2.0;
        moreY = pointer == null ? 180 : gui.layout.getHeight() / 2.0 - pointer.getY() / gui.canvasScale();
        gui.clearTooltip();
    }

    private void closeMoreMenu() {
        moreSlot = -1;
        moreItem = null;
        morePage = 0;
    }

    private void renderMoreMenu() {
        ObjectItem item = gui.product(moreSlot);
        if (item == null || item != moreItem || !gui.visible(item)) {
            moreSlot = -1;
            return;
        }
        List<ProductUIModel.Action> actions = gui.controls.getExtraActions(item);
        int columns = (int) gui.number("more-menu.columns", 1, 1, 2);
        int capacity = columns * 6;
        int pages = Math.max(1, (actions.size() + capacity - 1) / capacity);
        morePage = Math.min(morePage, pages - 1);
        int count = Math.min(capacity, actions.size() - morePage * capacity);
        moreW = 160 * columns;
        moreH = 10 + Math.max(1, (count + columns - 1) / columns) * 27 + 30 + (pages > 1 ? 26 : 0);
        moreX = Math.max(6, Math.min(gui.layout.getWidth() - 6 - moreW, moreX));
        moreY = Math.max(54, Math.min(gui.layout.getHeight() - 6 - moreH, moreY));
        if (gui.bevelsEnabled()) {
            gui.beveledPanel("extra", moreX, moreY, moreW, moreH,
                gui.color("more-menu.background", 0xFF182238), gui.color("border", 0xFF25334D),
                ArcMenuLayout.ACTIONS_DEPTH);
        } else {
            gui.panel(
                "extra:edge",
                moreX - 1,
                moreY - 1,
                moreW + 2,
                moreH + 2,
                gui.color("button-border", 0xFF6B829F),
                ArcMenuLayout.ACTIONS_DEPTH);
            gui.panel(
                "extra:panel",
                moreX,
                moreY,
                moreW,
                moreH,
                gui.color("more-menu.background", 0xFF182238),
                ArcMenuLayout.ACTIONS_DEPTH + 2);
        }
        gui.addHit("extra:shield", moreX, moreY, moreW, moreH, List.of(), type -> {

        });
        if (actions.isEmpty()) {
            gui.label(
                "extra:empty",
                moreX + 8,
                moreY + 18,
                gui.text("no-extra", "No extra actions"),
                .7,
                moreW - 16,
                ArcMenuLayout.ACTIONS_DEPTH + 4);
        }
        for (int i = 0; i < count; i++) {
            ProductUIModel.Action action = actions.get(morePage * capacity + i);
            double x = moreX + 160 * (i % columns);
            double y = moreY + 27 * (i / columns);
            String id = "extra:" + action.id();
            gui.panel(
                id + ":panel",
                x + 4,
                y + 5,
                152,
                25,
                gui.buttonColor(id, false),
                ArcMenuLayout.ACTIONS_DEPTH + 3);
            gui.label(
                id + ":text",
                x + 8,
                ArcMenuLayout.textCenterY(y + 5, 25, 1),
                gui.buttonText(id, action.label()),
                1,
                144,
                ArcMenuLayout.ACTIONS_DEPTH + 4);
            gui.addHit(
                id,
                x + 4,
                y + 5,
                152,
                25,
                List.of(),
                type -> {
                    moreSlot = -1;
                    execute(
                        item,
                        action,
                        item.getDefaultBuyAmount(),
                        item.getDefaultSellAmount());
                });
        }
        if (pages > 1) {
            double y = moreY + moreH - 55;
            gui.label(
                "extra:page",
                moreX + 40,
                y + 14,
                (morePage + 1) + " / " + pages,
                .7,
                70,
                ArcMenuLayout.ACTIONS_DEPTH + 4);
            gui.addHit(
                "extra:previous",
                moreX + 4,
                y,
                30,
                24,
                List.of(),
                type -> morePage = Math.max(0, morePage - 1));
            gui.addHit(
                "extra:next",
                moreX + moreW - 34,
                y,
                30,
                24,
                List.of(),
                type -> morePage = Math.min(pages - 1, morePage + 1));
            gui.label(
                "extra:previous:text",
                moreX + 10,
                y + 15,
                "‹",
                .8,
                20,
                ArcMenuLayout.ACTIONS_DEPTH + 4);
            gui.label(
                "extra:next:text",
                moreX + moreW - 28,
                y + 15,
                "›",
                .8,
                20,
                ArcMenuLayout.ACTIONS_DEPTH + 4);
        }
        gui.panel(
            "extra:cancel:panel",
            moreX + 4,
            moreY + moreH - 29,
            moreW - 8,
            25,
            gui.buttonColor("extra:cancel", false),
            ArcMenuLayout.ACTIONS_DEPTH + 3);
        gui.label(
            "extra:cancel:text",
            moreX + 8,
            ArcMenuLayout.textCenterY(moreY + moreH - 29, 25, 1),
            gui.buttonText("extra:cancel", gui.text("cancel", "Cancel")),
            1,
            moreW - 16,
            ArcMenuLayout.ACTIONS_DEPTH + 4);
        gui.addHit(
            "extra:cancel",
            moreX + 4,
            moreY + moreH - 29,
            moreW - 8,
            25,
            List.of(),
            type -> closeMoreMenu());
    }
}
