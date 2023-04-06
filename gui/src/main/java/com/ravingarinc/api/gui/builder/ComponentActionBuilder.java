package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.api.Component;

public class ComponentActionBuilder<T extends BaseGui> extends BaseActionBuilder<GuiBuilder<T>> {
    private final GuiBuilder<T> builder;

    public ComponentActionBuilder(final GuiBuilder<T> builder, final Actionable reference, final String menu) {
        super(reference, menu);
        this.builder = builder;
    }

    @Override
    public void build() {
        builder.get().addChild(() -> (Component) getActionable());
    }

    @Override
    public GuiBuilder<T> finalise() {
        builder.handleLastActionBuilder();
        return builder;
    }
}
