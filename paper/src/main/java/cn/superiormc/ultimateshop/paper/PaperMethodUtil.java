package cn.superiormc.ultimateshop.paper;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SpecialMethodUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaperMethodUtil implements SpecialMethodUtil {

    @Override
    public String methodID() {
        return "paper";
    }

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
        if (object instanceof ItemStack) {
            ErrorManager.errorManager.sendErrorMessage("§6Warning: The item you try obtained is using legacy format!");
            return (ItemStack) object;
        }
        if (CommonUtil.getMajorVersion(15)) {
            return ItemStack.deserializeBytes((byte[]) object);
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
        if (PaperTextUtil.containsLegacyCodes(name)) {
            name = "<!i>" + name;
        }
        meta.displayName(PaperTextUtil.modernParse(name, player));
    }

    @Override
    public void setItemItemName(ItemMeta meta, String itemName, Player player) {
        if (!itemName.isEmpty()) {
            if (PaperTextUtil.containsLegacyCodes(itemName)) {
                itemName = "<!i>" + itemName;
            }
            meta.itemName(PaperTextUtil.modernParse(itemName, player));
        } else {
            meta.itemName();
        }
    }

    @Override
    public void setItemLore(ItemMeta meta, List<String> lores, Player player) {
        List<Component> veryNewLore = new ArrayList<>();
        for (String lore : lores) {
            for (String singleLore : lore.split("\n")) {
                if (PaperTextUtil.containsLegacyCodes(singleLore)) {
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
    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        player.showTitle(Title.title(PaperTextUtil.modernParse(title),
                PaperTextUtil.modernParse(subTitle),
                Title.Times.times(Ticks.duration(fadeIn),
                        Ticks.duration(stay),
                        Ticks.duration(fadeOut))));
    }

    @Override
    public Inventory createNewInv(Player player, int size, String text) {
        return Bukkit.createInventory(player, size, PaperTextUtil.modernParse(text, player));
    }

    @Override
    public String legacyParse(String text) {
        if (text == null) {
            return "";
        }
        if (!ConfigManager.configManager.getBoolean("config-files.force-parse-mini-message")) {
            return TextUtil.colorize(text);
        }
        return LegacyComponentSerializer.legacySection().serialize(PaperTextUtil.modernParse(text));
    }

    @Override
    public String getItemName(ItemMeta meta) {
        return PaperTextUtil.changeToString(meta.displayName());
    }

    @Override
    public String getItemItemName(ItemMeta meta) {
        return PaperTextUtil.changeToString(meta.itemName());
    }

    @Override
    public List<String> getItemLore(ItemMeta meta) {
        return PaperTextUtil.changeToString(meta.lore());
    }

    @Override
    public ItemStack editItemStack(ItemStack item, Player player, ConfigurationSection section, int amount, String... args) {
        if (!CommonUtil.getMinorVersion(21, 5) || UltimateShop.freeVersion) {
            return item;
        }
        return BuildItemPaper.editItemStack(item, player, section, amount, args);
    }

}
