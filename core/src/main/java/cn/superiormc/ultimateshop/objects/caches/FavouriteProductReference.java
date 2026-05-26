package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.entity.Player;

import java.util.Objects;

public record FavouriteProductReference(String shop, String product) {

    public String serialize() {
        return shop + ";;" + product;
    }

    public ObjectItem resolve(Player player) {
        ObjectShop targetShop = ConfigManager.configManager.getShop(shop);
        if (targetShop == null) {
            return null;
        }
        ObjectItem item = targetShop.getProductNotHidden(player, product);
        if (item == null || !item.isAllowFavourite()) {
            return null;
        }
        return item;
    }

    public static FavouriteProductReference deserialize(String rawValue) {
        if (rawValue == null || !rawValue.contains(";;")) {
            return null;
        }
        String[] split = rawValue.split(";;", 2);
        if (split.length != 2 || split[0].isEmpty() || split[1].isEmpty()) {
            return null;
        }
        return new FavouriteProductReference(split[0], split[1]);
    }

    public static FavouriteProductReference fromItem(ObjectItem item) {
        if (item == null) {
            return null;
        }
        return new FavouriteProductReference(item.getShop(), item.getProduct());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FavouriteProductReference that)) {
            return false;
        }
        return Objects.equals(shop, that.shop) && Objects.equals(product, that.product);
    }

}
