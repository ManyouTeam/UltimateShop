package cn.superiormc.ultimateshop.gui.dialog;

import java.util.Objects;
import java.util.function.Consumer;

public final class DialogAction {

    private final String id;

    private final String label;

    private final String tooltip;

    private final Consumer<DialogResponse> handler;

    private final Boolean showSprite;

    private DialogAction(String id, String label, String tooltip, Consumer<DialogResponse> handler) {
        this(id, label, tooltip, handler, null);
    }

    private DialogAction(String id, String label, String tooltip, Consumer<DialogResponse> handler, Boolean showSprite) {
        this.showSprite = showSprite;
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

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getTooltip() {
        return tooltip;
    }

    public Boolean getShowSprite() {
        return showSprite;
    }

    public DialogAction withShowSprite(Boolean value) {
        return new DialogAction(id, label, tooltip, handler, value);
    }

    public void execute(DialogResponse response) {
        handler.accept(response);
    }
}
