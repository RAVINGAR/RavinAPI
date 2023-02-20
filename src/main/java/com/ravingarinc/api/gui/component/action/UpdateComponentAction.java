package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.icon.Dynamic;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;

//Should update a component with a name, lore, or material
public class UpdateComponentAction extends Action {
    private final String menu;
    private final Supplier<String> name;
    private final Supplier<String> lore;
    private final Material material;

    public UpdateComponentAction(final String pointer, final String menu, @NotNull final Supplier<String> name, @NotNull final Supplier<String> lore, final Material material) {
        super(pointer);
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.menu = menu;
    }

    @Override
    public void performAction(final BaseGui gui) {
        final Optional<Dynamic> component = gui.findComponent(Component.MENU, menu)
                .flatMap(m -> m.findComponent(Component.INTERACTIVE, pointer)
                        .flatMap(e -> e.findComponent(Component.DYNAMIC, pointer + "_DYNAMIC")));

        if (component.isPresent()) {
            final Dynamic dynamic = component.get();
            String name = null, lore = null;
            try {
                name = this.name.get();
                lore = this.lore.get();
            } catch (final Exception e) {
                I.log(Level.SEVERE, "Encountered exception updating dynamic icon! ", e);
            }
            dynamic.updateItem(name, lore, material);
        } else {
            I.log(Level.WARNING, "Could not find dynamic child of component " + this.getPointer());
        }
    }
}
