package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.listeners.BungeeCordSyncListener;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class BungeeCordManager {

    public static BungeeCordManager bungeeCordManager;

    private BungeeCordSyncListener listener;

    public BungeeCordManager() {
        if (enableThis()) {
            bungeeCordManager = this;
            init();
        }
    }

    public void init() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(UltimateShop.instance, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(UltimateShop.instance, "BungeeCord", listener = new BungeeCordSyncListener());
    }

    public void disable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(UltimateShop.instance, "BungeeCord");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(UltimateShop.instance, "BungeeCord", listener);
    }

    public void sendToOtherServer(String id,
                                  String nowValue,
                                  String lastRefreshTime) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Forward"); // So BungeeCord knows to forward it
            out.writeUTF("ALL");
            out.writeUTF("UltimateShop");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(id);
            msgout.writeUTF(nowValue);
            msgout.writeUTF(lastRefreshTime);
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            Bukkit.getServer().sendPluginMessage(UltimateShop.instance, "BungeeCord", b.toByteArray());
        } catch (IOException e) {
        }
    }

    public void sendToOtherServer(String shop,
                                  String product,
                                  String dataType,
                                  String content) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Forward"); // So BungeeCord knows to forward it
            out.writeUTF("ALL");
            out.writeUTF("UltimateShop");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(shop);
            msgout.writeUTF(product);
            msgout.writeUTF(dataType);
            msgout.writeUTF(Objects.requireNonNullElse(content, "null"));
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            Bukkit.getServer().sendPluginMessage(UltimateShop.instance, "BungeeCord", b.toByteArray());
        } catch (IOException e) {
        }
    }

    public static boolean enableThis() {
        return !UltimateShop.freeVersion && ConfigManager.configManager.getBoolean("bungeecord-sync.enabled");
    }

}
