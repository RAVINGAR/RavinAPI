package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public class PageIcon extends BaseIcon {
    public PageIcon(final String identifier, final String display, final String lore, final String parent, final Material material, final Predicate<BaseGui> predicate, final Consumer<ItemStack> consumer) {
        super(identifier, display, lore, parent, material, predicate, consumer);
    }

    @Override
    protected void fillIcon(final BaseGui gui) {
        gui.findComponent(Component.PAGE, this.parent).ifPresentOrElse((page) -> {
            if (canDisplay(gui)) {
                page.queueIconToPlace(this.item);
            }
        }, () -> I.log(Level.SEVERE, "Parent of PageIcon was not of type Page!"));
    }

    @Override
    public Class<PageIcon> getThisClass() {
        return PageIcon.class;
    }
}
