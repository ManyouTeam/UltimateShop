package cn.superiormc.ultimateshop.gui;

import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Prompt {

    private final String description;

    private final BiConsumer<Player, String> handler;

    private final Consumer<Player> cancelHandler;

    private final boolean reopenOnCancel;

    public Prompt(String description, BiConsumer<Player, String> handler, Consumer<Player> cancelHandler) {
        this(description, handler, cancelHandler, true);
    }

    public Prompt(String description,
                  BiConsumer<Player, String> handler,
                  Consumer<Player> cancelHandler,
                  boolean reopenOnCancel) {
        this.description = description;
        this.handler = handler;
        this.cancelHandler = cancelHandler;
        this.reopenOnCancel = reopenOnCancel;
    }

    public String getDescription() {
        return description;
    }

    public void handle(Player player, String input) {
        handler.accept(player, input);
    }

    public void cancel(Player player) {
        if (cancelHandler != null) {
            cancelHandler.accept(player);
        }
    }

    public boolean shouldReopenOnCancel() {
        return reopenOnCancel;
    }
}
