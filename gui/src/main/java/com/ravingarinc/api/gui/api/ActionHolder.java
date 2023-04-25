package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.entity.Player;

/**
 * Temporary class to handle transition for things.
 */
@Deprecated
public class ActionHolder {
    private final BaseGui gui;
    private final Player player;

    public ActionHolder(BaseGui gui, Player player) {
        this.gui = gui;
        this.player = player;
    }

    /**
     * Get the player who just clicked in this instance
     *
     * @return The player
     */
    public Player getPlayer() {
        return this.player;
    }

    public BaseGui getGui() {
        return gui;
    }
}
