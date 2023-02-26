package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.component.icon.StateIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StateIconBuilder<T> extends IconBuilder<StateIcon<T>, MenuBuilder> {
    private final List<StateActionBuilder> builders;

    protected StateIconBuilder(final MenuBuilder owner, final StateIcon<T> icon) {
        super(owner, icon);
        builders = new ArrayList<>();
    }

    public StateActionBuilder addState(final T state) {
        icon.addState(state);
        return getStateActionBuilder(state);
    }

    public StateActionBuilder getStateActionBuilder(final T type) {
        final Optional<StateIcon.State<T>> opt = icon.getState(type);
        if (opt.isPresent()) {
            final StateActionBuilder builder = new StateActionBuilder(opt.get(), icon.getParent(), this);
            builders.add(builder);
            return builder;
        }
        throw new IllegalArgumentException("Unknown type called '" + type.toString() + "' for state icon " + icon.getIdentifier());
    }

    @Override
    public StateIcon<T> get() {
        builders.forEach(StateActionBuilder::build);
        builders.clear();
        return super.get();
    }

    public class StateActionBuilder extends BaseActionBuilder<StateIconBuilder<T>> {

        private final StateIconBuilder<T> parent;

        public StateActionBuilder(final Actionable reference, final String lastMenu, final StateIconBuilder<T> parent) {
            super(reference, lastMenu);
            this.parent = parent;
        }

        @Override
        public StateIconBuilder<T> finalise() {
            return parent;
        }

        @Override
        public void build() {
            getActionable();
        }
    }
}
