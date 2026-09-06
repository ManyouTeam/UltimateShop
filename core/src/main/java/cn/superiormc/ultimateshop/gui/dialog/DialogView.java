package cn.superiormc.ultimateshop.gui.dialog;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DialogView {

    private final String title;

    private final List<String> body;

    private final List<ItemStack> items;

    private final List<DialogInput> inputs;

    private final List<DialogAction> actions;

    private final List<ItemAction> itemActions;

    private final Layout layout;

    private final boolean closeWithEscape;

    private final int buttonWidth;

    private final int columns;

    private DialogView(Builder builder) {
        this.title = builder.title;
        this.body = List.copyOf(builder.body);
        this.items = List.copyOf(builder.items);
        this.inputs = List.copyOf(builder.inputs);
        this.actions = List.copyOf(builder.actions);
        this.itemActions = List.copyOf(builder.itemActions);
        this.layout = builder.layout;
        this.closeWithEscape = builder.closeWithEscape;
        this.buttonWidth = builder.buttonWidth;
        this.columns = builder.columns;
        validateKeys();
    }

    public static Builder builder(String title) {
        return new Builder(title);
    }

    private void validateKeys() {
        Set<String> keys = new HashSet<>();
        for (DialogInput input : inputs) {
            if (!keys.add(input.getKey())) {
                throw new IllegalArgumentException("Duplicate dialog input key: " + input.getKey());
            }
        }
        keys.clear();
        for (DialogAction action : actions) {
            if (!keys.add(action.getId())) {
                throw new IllegalArgumentException("Duplicate dialog action id: " + action.getId());
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public List<String> getBody() {
        return body;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public List<DialogInput> getInputs() {
        return inputs;
    }

    public List<DialogAction> getActions() {
        return actions;
    }

    public List<ItemAction> getItemActions() {
        return itemActions;
    }

    public Layout getLayout() {
        return layout;
    }

    public boolean canCloseWithEscape() {
        return closeWithEscape;
    }

    public int getButtonWidth() {
        return buttonWidth;
    }

    public int getColumns() {
        return columns;
    }

    public static final class Builder {
        private final String title;

        private final List<String> body = new ArrayList<>();

        private final List<ItemStack> items = new ArrayList<>();

        private final List<DialogInput> inputs = new ArrayList<>();

        private final List<DialogAction> actions = new ArrayList<>();

        private final List<ItemAction> itemActions = new ArrayList<>();

        private Layout layout = Layout.MULTI_ACTION;

        private boolean closeWithEscape = false;

        private int buttonWidth = 150;

        private int columns = 2;

        private Builder(String title) {
            this.title = title == null ? "" : title;
        }

        public Builder body(String line) {
            body.add(line == null ? "" : line);
            return this;
        }

        public Builder item(ItemStack item) {
            if (item != null) {
                items.add(item.clone());
            }
            return this;
        }

        public Builder input(DialogInput input) {
            inputs.add(input);
            return this;
        }

        public Builder action(DialogAction action) {
            actions.add(action);
            return this;
        }

        public Builder itemAction(ItemStack item, DialogAction action) {
            if (item != null && action != null) {
                itemActions.add(new ItemAction(item, action));
                actions.add(action);
            }
            return this;
        }

        public Builder layout(String value) {
            layout = Layout.fromConfig(value); return this;
        }

        public Builder closeWithEscape(boolean value) {
            closeWithEscape = value;
            return this;
        }

        public Builder buttonWidth(int value) {
            buttonWidth = Math.max(1, value);
            return this;
        }

        public Builder columns(int value) {
            columns = Math.max(1, value); return this;
        }

        public DialogView build() {
            return new DialogView(this);
        }
    }

    public enum Layout {
        MULTI_ACTION,
        ITEM_ACTION_LIST;

        public static Layout fromConfig(String value) {
            return "item-action-list".equalsIgnoreCase(value) ? ITEM_ACTION_LIST : MULTI_ACTION;
        }
    }

    public static final class ItemAction {
        private final ItemStack item;

        private final DialogAction action;

        private ItemAction(ItemStack item, DialogAction action) {
            this.item = item.clone();
            this.action = action;
        }

        public ItemStack getItem() {
            return item.clone();
        }

        public DialogAction getAction() {
            return action;
        }
    }
}
