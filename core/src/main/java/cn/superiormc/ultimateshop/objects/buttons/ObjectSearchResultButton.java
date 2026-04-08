package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ObjectSearchResultButton extends AbstractButton {

    private final ObjectItem item;

    private final List<String> resultLore;

    public ObjectSearchResultButton(ObjectItem item, List<String> resultLore) {
        this.item = item;
        this.resultLore = resultLore;
        this.type = ButtonType.SEARCH_RESULT;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        ItemStack resultDisplayItem = item.getDisplayItem(player, multi).getItemStack();
        ItemMeta meta = resultDisplayItem.getItemMeta();
        if (meta == null) {
            return new ObjectDisplayItemStack(resultDisplayItem);
        }

        List<String> lore = new ArrayList<>(CommonUtil.modifyList(player, resultLore,
                "shop", item.getShop(),
                "product", item.getProduct()));
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
}
