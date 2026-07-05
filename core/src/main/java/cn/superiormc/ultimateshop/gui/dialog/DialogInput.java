package cn.superiormc.ultimateshop.gui.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DialogInput {

    public enum Type { TEXT, BOOLEAN, NUMBER, SINGLE_OPTION }

    private final Type type;
    private final String key;
    private final String label;
    private String initialText = "";
    private boolean initialBoolean;
    private float min;
    private float max = 1;
    private float step = 1;
    private float initialNumber;
    private List<String> options = Collections.emptyList();

    private DialogInput(Type type, String key, String label) {
        this.type = type;
        this.key = Objects.requireNonNull(key, "key");
        this.label = Objects.requireNonNull(label, "label");
    }

    public static DialogInput text(String key, String label, String initial) {
        DialogInput input = new DialogInput(Type.TEXT, key, label);
        input.initialText = initial == null ? "" : initial;
        return input;
    }

    public static DialogInput bool(String key, String label, boolean initial) {
        DialogInput input = new DialogInput(Type.BOOLEAN, key, label);
        input.initialBoolean = initial;
        return input;
    }

    public static DialogInput number(String key, String label, float min, float max, float step, float initial) {
        DialogInput input = new DialogInput(Type.NUMBER, key, label);
        input.min = min;
        input.max = max;
        input.step = step;
        input.initialNumber = initial;
        return input;
    }

    public static DialogInput singleOption(String key, String label, List<String> options) {
        DialogInput input = new DialogInput(Type.SINGLE_OPTION, key, label);
        input.options = Collections.unmodifiableList(new ArrayList<>(options));
        return input;
    }

    public Type getType() { return type; }
    public String getKey() { return key; }
    public String getLabel() { return label; }
    public String getInitialText() { return initialText; }
    public boolean isInitialBoolean() { return initialBoolean; }
    public float getMin() { return min; }
    public float getMax() { return max; }
    public float getStep() { return step; }
    public float getInitialNumber() { return initialNumber; }
    public List<String> getOptions() { return options; }
}
