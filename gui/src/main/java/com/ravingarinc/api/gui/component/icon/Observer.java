package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * A submodule of a component which will update its parent if a predicate is true
 */
public abstract class Observer implements Component, Actionable {
    protected final List<Action> actions;
    protected final Component parent;
    protected final String identifier;

    /**
     * An observer's actions will be called after it's parent as an item returns true for the predicate. It's actions in
     * most cases will point towards its parent
     */
    public Observer(@NotNull final Component parent) {
        this.identifier = parent.getIdentifier() + "_OBSERVER";
        this.parent = parent;
        actions = new LinkedList<>();
    }

    @Override
    public void addAction(final Action action) {
        if (action != null) {
            actions.add(action);
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getParent() {
        return parent.getIdentifier();
    }

    @Override
    public Class<Observer> getThisClass() {
        return Observer.class;
    }

    @Override
    public void performAllActions(final BaseGui gui, Player player) {
        actions.forEach(action -> {
            action.performAction(gui, player);
        });
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 2;
    }
}
