package cn.superiormc.ultimateshop.editor;

import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EditorPrompt {

    private final String description;

    private final BiConsumer<Player, String> handler;

    private final Consumer<Player> cancelHandler;

    public EditorPrompt(String description, BiConsumer<Player, String> handler, Consumer<Player> cancelHandler) {
        this.description = description;
        this.handler = handler;
        this.cancelHandler = cancelHandler;
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
}
