package cn.superiormc.ultimateshop.objects.caches;

import java.util.Objects;

public class UseTimesStorageKey {

    public static final String SHARED_SHOP_ID = "__shared_use_times__";

    private final String shop;

    private final String product;

    public UseTimesStorageKey(String shop, String product) {
        this.shop = shop;
        this.product = product;
    }

    public String getShop() {
        return shop;
    }

    public String getProduct() {
        return product;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(shop, product);
    }
}
