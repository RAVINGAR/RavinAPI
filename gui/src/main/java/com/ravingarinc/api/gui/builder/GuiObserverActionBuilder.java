package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.Menu;
import com.ravingarinc.api.gui.component.observer.GuiObserver;

import java.util.function.Predicate;

public class GuiObserverActionBuilder<C extends Component, P extends Builder<C>> extends ActionBuilder<P> implements Builder<GuiObserver> {
    private final P parent;

    public GuiObserverActionBuilder(final C component, final Predicate<BaseGui> predicate, final P builder) {
        super(new GuiObserver(component, predicate), component instanceof Menu ? component.getIdentifier() : component.getParent());
        this.parent = builder;
    }

    @Override
    public P finalise() {
        build();
        return parent;
    }

    @Override
    public GuiObserver reference() {
        return (GuiObserver) reference;
    }

    @Override
    public GuiObserver get() {
        build();
        return reference();
    }
}
