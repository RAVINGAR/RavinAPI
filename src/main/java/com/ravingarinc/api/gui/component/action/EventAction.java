package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Actionable;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic Action that contains API to call a specified event
 */
public abstract class EventAction extends Action implements Actionable {
    private final List<Action> preActions;
    private final List<Action> postActions;

    public EventAction(final String parent) {
        super(parent);
        preActions = new ArrayList<>();
        postActions = new ArrayList<>();
    }


    @Override
    public String getParent() {
        return pointer;
    }

    /**
     * Calls an event and returns true if it was executed successfully
     */
    public boolean callEvent(final Event event) {
        Bukkit.getPluginManager().callEvent(event);
        if (event instanceof Cancellable) {
            return !((Cancellable) event).isCancelled();
        }
        return true;
    }


    @Override
    public void addAction(final Action action) {
        if (action != null) {
            preActions.add(action);
        }
    }

    public void addPostAction(final Action action) {
        if (action != null) {
            postActions.add(action);
        }
    }

    @Override
    public void performAllActions(final BaseGui gui) {
        preActions.forEach(action -> {
            action.performAction(gui);
        });
    }

    public void performAllPostActions(final BaseGui gui) {
        postActions.forEach(action -> {
            action.performAction(gui);
        });
    }
}
