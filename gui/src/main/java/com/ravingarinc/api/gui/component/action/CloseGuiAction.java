package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.entity.Player;

public class CloseGuiAction extends Action {
    public CloseGuiAction() {
        super("CLOSE_ACTION");
    }

    @Override
    public void performAction(final BaseGui gui, Player player) {
        if (player.getOpenInventory().getTitle().equalsIgnoreCase(gui.getIdentifier())) {
            player.closeInventory();
        }
    }
}
