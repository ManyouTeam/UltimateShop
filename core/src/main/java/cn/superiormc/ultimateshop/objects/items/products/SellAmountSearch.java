package cn.superiormc.ultimateshop.objects.items.products;

import java.util.function.IntFunction;

/* package-private */ final class SellAmountSearch {

    private SellAmountSearch() {
    }

    // A probe returns its accepted result, or null when this quantity cannot be sold.
    public static <T> T find(int upper, int attempts, IntFunction<T> probe) {
        if (upper < 1 || attempts < 1) {
            return null;
        }
        T best = probe.apply(upper);
        if (best != null) {
            return best;
        }
        upper--;
        int lower = 1;
        for (int attempt = 1; attempt < attempts && lower <= upper; attempt++) {
            int candidate = attempt == attempts - 1 && best == null ? lower : lower + (upper - lower) / 2;
            T result = probe.apply(candidate);
            if (result == null) {
                upper = candidate - 1;
            } else {
                best = result;
                lower = candidate + 1;
            }
        }
        return best;
    }
}
