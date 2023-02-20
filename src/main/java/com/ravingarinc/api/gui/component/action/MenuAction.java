package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;

public class MenuAction extends Action {
    public MenuAction(final String pointer) {
        super(pointer);
    }

    @Override
    public void performAction(final BaseGui gui) {
        if (pointer.equalsIgnoreCase(gui.getIdentifier())) {
            //If attempting to go back to the BaseGui, this means you should exit!
            gui.getPlayer().closeInventory();
            gui.closeGui();
        } else {
            gui.updateCurrentMenu(pointer);
        }
    }
}
