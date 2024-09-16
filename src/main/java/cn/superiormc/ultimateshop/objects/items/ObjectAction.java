package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.PaperUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectAction {

    private final List<String> everyAction = new ArrayList<>();

    private final List<String> onceAction = new ArrayList<>();

    private ObjectShop shop = null;

    private ObjectItem item = null;

    private boolean isEmpty = false;

    private ProductTradeStatus lastTradeStatus = null;

    public ObjectAction() {
        this.isEmpty = true;
    }

    public ObjectAction(List<String> action) {
        this.isEmpty = action.isEmpty();
        for (String s : action) {
            // Means this action will be active only once.
            if (s.endsWith("-o")) {
                onceAction.add(s.substring(0, s.length() - 2));
            }
            else {
                everyAction.add(s);
            }
        }
    }

    public ObjectAction(List<String> action, ObjectShop shop) {
        this.isEmpty = action.isEmpty();
        for (String s : action) {
            if (s.endsWith("-o")) {
                onceAction.add(s.substring(0, s.length() - 2));
            }
            else {
                everyAction.add(s);
            }
        }
        this.shop = shop;
    }

    public ObjectAction(List<String> action, ObjectItem item) {
        this.isEmpty = action.isEmpty();
        for (String s : action) {
            if (s.endsWith("-o")) {
                onceAction.add(s.substring(0, s.length() - 2));
            }
            else {
                everyAction.add(s);
            }
        }
        this.shop = item.getShopObject();
        this.item = item;
    }

    public void doAction(Player player, int times, double amount) {
        if (everyAction.isEmpty() && onceAction.isEmpty()) {
            return;
        }
        checkAction(player, onceAction, times, amount, false, null);
        checkAction(player, everyAction, times, amount, false, null);
    }

    public void doAction(Player player, int times, int multi, boolean sellAll) {
        doAction(player, times, multi, sellAll, null);
    }

    public void doAction(Player player, int times, int multi, boolean sellAll, ClickType type) {
        if (everyAction.isEmpty() && onceAction.isEmpty()) {
            return;
        }
        checkAction(player, onceAction, times, multi, sellAll, type);
        for (int i = 0 ; i < multi ; i ++) {
            checkAction(player, everyAction, times, multi, sellAll, type);
        }
    }

    private void checkAction(Player player, List<String> actions, int times, double multi, boolean sellAll, ClickType type) {
        if (player == null) {
            return;
        }
        for (String singleAction : actions) {
            Pattern pattern = Pattern.compile("-\\d+$");
            Matcher matcher = pattern.matcher(singleAction);
            if (matcher.find()) {
                String number = matcher.group().substring(1);
                int realNumber = Integer.parseInt(number);
                if (times + 1 != realNumber) {
                    continue;
                }
                singleAction = singleAction.replaceAll("-\\d+$", "");
            }
            if (singleAction.endsWith("-b")) {
                singleAction = singleAction.substring(0, singleAction.length() - 2);
                if (sellAll) {
                    continue;
                }
            }
            if (singleAction.startsWith("@") && type != null) {
                String clickType = singleAction.split("@")[1];
                if (!clickType.equals(type.name())) {
                    continue;
                }
                singleAction = singleAction.substring(2 + clickType.length());
            }
            singleAction = replacePlaceholder(singleAction, player, multi);
            String[] splits = singleAction.split(";;");
            if (singleAction.startsWith("none")) {
                return;
            } else if (singleAction.startsWith("sound: ")) {
                // By: iKiwo
                String soundData = singleAction.substring(7); // "sound: LEVEL_UP;volume;pitch"
                String[] soundParts = soundData.split(";;");
                if (soundParts.length >= 1) {
                    String soundName = soundParts[0];
                    float volume = 1.0f;
                    float pitch = 1.0f;
                    if (soundParts.length >= 2) {
                        try {
                            volume = Float.parseFloat(soundParts[1]);
                        } catch (NumberFormatException e) {
                            ErrorManager.errorManager
                                    .sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Invalid volume value in sound action.");
                        }
                    }
                    if (soundParts.length >= 3) {
                        try {
                            pitch = Float.parseFloat(soundParts[2]);
                        } catch (NumberFormatException e) {
                            ErrorManager.errorManager
                                    .sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Invalid pitch value in sound action.");
                        }
                    }
                    Location location = player.getLocation();
                    player.playSound(location, soundName, volume, pitch);
                } else {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Invalid sound action format.");
                }
            } else if (singleAction.startsWith("message: ")) {
                PaperUtil.sendMessage(player, singleAction.substring(9));
            } else if (singleAction.startsWith("open_menu: ")) {
                OpenGUI.openCommonGUI(player, singleAction.substring(11), false, true);
            } else if (singleAction.startsWith("shop_menu: ")) {
                OpenGUI.openShopGUI(player, ConfigManager.configManager.getShop(singleAction.substring(11)), false, true);
            } else if (singleAction.startsWith("announcement: ")) {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                for (Player p : players) {
                    PaperUtil.sendMessage(p, singleAction.substring(14));
                }
            } else if (singleAction.startsWith("effect: ") && splits.length == 3) {
                PotionEffectType potionEffectType = PotionEffectType.getByName(singleAction.substring(8).split(";;")[0].toUpperCase());
                if (potionEffectType == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not found potion effect: " +
                            singleAction.substring(8).split(";;")[0] + ".");
                    continue;
                }
                PotionEffect effect = new PotionEffect(potionEffectType,
                        Integer.parseInt(singleAction.substring(8).split(";;")[2]),
                        Integer.parseInt(singleAction.substring(8).split(";;")[1]) - 1,
                        true,
                        true,
                        true);
                player.addPotionEffect(effect);
            } else if (singleAction.startsWith("entity_spawn: ")) {
                if (singleAction.split(";;").length == 1) {
                    EntityType entity = EntityType.valueOf(singleAction.substring(14).split(";;")[0].toUpperCase());
                    Location location = player.getLocation();
                    if (UltimateShop.isFolia) {
                        Bukkit.getRegionScheduler().run(UltimateShop.instance, location, task -> location.getWorld().spawnEntity(location, entity));
                        continue;
                    }
                    location.getWorld().spawnEntity(player.getLocation(), entity);
                } else if (singleAction.split(";;").length == 5) {
                    World world = Bukkit.getWorld(singleAction.substring(14).split(";;")[1]);
                    Location location = new Location(world,
                            Double.parseDouble(singleAction.substring(14).split(";;")[2]),
                            Double.parseDouble(singleAction.substring(14).split(";;")[3]),
                            Double.parseDouble(singleAction.substring(14).split(";;")[4]));
                    EntityType entity = EntityType.valueOf(singleAction.substring(14).split(";;")[0].toUpperCase());
                    if (location.getWorld() == null) {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your entity_spawn action in shop configs can not being correctly load.");
                        continue;
                    }
                    if (UltimateShop.isFolia) {
                        Bukkit.getRegionScheduler().run(UltimateShop.instance, location, task -> location.getWorld().spawnEntity(location, entity));
                        continue;
                    }
                    location.getWorld().spawnEntity(location, entity);
                }
            } else if (singleAction.startsWith("teleport: ")) {
                if (singleAction.split(";;").length == 4) {
                    Location loc = new Location(Bukkit.getWorld(singleAction.substring(10).split(";;")[0]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[1]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[2]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[3]),
                            player.getLocation().getYaw(),
                            player.getLocation().getPitch());
                    if (UltimateShop.isFolia) {
                        player.teleportAsync(loc);
                    } else {
                        player.teleport(loc);
                    }
                }
                else if (singleAction.split(";;").length == 6) {
                    Location loc = new Location(Bukkit.getWorld(singleAction.split(";;")[0]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[1]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[2]),
                            Double.parseDouble(singleAction.substring(10).split(";;")[3]),
                            Float.parseFloat(singleAction.substring(10).split(";;")[4]),
                            Float.parseFloat(singleAction.substring(10).split(";;")[5]));
                    if (UltimateShop.isFolia) {
                        player.teleportAsync(loc);
                    } else {
                        player.teleport(loc);
                    }
                }
                else {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your teleport action in shop configs can not being correctly load.");
                }
            } else if (CommonUtil.checkPluginLoad("MythicMobs") && singleAction.startsWith("mythicmobs_spawn: ")) {
                if (singleAction.substring(18).split(";;").length == 1) {
                    CommonUtil.summonMythicMobs(player.getLocation(),
                            singleAction.substring(18).split(";;")[0],
                            1);
                }
                else if (singleAction.substring(18).split(";;").length == 2) {
                    CommonUtil.summonMythicMobs(player.getLocation(),
                            singleAction.substring(18).split(";;")[0],
                            Integer.parseInt(singleAction.substring(18).split(";;")[1]));
                }
                else if (singleAction.substring(18).split(";;").length == 5) {
                    World world = Bukkit.getWorld(singleAction.substring(18).split(";;")[1]);
                    Location location = new Location(world,
                            Double.parseDouble(singleAction.substring(18).split(";;")[2]),
                            Double.parseDouble(singleAction.substring(18).split(";;")[3]),
                            Double.parseDouble(singleAction.substring(18).split(";;")[4])
                    );
                    CommonUtil.summonMythicMobs(location,
                            singleAction.substring(18).split(";;")[0],
                            1);
                }
                else if (singleAction.substring(18).split(";;").length == 6) {
                    World world = Bukkit.getWorld(singleAction.substring(18).split(";;")[2]);
                    Location location = new Location(world,
                            Double.parseDouble(singleAction.substring(18).split(";;")[3]),
                            Double.parseDouble(singleAction.substring(18).split(";;")[4]),
                            Double.parseDouble(singleAction.substring(18).split(";;")[5])
                    );
                    CommonUtil.summonMythicMobs(location,
                            singleAction.substring(18).split(";;")[0],
                            Integer.parseInt(singleAction.substring(18).split(";;")[1]));
                }
                else {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your mythicmobs_spawn action in shop configs can not being correctly load.");
                }
            } else if (singleAction.startsWith("console_command: ")) {
                CommonUtil.dispatchCommand(singleAction.substring(17));
            } else if (singleAction.startsWith("player_command: ")) {
                CommonUtil.dispatchCommand(player, singleAction.substring(16));
            } else if (singleAction.startsWith("op_command: ")) {
                CommonUtil.dispatchOpCommand(player, singleAction.substring(12));
            } else if (singleAction.equals("close")) {
                if (UltimateShop.isFolia) {
                    Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance, task -> player.closeInventory(), 2L);
                } else {
                    Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> player.closeInventory(), 2L);
                }
            } else if (singleAction.startsWith("buy: ") && splits.length == 3) {
                lastTradeStatus = BuyProductMethod.startBuy(singleAction.substring(5).split(";;")[0],
                        singleAction.substring(5).split(";;")[1],
                        player,
                        true,
                        false,
                        Integer.parseInt(singleAction.substring(5).split(";;")[2]));
            } else if (singleAction.startsWith("sell: ") && splits.length == 3) {
                lastTradeStatus = SellProductMethod.startSell(singleAction.substring(6).split(";;")[0],
                        singleAction.substring(6).split(";;")[1],
                        player,
                        true,
                        false,
                        Integer.parseInt(singleAction.substring(5).split(";;")[2]));
            } else if (singleAction.startsWith("sellall: ") && splits.length == 2) {
                lastTradeStatus = SellProductMethod.startSell(singleAction.substring(9).split(";;")[0],
                        singleAction.substring(9).split(";;")[1],
                        player,
                        true,
                        false,
                        true,
                        1);
            }
        }
    }

    public ProductTradeStatus getLastTradeStatus() {
        return lastTradeStatus;
    }

    private String replacePlaceholder(String str, Player player, double multi) {
        str = str.replace("{world}", player.getWorld().getName())
                .replace("{amount}", String.valueOf(multi))
                .replace("{player_x}", String.valueOf(player.getLocation().getX()))
                .replace("{player_y}", String.valueOf(player.getLocation().getY()))
                .replace("{player_z}", String.valueOf(player.getLocation().getZ()))
                .replace("{player_pitch}", String.valueOf(player.getLocation().getPitch()))
                .replace("{player_yaw}", String.valueOf(player.getLocation().getYaw()))
                .replace("{player}", player.getName());
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            str = PlaceholderAPI.setPlaceholders(player, str);
        }
        if (shop != null) {
            str = str.replace("{shop-menu}", shop.getShopMenu())
                    .replace("{shop}", shop.getShopName()
                    .replace("{shop-name}", shop.getShopDisplayName()));
        }
        if (item != null) {
            str = str.replace("{item}", item.getProduct())
                    .replace("{item-name}", item.getDisplayName(player));
        }
        return str;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
