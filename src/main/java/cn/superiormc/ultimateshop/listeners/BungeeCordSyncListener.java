package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class BungeeCordSyncListener implements PluginMessageListener {


    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        try {
            String subChannel = in.readUTF();
            if (!subChannel.equals("UltimateShop")) {
                return;
            }
            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);
            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            String shop = msgin.readUTF();
            ObjectShop tempVal1 = ConfigManager.configManager.getShop(shop);
            if (tempVal1 == null) {
                ObjectRandomPlaceholder tempVal3 = ConfigManager.configManager.getRandomPlaceholder(shop);
                if (tempVal3 == null) {
                    return;
                } else {
                    String nowValue = msgin.readUTF();
                    String refreshDoneTime = msgin.readUTF();
                    CacheManager.cacheManager.serverCache.setRandomPlaceholderCache(tempVal3,
                            refreshDoneTime, CommonUtil.translateString(nowValue));
                    return;
                }
            }
            String product = msgin.readUTF();
            ObjectItem tempVal2 = tempVal1.getProduct(product);
            if (tempVal2 == null) {
                return;
            }
            ObjectUseTimesCache useTimesCache = CacheManager.cacheManager.serverCache.getUseTimesCache().get(tempVal2);
            if (useTimesCache == null) {
                useTimesCache = CacheManager.cacheManager.serverCache.createUseTimesCache(tempVal2);
            }
            String typeMode = msgin.readUTF();
            switch (typeMode) {
                case "buy-times":
                    useTimesCache.setBuyUseTimes(Integer.parseInt(msgin.readUTF()), true);
                    break;
                case "sell-times":
                    useTimesCache.setSellUseTimes(Integer.parseInt(msgin.readUTF()), true);
                    break;
                case "last-buy-time":
                    useTimesCache.setLastBuyTime(CommonUtil.stringToTime(msgin.readUTF()), true);
                    break;
                case "last-sell-time":
                    useTimesCache.setLastSellTime(CommonUtil.stringToTime(msgin.readUTF()), true);
                    break;
                case "cooldown-buy-time":
                    useTimesCache.setCooldownBuyTime(true);
                    break;
                case "cooldown-sell-time":
                    useTimesCache.setCooldownSellTime(true);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
