package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class NextPageAction extends Action {
    private final String menu;

    public NextPageAction(final String pointer, final String menu) {
        super(pointer, 0);
        this.menu = menu;
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        gui.findComponent(Component.MENU, menu).map((menu) -> menu.findComponent(Component.PAGE, pointer)).ifPresentOrElse((optional) -> {
            optional.ifPresentOrElse(page -> {
                page.nextPage();
                gui.fillElement(gui, performer);
            }, () -> I.log(Level.WARNING, "Could not find page called " + pointer + "!"));
        }, () -> I.log(Level.WARNING, "Could not find menu called " + menu + "!"));
    }
}
