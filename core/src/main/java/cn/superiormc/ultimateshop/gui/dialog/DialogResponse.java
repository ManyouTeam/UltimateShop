package cn.superiormc.ultimateshop.gui.dialog;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DialogResponse {

    private final Map<String, Object> values;

    public DialogResponse(Map<String, Object> values) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public static DialogResponse empty() {
        return new DialogResponse(Collections.emptyMap());
    }

    public String getText(String key) {
        Object value = values.get(key);
        return value instanceof String ? (String) value : null;
    }

    public Boolean getBoolean(String key) {
        Object value = values.get(key);
        return value instanceof Boolean ? (Boolean) value : null;
    }

    public Float getFloat(String key) {
        Object value = values.get(key);
        return value instanceof Number ? ((Number) value).floatValue() : null;
    }

    public Map<String, Object> values() {
        return values;
    }
}
