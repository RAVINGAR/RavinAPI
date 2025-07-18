package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.builder.GuiProvider;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class SetPageAction extends Action {
    private final int pageNumber;

    public SetPageAction(int page) {
        super("SET_PAGE_ACTION_" + page, 0);
        this.pageNumber = page;
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        gui.getCurrentMenu().findFirstComponent(Component.PAGE).ifPresentOrElse(page -> {
            page.setPage(pageNumber);
            gui.queueRefresh();
        }, () -> GuiProvider.log(Level.WARNING, "Cannot execute set page action as could not find any pages within the menu; '" + gui.getCurrentMenu()
                .getIdentifier() + "'!"));
    }
}
