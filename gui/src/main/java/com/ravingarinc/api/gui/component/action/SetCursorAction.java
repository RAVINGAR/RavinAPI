package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class SetCursorAction extends Action {
    private final Supplier<ItemStack> cursorSupplier;

    public SetCursorAction(final Supplier<ItemStack> cursorSupplier) {
        super("cursor_action", 10);
        this.cursorSupplier = cursorSupplier;
    }

    @Override
    public void performAction(BaseGui gui, Player performer) {
        performer.setItemOnCursor(cursorSupplier.get());
    }
}
