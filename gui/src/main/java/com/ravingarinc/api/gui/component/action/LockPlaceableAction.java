package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.Menu;
import org.bukkit.entity.Player;

import java.util.Optional;

public class LockPlaceableAction extends Action {
    private final String menu;
    private final boolean locked;

    public LockPlaceableAction(final String pointer, final String menu, final boolean locked) {
        super(pointer);
        this.menu = menu;
        this.locked = locked;
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        final Optional<Menu> componentMenu = gui.findComponent(Component.MENU, menu);
        componentMenu.flatMap(m -> m.findComponent(Component.PLACEABLE_ICON, pointer)).ifPresent(p -> p.setLocked(locked));
    }
}
