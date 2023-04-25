package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class GuiObserver extends Observer {
    private final Predicate<BaseGui> predicate;

    /**
     * An observer's actions will be called after it's parent as an item returns true for the predicate. It's actions in
     * most cases will point towards its parent
     */
    public GuiObserver(@NotNull final Component parent, final Predicate<BaseGui> predicate) {
        super(parent);
        this.predicate = predicate;
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {
        if (predicate.test(gui)) {
            performAllActions(gui, player);
        }
    }
}
