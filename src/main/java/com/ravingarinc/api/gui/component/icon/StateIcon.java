package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StateIcon<T> extends BaseIcon {
    private final List<State<T>> states;
    private final Supplier<T> determiner; //Provides the value such that this state icon's current state will always equal to the value presented
    private final int index;
    private State<T> currentState;

    public StateIcon(final String identifier, final String parent, final Action action, final Predicate<BaseGui> predicate, final int index, final Supplier<T> initial) {
        super(identifier, identifier, "", parent, Material.BARRIER, action, predicate, i -> {
        });
        this.index = index;
        states = new ArrayList<>();
        currentState = null;
        this.determiner = initial;
    }

    public void addState(final T state) {
        states.add(new State<>(states.size(), state));
    }

    public void nextState(final BaseGui gui) {
        int nextState = currentState.getIndex() + 1;
        if (nextState > states.size() - 1) {
            nextState = 0;
        }
        switchState(nextState, gui);
    }

    @NotNull
    public T getCurrentState() {
        return currentState.getType();
    }

    public Optional<State<T>> getState(final int index) {
        for (final State<T> next : states) {
            if (next.getIndex() == index) {
                return Optional.of(next);
            }
        }
        return Optional.empty();
    }

    public Optional<State<T>> getState(final T type) {
        for (final State<T> next : states) {
            if (next.getType().equals(type)) {
                return Optional.of(next);
            }
        }
        return Optional.empty();
    }

    public void switchState(final int index, final BaseGui gui) {
        getState(index).ifPresent(s -> switchState(s, gui));
    }

    public void switchState(final State<T> state, final BaseGui gui) {
        currentState = state;
        currentState.performAllActions(gui);
    }

    @Override
    protected void fillIcon(final BaseGui gui) {
        final T type = determiner.get();
        final State<T> nextState = type == null ? states.get(0) : getState(type).orElseGet(() -> states.get(0));
        if (currentState == null || !currentState.equals(nextState)) {
            switchState(nextState, gui);
        }
        if (this.canDisplay(gui)) {
            gui.getInventory().setItem(index, this.item);
        }
    }

    @Override
    public Class<? extends Component> getThisClass() {
        return StateIcon.class;
    }

    public static class State<S> implements Actionable {
        private final int index;
        private final S type;

        private final List<Action> actions;

        public State(final int index, final S type) {
            this.index = index;
            this.type = type;
            this.actions = new ArrayList<>();
        }

        @Override
        public void addAction(final Action action) {
            this.actions.add(action);
        }

        @Override
        public void performAllActions(final BaseGui gui) {
            this.actions.forEach(a -> a.performAction(gui));
        }

        @Override
        public String getParent() {
            return null;
        }

        public S getType() {
            return type;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final State<?> state = (State<?>) o;
            return index == state.index && type.equals(state.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, type);
        }
    }
}
