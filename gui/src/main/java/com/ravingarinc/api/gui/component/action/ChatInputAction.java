package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.TriConsumer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class ChatInputAction extends Action {
    private final TriConsumer<BaseGui, Player, String> onResponse;
    private final String description;

    public ChatInputAction(String description, BiConsumer<Player, String> onResponse) {
        this(description, (gui, player, str) -> onResponse.accept(player, str));
    }

    public ChatInputAction(String description, TriConsumer<BaseGui, Player, String> onResponse) {
        super("CHAT_INPUT_ACTION");
        this.onResponse = onResponse;
        this.description = ChatColor.translateAlternateColorCodes('&', description);
    }

    @Override
    public void performAction(BaseGui gui, Player performer) {
        performer.closeInventory();
        for (final String line : description.split("\n")) {
            performer.sendMessage(line);
        }
        Component.CHAT_INPUT_COMPONENT.addListener(gui, performer, onResponse);
    }
}
