package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.Page;

import java.util.logging.Level;

public class PreviousPageAction extends Action {
    private final String menu;

    public PreviousPageAction(final String pointer, final String menu) {
        super(pointer, 0);
        this.menu = menu;
    }

    @Override
    public void performAction(final BaseGui gui) {
        gui.findComponent(Component.MENU, menu).map((menu) -> menu.findComponent(Page.PAGE, pointer)).ifPresentOrElse((optional) -> {
            optional.ifPresentOrElse(page -> {
                page.previousPage();
                gui.fillElement(gui);
            }, () -> I.log(Level.WARNING, "Could not find page called " + pointer + "!"));
        }, () -> I.log(Level.WARNING, "Could not find menu called " + menu + "!"));
    }
}
