package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.utils.PaperUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ActionAnnouncement extends AbstractRunAction {

    public ActionAnnouncement() {
        super("announcement");
        setRequiredArgs("message");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player p : players) {
            PaperUtil.sendMessage(p, singleAction.getString("message", player, amount));
        }
    }
}
