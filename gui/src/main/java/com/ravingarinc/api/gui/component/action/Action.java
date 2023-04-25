package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.entity.Player;

public abstract class Action implements Comparable<Action> {
    protected final String pointer;
    private final Integer priority;

    public Action(final String pointer, final int priority) {
        this.pointer = pointer;
        this.priority = priority;
    }

    public Action(final String pointer) {
        this.pointer = pointer;
        this.priority = 2;
    }

    protected Integer getPriority() {
        return priority;
    }

    public String getPointer() {
        return pointer;
    }

    public abstract void performAction(BaseGui gui, Player performer);

    @Override
    public int compareTo(final Action other) {
        return priority.compareTo(other.getPriority());
    }
}
