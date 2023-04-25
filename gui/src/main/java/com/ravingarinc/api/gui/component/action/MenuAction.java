package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.entity.Player;

public class MenuAction extends Action {
    public MenuAction(final String pointer) {
        super(pointer);
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        if (pointer.equalsIgnoreCase(gui.getIdentifier())) {
            performer.closeInventory();
        } else {
            gui.updateCurrentMenu(pointer, performer);
        }
    }
}
