package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

/*
Represents an icon in a menu that has a fixed position.
 */
public class StaticIcon extends BaseIcon {
    private final int index;

    public StaticIcon(final String identifier, final String display, final String lore, final String parent, final Material material, final Action action, final Predicate<BaseGui> predicate, final Consumer<ItemStack> consumer, final int index) {
        super(identifier, display, lore, parent, material, action, predicate, consumer);
        this.index = index;
    }

    @Override
    protected void fillIcon(final BaseGui gui) {
        if (this.canDisplay(gui)) {
            gui.getInventory().setItem(index, this.item);
        }
    }

    @Override
    public Class<StaticIcon> getThisClass() {
        return StaticIcon.class;
    }
}
