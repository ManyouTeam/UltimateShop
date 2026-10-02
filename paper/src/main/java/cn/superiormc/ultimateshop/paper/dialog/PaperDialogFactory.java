package cn.superiormc.ultimateshop.paper.dialog;

import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.gui.dialog.DialogResponse;
import cn.superiormc.ultimateshop.gui.dialog.DialogView;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PaperDialogFactory {

    private PaperDialogFactory() {
    }

    public static Dialog create(Player player, DialogGUI gui, DialogView view) {
        if (view.getScreen() != null) {
            return PaperOreRenderer.create(player, gui, view);
        }
        long generation = gui.getGeneration();
        List<DialogBody> bodies = new ArrayList<>();
        for (String line : view.getBody()) {
            bodies.add(DialogBody.plainMessage(PaperTextUtil.modernParse(line, player)));
        }
        for (ItemStack item : view.getItems()) {
            bodies.add(DialogBody.item(item).build());
        }
        if (view.getLayout() == DialogView.Layout.ITEM_ACTION_LIST) {
            for (DialogView.ItemAction itemAction : view.getItemActions()) {
                var action = itemAction.getAction();
                bodies.add(DialogBody.item(itemAction.getItem())
                        .description(DialogBody.plainMessage(itemActionLabel(player, itemAction)
                                .hoverEvent(action.getTooltip() == null ? null : PaperTextUtil.modernParse(action.getTooltip(), player))
                                .clickEvent(ClickEvent.callback(audience -> {
                                            if (audience instanceof Player actor && actor.getUniqueId().equals(player.getUniqueId())) {
                                                SchedulerUtil.runSync(player, () -> gui.handleAction(action.getId(), DialogResponse.empty(), generation));
                                            }
                                        },
                                        callbackOptions(view.keepOpenAfterAction())))))
                        .showTooltip(true)
                        .build());
            }
        }

        List<DialogInput> inputs = new ArrayList<>();
        for (int index = 0; index < view.getInputs().size(); index++) {
            inputs.add(createInput(player, view, index));
        }

        List<ActionButton> buttons = createButtons(player, gui, view, generation);

        DialogBase base = DialogBase.builder(PaperTextUtil.modernParse(view.getTitle(), player))
                .body(bodies)
                .inputs(inputs)
                .canCloseWithEscape(view.canCloseWithEscape())
                .pause(!view.keepOpenAfterAction())
                .afterAction(view.keepOpenAfterAction()
                        ? DialogBase.DialogAfterAction.NONE
                        : DialogBase.DialogAfterAction.CLOSE)
                .build();

        return Dialog.create(builder -> builder.empty()
                .base(base)
                .type(createType(player, gui, view, buttons)));
    }

    static List<ActionButton> createButtons(Player player, DialogGUI gui, DialogView view, long generation) {
        List<ActionButton> buttons = new ArrayList<>();
        for (var action : view.getFooterActions()) {
            DialogActionCallback callback = (response, audience) -> {
                if (audience instanceof Player actor && actor.getUniqueId().equals(player.getUniqueId())) {
                    SchedulerUtil.runSync(player, () -> gui.handleAction(action.getId(), response(view, response), generation));
                }
            };
            var label = PaperTextUtil.modernParse(action.getLabel(), player);
            if (!PaperTextUtil.shouldShowSprite(action.getShowSprite(), false)) {
                label = PaperTextUtil.withoutObjects(label);
            }
            ActionButton.Builder button = ActionButton.builder(label)
                    .width(view.getButtonWidth())
                    .action(DialogAction.customClick(callback,
                            callbackOptions(view.keepOpenAfterAction())));
            if (action.getTooltip() != null) {
                button.tooltip(PaperTextUtil.modernParse(action.getTooltip(), player));
            }
            buttons.add(button.build());
        }
        return buttons;
    }

    private static net.kyori.adventure.text.Component itemActionLabel(Player player, DialogView.ItemAction itemAction) {
        var label = PaperTextUtil.modernParse(itemAction.getAction().getLabel(), player);
        return PaperTextUtil.shouldShowSprite(itemAction.getAction().getShowSprite(), !itemAction.getItem().getType().isAir())
                ? label : PaperTextUtil.withoutObjects(label);
    }

    static DialogType createType(Player player, DialogGUI gui, DialogView view, List<ActionButton> buttons) {
        if ((view.getLayout() == DialogView.Layout.ITEM_ACTION_LIST || view.getScreen() != null) && buttons.size() <= 1) {
            return DialogType.notice(buttons.isEmpty() ? defaultCloseButton(player, gui, view) : buttons.get(0));
        }
        if (buttons.isEmpty()) {
            buttons.add(defaultCloseButton(player, gui, view));
        }
        return DialogType.multiAction(buttons).columns(view.getColumns()).build();
    }

    private static ActionButton defaultCloseButton(Player player, DialogGUI gui, DialogView view) {
        long generation = gui.getGeneration();
        return ActionButton.builder(PaperTextUtil.modernParse(
                        ConfigManager.configManager.getString("menu.dialog.default-button", ""), player))
                .width(view.getButtonWidth())
                .action(DialogAction.customClick((response, audience) -> {
                    if (audience instanceof Player actor && actor.getUniqueId().equals(player.getUniqueId())) {
                        SchedulerUtil.runSync(player, () -> gui.closeGUI(generation));
                    }
                },
                        callbackOptions(view.keepOpenAfterAction())))
                .build();
    }

    private static ClickCallback.Options callbackOptions(boolean repeatable) {
        return ClickCallback.Options.builder()
                .uses(repeatable ? Integer.MAX_VALUE : 1)
                .build();
    }

    private static DialogInput createInput(
            Player player, DialogView view, int index) {
        var input = view.getInputs().get(index);
        switch (input.getType()) {
            case TEXT:
                return DialogInput.text(input.getKey(), 300, PaperTextUtil.modernParse(input.getLabel(), player),
                        true, input.getInitialText(), 1024, null);
            case BOOLEAN:
                return DialogInput.bool(input.getKey(), PaperTextUtil.modernParse(input.getLabel(), player),
                        input.isInitialBoolean(), "true", "false");
            case NUMBER:
                return DialogInput.numberRange(input.getKey(), 300,
                        PaperTextUtil.modernParse(input.getLabel(), player), "%s: %s",
                        input.getMin(), input.getMax(), input.getInitialNumber(), input.getStep());
            case SINGLE_OPTION:
                List<SingleOptionDialogInput.OptionEntry> entries = new ArrayList<>();
                for (int i = 0; i < input.getOptions().size(); i++) {
                    String option = input.getOptions().get(i);
                    entries.add(SingleOptionDialogInput.OptionEntry.create(option,
                            PaperTextUtil.modernParse(option, player), i == 0));
                }
                return DialogInput.singleOption(input.getKey(), 300, entries,
                        PaperTextUtil.modernParse(input.getLabel(), player), true);
            default:
                throw new IllegalArgumentException("Unsupported dialog input type: " + input.getType());
        }
    }

    private static DialogResponse response(DialogView view, DialogResponseView response) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (var input : view.getInputs()) {
            Object value;
            switch (input.getType()) {
                case BOOLEAN:
                    value = response.getBoolean(input.getKey());
                    break;
                case NUMBER:
                    value = response.getFloat(input.getKey());
                    break;
                case TEXT:
                case SINGLE_OPTION:
                    value = response.getText(input.getKey());
                    break;
                default:
                    value = null;
            }
            if (value != null) {
                values.put(input.getKey(), value);
            }
        }
        return new DialogResponse(values);
    }
}
