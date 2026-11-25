package cn.superiormc.ultimateshop.objects.items.products;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// One instance per search. Candidates reuse cumulative costs for the same product and starting offset.
/* package-private */ final class DynamicSellCosts {

    public record Match(int count, BigDecimal cost) {
    }

    private record Run(ObjectSingleProduct product, int offset) {
    }

    private final Map<Run, List<BigDecimal>> totalsByRun = new HashMap<>();

    public Match match(ObjectSingleProduct product, Player player, int offset, int target, BigDecimal stock) {
        List<BigDecimal> totals = totalsByRun.computeIfAbsent(new Run(product, offset),
                key -> new ArrayList<>(List.of(BigDecimal.ZERO)));
        int count = totals.size() - 1;
        while (count < target && totals.get(count).compareTo(stock) <= 0) {
            BigDecimal next = product.getAmount(player, offset + count, false);
            if (next.signum() <= 0) {
                break;
            }
            totals.add(totals.get(count).add(next));
            count++;
        }
        int limit = Math.min(target, count);
        if (totals.get(limit).compareTo(stock) <= 0) {
            return new Match(limit, totals.get(limit));
        }
        int index = Collections.binarySearch(totals.subList(0, limit + 1), stock);
        int matched = index >= 0 ? index : Math.max(0, -index - 2);
        return new Match(matched, totals.get(matched));
    }
}
