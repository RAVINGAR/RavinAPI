package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Icon extends BaseIcon {
    public Icon(final String identifier, final String display, final String lore, final String parent, final Material material, final Action action, final Predicate<BaseGui> predicate, final Consumer<ItemStack> consumer) {
        super(identifier, display, lore, parent, material, predicate, consumer);
        addAction(action);
    }

    @Override
    public Class<Icon> getThisClass() {
        return Icon.class;
    }
}
