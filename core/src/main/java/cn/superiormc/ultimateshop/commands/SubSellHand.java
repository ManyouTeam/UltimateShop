package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ItemStorage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SubSellHand extends AbstractCommand {

    public SubSellHand() {
        this.id = "sellhand";
        this.requiredPermission = "ultimateshop." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        sellMainHandStack(player);
    }

    public static void sellMainHandStack(Player player) {
        ItemStorage handStorage = createMainHandStorage(player);
        ObjectItem item = ShopHelper.getTargetItem(handStorage, player);
        if (item == null || item.getSellPrice().empty) {
            LanguageManager.languageManager.sendStringText(player, "error.result-empty");
            return;
        }
        SellProductMethod.startSell(handStorage, item, player, false, false, true, false, 1, 1);
    }

    private static ItemStorage createMainHandStorage(Player player) {
        return new ItemStorage() {
            @Override
            public ItemStack[] getStorageContents() {
                ItemStack handItem = player.getInventory().getItemInMainHand();
                if (handItem == null || handItem.getType().isAir()) {
                    return new ItemStack[1];
                }
                return new ItemStack[]{handItem.clone()};
            }

            @Override
            public void setStorageContents(ItemStack[] contents) {
                PlayerInventory inventory = player.getInventory();
                if (contents == null || contents.length == 0 || contents[0] == null || contents[0].getType().isAir()) {
                    inventory.setItemInMainHand(new ItemStack(Material.AIR));
                    return;
                }
                inventory.setItemInMainHand(contents[0].clone());
            }

            @Override
            public boolean isPlayerInventory() {
                return true;
            }
        };
    }
}
