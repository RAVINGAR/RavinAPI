package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class RunnableAction extends Action {
    private final BiConsumer<BaseGui, Player> consumer;

    public RunnableAction(final Runnable runnable) {
        this((g, p) -> runnable.run());
    }

    public RunnableAction(final BiConsumer<BaseGui, Player> consumer) {
        super("Runnable_Action");
        this.consumer = consumer;
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        consumer.accept(gui, performer);
    }
}
