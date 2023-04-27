package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.builder.GuiProvider;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class RefreshGuiAction extends Action {
    private boolean iterate = true;

    public RefreshGuiAction() {
        super("REFRESH", 8);
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        if (iterate) {
            iterate = false;
            gui.fillElement(gui, performer);
            iterate = true;
        } else {
            GuiProvider.log(Level.SEVERE, "Attempted to perform recursive refresh gui! This was cancelled in menu of " + pointer);
        }
    }
}
