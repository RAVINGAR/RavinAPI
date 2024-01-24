package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

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

    public static Action createMiscAction(final BiConsumer<BaseGui, Player> action) {
        return createMiscAction(2, action);
    }

    public static Action createMiscAction(final int priority, final BiConsumer<BaseGui, Player> action) {
        return new Action("MISC_ACTION", priority) {
            @Override
            public void performAction(BaseGui gui, Player performer) {
                action.accept(gui, performer);
            }
        };
    }
}
