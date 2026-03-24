package cn.superiormc.ultimateshop.spigot;

import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.SpecialMethodUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpigotMethodUtil implements SpecialMethodUtil {

    private static final Pattern TEXTURE_URL_PATTERN = Pattern.compile("\"url\"\\s*:\\s*\"(https?://textures\\.minecraft\\.net/texture/[^\"]+)\"");

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
        if (!CommonUtil.getMajorVersion(19)) {
            return meta;
        }
        try {
            URL skinUrl = resolveSkinUrl(skull);
            if (skinUrl == null) {
                return meta;
            }
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "custom_head");
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(skinUrl);
            profile.setTextures(textures);

            meta.setOwnerProfile(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return meta;
    }

    @Override
    public String serializeSkull(SkullMeta meta) {
        if (!CommonUtil.getMajorVersion(19)) {
            return null;
        }
        try {
            PlayerProfile ownerProfile = meta.getOwnerProfile();
            if (ownerProfile != null) {
                ownerProfile.getTextures();
                URL skinUrl = ownerProfile.getTextures().getSkin();
                if (skinUrl != null) {
                    return encodeSkinUrl(skinUrl);
                }
            }
        } catch (Throwable ignored) {
        }

        if (meta.getOwningPlayer() != null) {
            return meta.getOwningPlayer().getName();
        }
        return null;
    }

    private URL resolveSkinUrl(String skull) throws Exception {
        if (skull == null) {
            return null;
        }

        String trimmedSkull = skull.trim();
        if (trimmedSkull.isEmpty()) {
            return null;
        }

        if (trimmedSkull.startsWith("http://textures.minecraft.net/texture/")
                || trimmedSkull.startsWith("https://textures.minecraft.net/texture/")) {
            return new URL(trimmedSkull);
        }

        String json = new String(Base64.getDecoder().decode(trimmedSkull), StandardCharsets.UTF_8);
        Matcher matcher = TEXTURE_URL_PATTERN.matcher(json);

        if (matcher.find()) {
            return new URL(matcher.group(1));
        }
        return null;
    }

    private String encodeSkinUrl(URL skinUrl) {
        String textureJson = "{\"textures\":{\"SKIN\":{\"url\":\"" + skinUrl + "\"}}}";
        return Base64.getEncoder().encodeToString(textureJson.getBytes(StandardCharsets.UTF_8));
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
            for (String singleLore : lore.split("\\\\n")) {
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
    public void sendChat(Player player, String text) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage(TextUtil.parse(text));
        } else {
            player.sendMessage(TextUtil.parse(text, player));
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) {
            return;
        }

        player.sendTitle(TextUtil.parse(title, player), TextUtil.parse(subTitle, player), fadeIn, stay, fadeOut);
    }

    @Override
    public void sendActionBar(Player player, String message) {
        if (player == null) {
            return;
        }

        player.spigot().sendMessage(
                net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(TextUtil.parse(message, player))
        );
    }

    @Override
    public void sendBossBar(Player player,
                            String title,
                            float progress,
                            String color,
                            String style) {
        if (player == null) {
            return;
        }

        BossBar bar = Bukkit.createBossBar(
                TextUtil.parse(title, player),
                color == null ? BarColor.WHITE : BarColor.valueOf(color.toUpperCase()),
                style == null ? BarStyle.SOLID : BarStyle.valueOf(style.toUpperCase())
        );

        bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        bar.addPlayer(player);
        bar.setVisible(true);

        SchedulerUtil.runTaskLater(bar::removeAll, 60);
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

    @Override
    public ConfigurationSection serializeItemStack(ItemStack item) {
        return null;
    }
}
