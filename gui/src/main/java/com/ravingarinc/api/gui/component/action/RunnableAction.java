package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;

import java.util.function.Consumer;

public class RunnableAction extends Action {
    private final Consumer<BaseGui> consumer;

    public RunnableAction(final Consumer<BaseGui> consumer) {
        super("Runnable_Action");
        this.consumer = consumer;
    }

    @Override
    public void performAction(final BaseGui gui) {
        consumer.accept(gui);
    }
}
