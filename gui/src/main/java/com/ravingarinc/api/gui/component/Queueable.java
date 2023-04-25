package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * When a BaseGui is closed
 */
public class Queueable implements Component, Actionable {
    private final List<Action> actions;
    private final String parent;

    private final boolean persistent;

    public Queueable(final String parent, final boolean persistent) {
        this.actions = new ArrayList<>();
        this.parent = parent;
        this.persistent = persistent;
    }

    @Override
    public String getIdentifier() {
        return parent + "_QUEUEABLE";
    }

    @Override
    public void addAction(final Action action) {
        this.actions.add(action);
    }

    @Override
    public void performAllActions(final BaseGui gui, Player player) {
        this.actions.forEach(a -> a.performAction(gui, player));
        if (!persistent) {
            this.actions.clear();
        }
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {
        performAllActions(gui, player);
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 16;
    }

    @Override
    public Class<? extends Component> getThisClass() {
        return Queueable.class;
    }
}
