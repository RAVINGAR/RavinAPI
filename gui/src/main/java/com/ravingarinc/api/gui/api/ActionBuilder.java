package com.ravingarinc.api.gui.api;

public interface ActionBuilder<P> {

    /**
     * Finalises this builder and returns the parent
     *
     * @return The parent of type P
     */
    P finalise();

    /**
     * Builds this action builder. In most cases this means adding all queued actions to the component.
     */
    void build();
}
