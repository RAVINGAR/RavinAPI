package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;

import java.util.logging.Level;

public class InputAction extends Action {
    private final String menu;

    public InputAction(final String pointer, final String menu) {
        super(pointer);
        this.menu = menu;
    }

    @Override
    public void performAction(final BaseGui gui) {
        gui.findComponent(Component.MENU, menu).map((menu) -> menu.findComponent(Component.INPUT_COMPONENT, pointer)).ifPresentOrElse((opt) -> {
            opt.ifPresentOrElse((input) -> input.getResponse(gui), () -> I.log(Level.WARNING, "Could not find input component called " + pointer));
        }, () -> I.log(Level.WARNING, "Could not find menu called " + menu));
    }
}
