package cn.superiormc.ultimateshop.objects.items;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public interface ItemStorage {

    ItemStack[] getStorageContents();

    void setStorageContents(ItemStack[] contents);

    default boolean isPlayerInventory() {
        return false;
    }

    default boolean isEmpty() {
        for (ItemStack item : getStorageContents()) {
            if (item != null && !item.getType().isAir()) {
                return false;
            }
        }
        return true;
    }

    static ItemStorage of(Inventory inventory) {
        return new ItemStorage() {
            @Override
            public ItemStack[] getStorageContents() {
                return inventory.getStorageContents();
            }

            @Override
            public void setStorageContents(ItemStack[] contents) {
                inventory.setStorageContents(contents);
            }

            @Override
            public boolean isPlayerInventory() {
                return inventory instanceof PlayerInventory;
            }
        };
    }

    static ItemStorage of(ItemStack[] contents) {
        return new ItemStorage() {

            private ItemStack[] storageContents = cloneContents(contents);

            @Override
            public ItemStack[] getStorageContents() {
                return storageContents;
            }

            @Override
            public void setStorageContents(ItemStack[] contents) {
                storageContents = cloneContents(contents);
            }
        };
    }

    static ItemStack[] cloneContents(ItemStack[] contents) {
        if (contents == null) {
            return new ItemStack[0];
        }
        ItemStack[] clonedContents = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            clonedContents[i] = contents[i] == null ? null : contents[i].clone();
        }
        return clonedContents;
    }
}
