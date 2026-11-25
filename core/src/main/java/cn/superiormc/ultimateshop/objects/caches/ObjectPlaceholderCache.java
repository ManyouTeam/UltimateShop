package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.objects.items.subobjects.AbstractCustomPlaceholder;

import java.time.LocalDateTime;
import java.util.List;

public class ObjectPlaceholderCache<P extends AbstractCustomPlaceholder> {

    protected final ObjectCache cache;

    protected final P placeholder;

    protected LocalDateTime refreshDoneTime;

    protected LocalDateTime lastResetTime;

    private List<String> values;

    public ObjectPlaceholderCache(ObjectCache cache, P placeholder) {
        this.cache = cache;
        this.placeholder = placeholder;
    }

    public P getPlaceholder() {
        return placeholder;
    }

    protected final boolean hasValue() {
        return values != null;
    }

    public final synchronized boolean isExpired(LocalDateTime now) {
        return refreshDoneTime != null && !refreshDoneTime.isAfter(now);
    }

    public final synchronized List<String> getStoredValues() {
        return values;
    }

    protected final void storeValues(List<String> values) {
        this.values = List.copyOf(values);
    }

    public final synchronized void restoreState(List<String> values, LocalDateTime nextReset, LocalDateTime lastReset) {
        storeValues(values);
        refreshDoneTime = nextReset;
        lastResetTime = lastReset;
    }

    public synchronized LocalDateTime getLastResetTime() {
        return lastResetTime;
    }

    public synchronized LocalDateTime getStoredResetTime() {
        return refreshDoneTime;
    }
}
