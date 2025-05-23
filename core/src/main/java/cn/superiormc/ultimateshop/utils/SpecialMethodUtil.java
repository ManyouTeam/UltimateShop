package cn.superiormc.ultimateshop.utils;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public interface SpecialMethodUtil {

    void dispatchCommand(String command);

    void dispatchCommand(Player player, String command);

    void dispatchOpCommand(Player player, String command);

    ItemStack getItemObject(Object object);

    Object makeItemToObject(ItemStack item);

    void spawnEntity(Location location, EntityType entity);

    void playerTeleport(Player player, Location location);

    SkullMeta setSkullMeta(SkullMeta meta, String skull);

    void setItemName(ItemMeta meta, String name, Player player);

    void setItemItemName(ItemMeta meta, String itemName, Player player);

    void setItemLore(ItemMeta meta, List<String> lore, Player player);

    void sendMessage(Player player, String text);

    Inventory createNewInv(Player player, int size, String text);

    String legacyParse(String text);
}
