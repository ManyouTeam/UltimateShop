package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ActionConnect extends AbstractRunAction {

    public ActionConnect() {
        super("connect");
        setRequiredArgs("server");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArray);

            out.writeUTF("Connect");
            out.writeUTF(singleAction.getString("server"));

            Player player = thingRun.getPlayer();

            if (player != null) {
                player.sendPluginMessage(UltimateShop.instance, "BungeeCord", byteArray.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
