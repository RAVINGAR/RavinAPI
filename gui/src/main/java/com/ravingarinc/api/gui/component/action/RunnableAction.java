package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.ActionHolder;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RunnableAction extends Action {
    private final BiConsumer<BaseGui, Player> consumer;

    @Deprecated
    public RunnableAction(final Consumer<ActionHolder> consumer) {
        this((gui, p) -> consumer.accept(new ActionHolder(gui, p)));
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
