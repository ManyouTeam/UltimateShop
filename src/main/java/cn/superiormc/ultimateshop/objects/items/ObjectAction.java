package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.TextUtil;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectAction {

    private List<String> everyAction = new ArrayList<>();

    private List<String> onceAction = new ArrayList<>();

    private ObjectShop shop = null;

    public ObjectAction() {
        // Empty...
    }

    public ObjectAction(List<String> action) {
        for (String s : action) {
            if (s.endsWith("-o")) {
                s = s.substring(0, s.length() - 3);
                onceAction.add(s);
            }
            else {
                everyAction.add(s);
            }
        }
    }

    public ObjectAction(List<String> action, ObjectShop shop) {
        for (String s : action) {
            if (s.endsWith("-o")) {
                s = s.substring(0, s.length() - 3);
                onceAction.add(s);
            }
            else {
                everyAction.add(s);
            }
        }
        this.shop = shop;
    }

    public void doAction(Player player, int multi){
        if (everyAction.isEmpty() && onceAction.isEmpty()) {
            return;
        }
        checkAction(player, onceAction, multi);
        for (int i = 0 ; i < multi ; i ++) {
            checkAction(player, everyAction, multi);
        }
    }

    private void checkAction(Player player, List<String> actions, int multi) {
        for (String singleAction : actions) {
            singleAction = replacePlaceholder(singleAction, player, multi);
            if (singleAction.startsWith("none")) {
                return;
            } else if (singleAction.startsWith("message: ")) {
                player.sendMessage(TextUtil.parse(singleAction.substring(9), player));
            } else if (singleAction.startsWith("open_menu: ")) {
                OpenGUI.openCommonGUI(player, singleAction.substring(11));
            } else if (singleAction.startsWith("shop_menu: ")) {
                OpenGUI.openShopGUI(player, ConfigManager.configManager.getShop(singleAction.substring(11)));
            } else if (singleAction.startsWith("announcement: ")) {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                for (Player p : players) {
                    p.sendMessage(TextUtil.parse(singleAction.substring(14), player));
                }
            } else if (singleAction.startsWith("effect: ")) {
                try {
                    if (PotionEffectType.getByName(singleAction.substring(8).split(";;")[0].toUpperCase()) == null) {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not found potion effect: " +
                                singleAction.split(";;")[0] + ".");
                    }
                    PotionEffect effect = new PotionEffect(PotionEffectType.getByName(singleAction.split(";;")[0].toUpperCase()),
                            Integer.parseInt(singleAction.substring(8).split(";;")[2]),
                            Integer.parseInt(singleAction.substring(8).split(";;")[1]) - 1,
                            true,
                            true,
                            true);
                    player.addPotionEffect(effect);
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your effect action in totem configs can not being correctly load.");
                }
            } else if (singleAction.startsWith("teleport: ")) {
                try {
                    if (singleAction.split(";;").length == 4) {
                        Location loc = new Location(Bukkit.getWorld(singleAction.substring(10).split(";;")[0]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[1]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[2]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[3]),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        player.teleport(loc);
                    }
                    else if (singleAction.split(";;").length == 6) {
                        Location loc = new Location(Bukkit.getWorld(singleAction.split(";;")[0]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[1]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[2]),
                                Double.parseDouble(singleAction.substring(10).split(";;")[3]),
                                Float.parseFloat(singleAction.substring(10).split(";;")[4]),
                                Float.parseFloat(singleAction.substring(10).split(";;")[5]));
                        player.teleport(loc);
                    }
                    else {
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your teleport action in totem configs can not being correctly load.");
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your teleport action in totem configs can not being correctly load.");
                }
            } else if (CommonUtil.checkPluginLoad("MythicMobs") && singleAction.startsWith("mythicmobs_spawn: ")) {
                 try {
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
                         ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your mythicmobs_spawn action in totem configs can not being correctly load.");
                     }
                 }
                 catch (ArrayIndexOutOfBoundsException e) {
                     ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your mythicmobs_spawn action in totem configs can not being correctly load.");
                 }
            } else if (singleAction.startsWith("console_command: ")) {
                CommonUtil.dispatchCommand(singleAction.substring(17));
            } else if (singleAction.startsWith("player_command: ") && player != null) {
                CommonUtil.dispatchCommand(player, singleAction.substring(16));
            } else if (singleAction.equals("close") && player != null) {
                Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> {
                    player.closeInventory();
                }, 2L);
            }
        }
    }
    private String replacePlaceholder(String str, Player player, int multi){
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
                    .replace("{shop}", shop.getShopName());
        }
        return str;
    }
}
