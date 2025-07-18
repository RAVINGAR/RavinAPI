package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;

public class IconActionBuilder<C extends Interactive, P extends Builder<? extends Component>> extends ActionBuilder<IconBuilder<C, P>> {
    private final IconBuilder<C, P> parent;

    public IconActionBuilder(final Actionable reference, final String lastMenu, final IconBuilder<C, P> parent) {
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
}
