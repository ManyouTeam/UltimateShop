package cn.superiormc.ultimateshop.spigot;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.SpecialMethodUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpigotMethodUtil implements SpecialMethodUtil {

    @Override
    public String methodID() {
        return "spigot";
    }

    @Override
    public void dispatchCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public void dispatchCommand(Player player, String command) {
        Bukkit.dispatchCommand(player, command);
    }

    @Override
    public void dispatchOpCommand(Player player, String command) {
        boolean playerIsOp = player.isOp();
        try {
            player.setOp(true);
            Bukkit.dispatchCommand(player, command);
        } finally {
            player.setOp(playerIsOp);
        }
    }

    @Override
    public ItemStack getItemObject(Object object) {
        if (object instanceof ItemStack) {
            return (ItemStack) object;
        }
        return null;
    }

    @Override
    public Object makeItemToObject(ItemStack item) {
        return item;
    }

    @Override
    public void spawnEntity(Location location, EntityType entity) {
        location.getWorld().spawnEntity(location, entity);
    }

    @Override
    public void playerTeleport(Player player, Location location) {
        player.teleport(location);
    }

    @Override
    public SkullMeta setSkullMeta(SkullMeta meta, String skull) {
        if (UltimateShop.newSkullMethod) {
            try {
                Class<?> profileClass = Class.forName("net.minecraft.world.item.component.ResolvableProfile");
                Constructor<?> constroctor = profileClass.getConstructor(GameProfile.class);
                GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                profile.getProperties().put("textures", new Property("textures", skull));
                try {
                    Method mtd = meta.getClass().getDeclaredMethod("setProfile", profileClass);
                    mtd.setAccessible(true);
                    mtd.invoke(meta, constroctor.newInstance(profile));
                } catch (Exception exception) {
                    exception.printStackTrace();
                    ErrorManager.errorManager.sendErrorMessage("§cError: Can not parse skull texture in a item!");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "");
            profile.getProperties().put("textures", new Property("textures", skull));
            try {
                Method mtd = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                mtd.setAccessible(true);
                mtd.invoke(meta, profile);
            } catch (Exception exception) {
                exception.printStackTrace();
                ErrorManager.errorManager.sendErrorMessage("§cError: Can not parse skull texture in a item!");
            }
        }
        return meta;
    }

    @Override
    public void setItemName(ItemMeta meta, String name, Player player) {
        meta.setDisplayName(TextUtil.parse(player, name));
    }

    @Override
    public void setItemItemName(ItemMeta meta, String itemName, Player player) {
        if (itemName.isEmpty()) {
            meta.setItemName(" ");
        } else {
            meta.setItemName(TextUtil.parse(player, itemName));
        }
    }

    @Override
    public void setItemLore(ItemMeta meta, List<String> lores, Player player) {
        List<String> newLore = new ArrayList<>();
        for (String lore : lores) {
            for (String singleLore : lore.split("\n")) {
                if (singleLore.isEmpty()) {
                    newLore.add(" ");
                    continue;
                }
                newLore.add(TextUtil.parse(singleLore, player));
            }
        }
        if (!newLore.isEmpty()) {
            meta.setLore(newLore);
        }
    }

    @Override
    public void sendMessage(Player player, String text) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.parse(text));
        } else {
            player.sendMessage(TextUtil.parse(text, player));
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
    }

    @Override
    public Inventory createNewInv(Player player, int size, String text) {
        return Bukkit.createInventory(player, size, TextUtil.parse(text, player));
    }

    @Override
    public String legacyParse(String text) {
        if (text == null)
            return "";
        return TextUtil.colorize(text);
    }

    @Override
    public String getItemName(ItemMeta meta) {
        return meta.getDisplayName();
    }

    @Override
    public String getItemItemName(ItemMeta meta) {
        return meta.getItemName();
    }

    @Override
    public List<String> getItemLore(ItemMeta meta) {
        return meta.getLore();
    }

    @Override
    public ItemStack editItemStack(ItemStack item, Player player, ConfigurationSection section, int amount, String... args) {
        return item;
    }
}
