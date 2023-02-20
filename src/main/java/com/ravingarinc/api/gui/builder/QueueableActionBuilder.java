package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.Queueable;
import com.ravingarinc.api.gui.component.action.Action;

public class QueueableActionBuilder<T extends BaseGui> extends BaseActionBuilder {
    private final GuiBuilder<T> builder;

    public QueueableActionBuilder(final GuiBuilder<T> builder, final boolean persistent, final String lastMenu) {
        super(new Queueable(builder.get().getIdentifier(), persistent), lastMenu);
        this.builder = builder;
    }

    @Override
    public void build() {
        builder.get().addChild(() -> (Component) getActionable());
    }

    public GuiBuilder<T> finalise() {
        builder.handleLastQueueable();
        return builder;
    }

    @Override
    public QueueableActionBuilder<T> addMiscAction(final Action action) {
        addAction(action);
        return this;
    }
}
