package cn.superiormc.ultimateshop.paper.dialog;

import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.gui.dialog.DialogResponse;
import cn.superiormc.ultimateshop.gui.dialog.DialogView;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PaperDialogFactory {

    private PaperDialogFactory() {
    }

    public static Dialog create(Player player, DialogGUI gui, DialogView view) {
        long generation = gui.getGeneration();
        List<DialogBody> bodies = new ArrayList<>();
        for (String line : view.getBody()) {
            bodies.add(DialogBody.plainMessage(PaperTextUtil.modernParse(line, player)));
        }
        for (org.bukkit.inventory.ItemStack item : view.getItems()) {
            bodies.add(DialogBody.item(item).build());
        }

        List<DialogInput> inputs = new ArrayList<>();
        for (cn.superiormc.ultimateshop.gui.dialog.DialogInput input : view.getInputs()) {
            inputs.add(createInput(player, input));
        }

        List<ActionButton> buttons = new ArrayList<>();
        for (cn.superiormc.ultimateshop.gui.dialog.DialogAction action : view.getActions()) {
            DialogActionCallback callback = (response, audience) ->
                    gui.handleAction(action.getId(), response(view, response), generation);
            ActionButton.Builder button = ActionButton.builder(PaperTextUtil.modernParse(action.getLabel(), player))
                    .width(view.getButtonWidth())
                    .action(DialogAction.customClick(callback,
                            ClickCallback.Options.builder().uses(1).build()));
            if (action.getTooltip() != null) {
                button.tooltip(PaperTextUtil.modernParse(action.getTooltip(), player));
            }
            buttons.add(button.build());
        }

        if (buttons.isEmpty()) {
            buttons.add(ActionButton.builder(PaperTextUtil.modernParse(
                            ConfigManager.configManager.getString("menu.dialog.default-button", ""), player))
                    .width(view.getButtonWidth())
                    .action(DialogAction.customClick((response, audience) -> gui.finishGUI(),
                            ClickCallback.Options.builder().uses(1).build()))
                    .build());
        }

        DialogBase base = DialogBase.builder(PaperTextUtil.modernParse(view.getTitle(), player))
                .body(bodies)
                .inputs(inputs)
                .canCloseWithEscape(view.canCloseWithEscape())
                .afterAction(DialogBase.DialogAfterAction.CLOSE)
                .build();

        return Dialog.create(builder -> builder.empty()
                .base(base)
                .type(DialogType.multiAction(buttons).columns(view.getColumns()).build()));
    }

    private static DialogInput createInput(
            Player player, cn.superiormc.ultimateshop.gui.dialog.DialogInput input) {
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
        for (cn.superiormc.ultimateshop.gui.dialog.DialogInput input : view.getInputs()) {
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
            if (value != null) values.put(input.getKey(), value);
        }
        return new DialogResponse(values);
    }
}
