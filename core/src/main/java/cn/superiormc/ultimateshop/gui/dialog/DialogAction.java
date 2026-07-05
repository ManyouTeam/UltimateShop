package cn.superiormc.ultimateshop.gui.dialog;

import java.util.Objects;
import java.util.function.Consumer;

public final class DialogAction {

    private final String id;
    private final String label;
    private final String tooltip;
    private final Consumer<DialogResponse> handler;

    private DialogAction(String id, String label, String tooltip, Consumer<DialogResponse> handler) {
        this.id = Objects.requireNonNull(id, "id");
        this.label = Objects.requireNonNull(label, "label");
        this.tooltip = tooltip;
        this.handler = Objects.requireNonNull(handler, "handler");
    }

    public static DialogAction of(String id, String label, Consumer<DialogResponse> handler) {
        return new DialogAction(id, label, null, handler);
    }

    public static DialogAction of(String id, String label, String tooltip, Consumer<DialogResponse> handler) {
        return new DialogAction(id, label, tooltip, handler);
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
    public String getTooltip() { return tooltip; }
    public void execute(DialogResponse response) { handler.accept(response); }
}
