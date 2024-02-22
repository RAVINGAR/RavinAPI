package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.Queueable;

public class QueueableActionBuilder<T extends BaseGui> extends BaseActionBuilder<GuiBuilder<T>> {
    private final GuiBuilder<T> builder;

    public QueueableActionBuilder(final GuiBuilder<T> builder, final boolean persistent, final String lastMenu) {
        super(new Queueable(builder.reference().getIdentifier(), persistent), lastMenu);
        this.builder = builder;
    }

    @Override
    public void build() {
        builder.reference().addChild(() -> (Component) getActionable());
    }

    @Override
    public GuiBuilder<T> finalise() {
        builder.handleLastQueueable();
        return builder;
    }
}
