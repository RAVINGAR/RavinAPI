package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.builder.GuiProvider;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.logging.Level;

public class SetAmountAction extends Action {
    private final int amount;

    public SetAmountAction(final String pointer, final int amount) {
        super(pointer);
        this.amount = amount;
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        final Optional<Interactive> component = gui.getCurrentMenu().findComponent(Component.INTERACTIVE, pointer);
        component.ifPresentOrElse(c -> c.setAmount(amount), () -> GuiProvider.log(Level.WARNING, "Could not find interactive component named " + this.getPointer()));
    }
}
