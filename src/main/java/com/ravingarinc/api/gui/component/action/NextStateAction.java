package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;

public class NextStateAction extends Action {

    public NextStateAction(final String pointer) {
        super(pointer);
    }

    @Override
    public void performAction(final BaseGui gui) {
        gui.findComponent(Component.STATE_ICON, pointer).ifPresent(i -> i.nextState(gui));
    }
}
