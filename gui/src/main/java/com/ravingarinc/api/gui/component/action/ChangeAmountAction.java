package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;

import java.util.Optional;
import java.util.logging.Level;

public class ChangeAmountAction extends Action {
    private final int amount;

    public ChangeAmountAction(final String pointer, final int amount) {
        super(pointer);
        this.amount = amount;
    }

    @Override
    public void performAction(final BaseGui gui) {
        final Optional<Interactive> component = gui.getCurrentMenu().findComponent(Component.INTERACTIVE, pointer);
        component.ifPresentOrElse(c -> c.addAmount(amount), () -> I.log(Level.WARNING, "Could not find interactive component named " + this.getPointer()));
    }
}
