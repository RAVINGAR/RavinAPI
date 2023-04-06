package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

//Should update a component with a name, lore, or material
public class UpdateComponentAction extends Action {
    private final String menu;
    private final Function<BaseGui, String> name;
    private final Function<BaseGui, String> lore;
    private final Material material;

    public UpdateComponentAction(final String pointer, final String menu, @NotNull final Function<BaseGui, String> name, @NotNull final Function<BaseGui, String> lore, final Material material) {
        super(pointer);
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.menu = menu;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public void performAction(final BaseGui gui) {
        final Optional<Interactive> component = gui.findComponent(Component.MENU, menu)
                .flatMap(m -> m.findComponent(Component.INTERACTIVE, pointer));

        if (component.isPresent()) {
            final Interactive interactive = component.get();
            String name = null, lore = null;
            try {
                name = this.name.apply(gui);
                lore = this.lore.apply(gui);
            } catch (final Exception e) {
                I.log(Level.SEVERE, "Encountered exception updating dynamic icon! ", e);
            }
            interactive.updateItem(name, lore, material);
        } else {
            I.log(Level.WARNING, "Could not find interactive component " + this.getPointer());
        }
    }
}
