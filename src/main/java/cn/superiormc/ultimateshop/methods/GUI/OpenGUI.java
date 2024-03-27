package cn.superiormc.ultimateshop.methods.GUI;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.form.FormCommonGUI;
import cn.superiormc.ultimateshop.gui.form.FormShopGUI;
import cn.superiormc.ultimateshop.gui.inv.BuyMoreGUI;
import cn.superiormc.ultimateshop.gui.inv.CommonGUI;
import cn.superiormc.ultimateshop.gui.inv.SellAllGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.CreateOrEditShopGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OpenGUI {

    public static List<Player> editorWarningCache = new ArrayList<>();

    public static void openShopGUI(Player player, ObjectShop shop, boolean bypassBedrockCheck) {
        if (shop == null) {
            return;
        }
        if (UltimateShop.useGeyser && (bypassBedrockCheck || CommonUtil.isBedrockPlayer(player))) {
            FormShopGUI formShopGUI = new FormShopGUI(player, shop);
            formShopGUI.openGUI();
            formShopGUI.getMenu().doAction(player);
            return;
        }
        ShopGUI gui = new ShopGUI(player, shop);
        gui.openGUI();
        gui.getMenu().doAction(player);
    }

    public static void openCommonGUI(Player player, String fileName, boolean bypassBedrockCheck) {
        if (UltimateShop.useGeyser && (bypassBedrockCheck || CommonUtil.isBedrockPlayer(player))) {
            FormCommonGUI formCommonGUI = new FormCommonGUI(player, fileName);
            formCommonGUI.openGUI();
            formCommonGUI.getMenu().doAction(player);
            return;
        }
        CommonGUI gui = new CommonGUI(player, fileName);
        gui.openGUI();
        gui.getMenu().doAction(player);
    }

    public static void openMoreGUI(Player player, ObjectItem item) {
        BuyMoreGUI gui = new BuyMoreGUI(player, item);
        gui.openGUI();
    }

    public static void openSellAllGUI(Player player) {
        SellAllGUI gui = new SellAllGUI(player);
        gui.openGUI();
    }

    public static void openEditorGUI(Player player) {
        if (!editorWarningCache.contains(player)) {
            player.sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fWelcome to use UltimateShop in-game GUI Editor!");
            player.sendMessage("§fPlease carefully note that this editor has not done, only about 20% part of it has finished.");
            player.sendMessage("§fThis is because UltimateShop has high customization, and I have to consider every situation.");
            player.sendMessage("§fAnd this editor is not §cSTABLE §fto use, make backup of your configs before you use it.");
            player.sendMessage("§fType §b/shop editor §fagain to enter the editor.");
            if (UltimateShop.freeVersion) {
                player.sendMessage("§cWarning: You are now using free version, GUI editor will not save your changes!");
                player.sendMessage("§cYou can only view the GUI and can not edit config by this way, you have to view");
                player.sendMessage("§cplugin Wiki and manually adjusting the configuration file of the plugin.");
            }
            editorWarningCache.add(player);
            return;
        }
        if (InvGUI.guiCache.containsKey(player)) {
            LanguageManager.languageManager.sendStringText(player, "editor.already-editing");
            return;
        }
        CreateOrEditShopGUI gui = new CreateOrEditShopGUI(player);
        gui.openGUI();
    }

}
