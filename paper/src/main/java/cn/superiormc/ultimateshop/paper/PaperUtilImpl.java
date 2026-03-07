package cn.superiormc.ultimateshop.paper;

import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PaperUtilImpl implements PacketInventoryUtil.PaperUtil {

    @Override
    public Component modernParse(Player player, String message) {
        return PaperTextUtil.modernParse(message, player);
    }

}
