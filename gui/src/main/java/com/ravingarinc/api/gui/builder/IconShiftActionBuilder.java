package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;

import java.util.Collections;

public class IconShiftActionBuilder<C extends Interactive, P extends Builder<? extends Component>> extends ActionBuilder<IconBuilder<C, P>> {
    private final IconBuilder<C, P> parent;

    public IconShiftActionBuilder(final Interactive reference, final String lastMenu, final IconBuilder<C, P> parent) {
        super(reference, lastMenu);
        this.parent = parent;
    }

    /**
     * Finalises the current action builder and returns the previous builder
     */
    @Override
    public IconBuilder<C, P> finalise() {
        build();
        return parent;
    }

    @Override
    public void build() {
        Collections.sort(actionsToAdd);
        final var castedRef = (Interactive) reference;
        actionsToAdd.forEach(castedRef::addShiftAction);
        actionsToAdd.clear();
    }
}
