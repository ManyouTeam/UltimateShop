package cn.superiormc.ultimateshop.objects.caches;

import java.util.Objects;

public record UseTimesStorageKey(String shop, String product) {

    public static final String SHARED_SHOP_ID = "__shared_use_times__";

    public boolean isShared() {
        return SHARED_SHOP_ID.equals(shop);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UseTimesStorageKey that)) {
            return false;
        }
        return Objects.equals(shop, that.shop) && Objects.equals(product, that.product);
    }

}
