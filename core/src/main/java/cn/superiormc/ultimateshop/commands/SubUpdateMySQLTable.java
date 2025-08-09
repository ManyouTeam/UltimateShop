package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SubUpdateMySQLTable extends AbstractCommand {

    public SubUpdateMySQLTable() {
        this.id = "updatemysqltable";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        if (SQLDatabase.sqlManager != null) {
            SQLDatabase.updateTable();
            player.sendMessage(TextUtil.pluginPrefix() + " §aSuccessfully update your MySQL table format to new version!");
        } else {
            player.sendMessage(TextUtil.pluginPrefix() + " §cCan not found MySQL database plugin now connecting!");
        }
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        if (SQLDatabase.sqlManager != null) {
            SQLDatabase.updateTable();
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §aSuccessfully update your MySQL table format to new version!");
        } else {
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cCan not found MySQL database plugin now connecting!");
        }
    }
}
