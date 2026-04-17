package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ObjectFavouriteResultButton extends AbstractButton {

    private final ObjectItem item;

    private final int index;

    private final List<String> resultLore;

    private final boolean editing;

    public ObjectFavouriteResultButton(ObjectItem item, int index, List<String> resultLore, boolean editing) {
        this.item = item;
        this.index = index;
        this.resultLore = resultLore;
        this.editing = editing;
        this.type = ButtonType.FAVOURITE_RESULT;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        if (editing) {
            ItemStack resultDisplayItem = item.getDisplayItem(player);
            ItemMeta meta = resultDisplayItem.getItemMeta();
            if (meta == null) {
                return new ObjectDisplayItemStack(resultDisplayItem);
            }

            List<String> lore = new ArrayList<>();
            List<String> originalLore = UltimateShop.methodUtil.getItemLore(meta);
            if (originalLore != null && !originalLore.isEmpty()) {
                lore.addAll(originalLore);
            }
            lore.addAll(CommonUtil.modifyList(player, resultLore,
                    "shop", item.getShop(),
                    "product", item.getProduct(),
                    "index", String.valueOf(index + 1),
                    "editing", "true",
                    "edit-mode", "true"));
            UltimateShop.methodUtil.setItemLore(meta, lore, player);
            resultDisplayItem.setItemMeta(meta);
            return new ObjectDisplayItemStack(resultDisplayItem);
        }
        ItemStack resultDisplayItem = item.getDisplayItem(player, multi).getItemStack();
        ItemMeta meta = resultDisplayItem.getItemMeta();
        if (meta == null) {
            return new ObjectDisplayItemStack(resultDisplayItem);
        }
        List<String> lore = new ArrayList<>(CommonUtil.modifyList(player, resultLore,
                "shop", item.getShop(),
                "product", item.getProduct(),
                "index", String.valueOf(index + 1),
                "editing", String.valueOf(editing),
                "edit-mode", String.valueOf(editing)));
        List<String> originalLore = UltimateShop.methodUtil.getItemLore(meta);
        if (originalLore != null && !originalLore.isEmpty()) {
            lore.addAll(originalLore);
        }
        UltimateShop.methodUtil.setItemLore(meta, lore, player);
        resultDisplayItem.setItemMeta(meta);
        return new ObjectDisplayItemStack(resultDisplayItem);
    }

    public ObjectItem getItem() {
        return item;
    }

    public int getIndex() {
        return index;
    }

    public boolean isEditing() {
        return editing;
    }
}
