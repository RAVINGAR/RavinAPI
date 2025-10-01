package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.TriConsumer;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ChatInputAction extends Action {
    private final TriConsumer<BaseGui, Player, String> onResponse;
    private final Function<BaseGui, net.kyori.adventure.text.Component> description;

    @Deprecated
    public ChatInputAction(String description, BiConsumer<Player, String> onResponse) {
        this((g) -> Component.format(description), onResponse);
    }

    public ChatInputAction(String description, TriConsumer<BaseGui, Player, String> onResponse) {
        this((g) -> Component.format(description), onResponse);
    }

    @Deprecated
    public ChatInputAction(net.kyori.adventure.text.Component description, BiConsumer<Player, String> onResponse) {
        this((g) -> description, onResponse);
    }

    public ChatInputAction(net.kyori.adventure.text.Component description, TriConsumer<BaseGui, Player, String> onResponse) {
        this((g) -> description, onResponse);
    }

    @Deprecated
    public ChatInputAction(Function<BaseGui, net.kyori.adventure.text.Component> description, BiConsumer<Player, String> onResponse) {
        this(description, (gui, player, str) -> onResponse.accept(player, str));
    }

    public ChatInputAction(Function<BaseGui, net.kyori.adventure.text.Component> description, TriConsumer<BaseGui, Player, String> onResponse) {
        super("CHAT_INPUT_ACTION");
        this.onResponse = onResponse;
        this.description = description;
    }

    @Override
    public void performAction(BaseGui gui, Player performer) {
        performer.closeInventory();
        performer.sendMessage(description.apply(gui));
        Component.CHAT_INPUT_COMPONENT.addListener(gui, performer, onResponse);
    }
}
