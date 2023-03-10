package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;

import java.util.logging.Level;

public class RefreshGuiAction extends Action {
    private boolean iterate = true;

    public RefreshGuiAction() {
        super("REFRESH", 8);
    }

    @Override
    public void performAction(final BaseGui gui) {
        if (iterate) {
            iterate = false;
            gui.fillElement(gui);
            iterate = true;
        } else {
            I.log(Level.SEVERE, "Attempted to perform recursive refresh gui! This was cancelled in menu of " + pointer);
        }
    }
}
