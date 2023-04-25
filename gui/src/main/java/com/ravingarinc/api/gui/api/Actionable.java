package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.entity.Player;

public interface Actionable {
    void addAction(Action action);

    void performAllActions(BaseGui gui, Player player);

    String getParent();
}
