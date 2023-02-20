package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;

import java.util.function.Supplier;

public class ChangeStateAction extends Action {
    private final Supplier<Integer> state;

    public ChangeStateAction(final String pointer, final Supplier<Integer> state) {
        super(pointer);
        this.state = state;
    }

    @Override
    public void performAction(final BaseGui gui) {
        gui.findComponent(Component.STATE_ICON, pointer).ifPresent(icon -> icon.switchState(state.get(), gui));
    }
}
