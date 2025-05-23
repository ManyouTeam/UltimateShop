package cn.superiormc.ultimateshop.paper;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SpecialMethodUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaperMethodUtil implements SpecialMethodUtil {

    @Override
    public void dispatchCommand(String command) {
        if (UltimateShop.isFolia) {
            Bukkit.getGlobalRegionScheduler().run(UltimateShop.instance, task -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public void dispatchCommand(Player player, String command) {
        if (UltimateShop.isFolia) {
            player.getScheduler().run(UltimateShop.instance, task -> Bukkit.dispatchCommand(player, command), () -> {
            });
            return;
        }
        Bukkit.dispatchCommand(player, command);
    }

    @Override
    public void dispatchOpCommand(Player player, String command) {
        if (UltimateShop.isFolia) {
            player.getScheduler().run(UltimateShop.instance, task -> {
                boolean playerIsOp = player.isOp();
                try {
                    player.setOp(true);
                    Bukkit.dispatchCommand(player, command);
                } finally {
                    player.setOp(playerIsOp);
                }
            }, () -> {
            });
            return;
        }
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
        if (CommonUtil.getMajorVersion(15)) {
            return ItemStack.deserializeBytes((byte[]) object);
        }
        if (object instanceof ItemStack) {
            return (ItemStack) object;
        }
        return null;
    }

    @Override
    public Object makeItemToObject(ItemStack item) {
        if (CommonUtil.getMajorVersion(15)) {
            return item.serializeAsBytes();
        }
        return item;
    }

    @Override
    public void spawnEntity(Location location, EntityType entity) {
        if (UltimateShop.isFolia) {
            Bukkit.getRegionScheduler().run(UltimateShop.instance, location, task -> location.getWorld().spawnEntity(location, entity));
            return;
        }
        location.getWorld().spawnEntity(location, entity);
    }

    @Override
    public void playerTeleport(Player player, Location location) {
        if (UltimateShop.isFolia) {
            player.teleportAsync(location);
        } else {
            player.teleport(location);
        }
    }

    @Override
    public SkullMeta setSkullMeta(SkullMeta meta, String skull) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
        profile.setProperty(new ProfileProperty("textures", skull));
        meta.setPlayerProfile(profile);
        return meta;
    }

    @Override
    public void setItemName(ItemMeta meta, String name, Player player) {
        if (!CommonUtil.getMinorVersion(17, 1)) {
            meta.setDisplayName(TextUtil.parse(player, name));
            return;
        }
        if (!name.startsWith("&o")) {
            name = "<!i>" + name;
        }
        meta.displayName(PaperTextUtil.modernParse(name, player));
    }

    @Override
    public void setItemItemName(ItemMeta meta, String itemName, Player player) {
        if (!itemName.isEmpty()) {
            if (!itemName.startsWith("&o")) {
                itemName = "<!i>" + itemName;
            }
            meta.itemName(PaperTextUtil.modernParse(itemName, player));
        } else {
            meta.itemName();
        }
    }

    @Override
    public void setItemLore(ItemMeta meta, List<String> lores, Player player) {
        if (!CommonUtil.getMinorVersion(17, 1)) {
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
            return;
        }
        List<Component> veryNewLore = new ArrayList<>();
        for (String lore : lores) {
            for (String singleLore : lore.split("\n")) {
                if (!singleLore.startsWith("&o")) {
                    singleLore = "<!i>" + singleLore;
                }
                veryNewLore.add(PaperTextUtil.modernParse(singleLore, player));
            }
        }
        if (!veryNewLore.isEmpty()) {
            meta.lore(veryNewLore);
        }
    }

    @Override
    public void sendMessage(Player player, String text) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage(PaperTextUtil.modernParse(text));
        } else {
            player.sendMessage(PaperTextUtil.modernParse(text, player));
        }
    }

    @Override
    public Inventory createNewInv(Player player, int size, String text) {
        return Bukkit.createInventory(player, size, PaperTextUtil.modernParse(text, player));
    }

    @Override
    public String legacyParse(String text) {
        if (text == null)
            return "";
        return LegacyComponentSerializer.legacySection().serialize(PaperTextUtil.modernParse(text));
    }

}
