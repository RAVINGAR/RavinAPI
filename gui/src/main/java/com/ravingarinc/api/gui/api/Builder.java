package com.ravingarinc.api.gui.api;

public interface Builder<T extends Component> {

    /**
     * Gets the component this builder points to without building anything.
     *
     * @return The exact component
     */
    T reference();

    /**
     * Compiles all changes for this builder and returns the literal component.
     *
     * @return The component
     */
    T get();
}
