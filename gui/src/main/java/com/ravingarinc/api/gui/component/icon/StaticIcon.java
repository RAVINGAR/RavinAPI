package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

/*
Represents an icon in a menu that has a fixed position.
 */
public class StaticIcon extends BaseIcon {
    private final int index;

    public StaticIcon(final String identifier, final String display, final String lore, final String parent, final Material material, final Action action, final BiPredicate<BaseGui, Player> predicate, final Consumer<ItemStack> consumer, final int index) {
        super(identifier, display, lore, parent, material, predicate, consumer);
        addAction(action);
        this.index = index;
    }

    @Override
    protected void fillIcon(final BaseGui gui, Player player) {
        if (this.canDisplay(gui, player)) {
            gui.getInventory().setItem(index, this.item);
        }
    }

    @Override
    public Class<StaticIcon> getThisClass() {
        return StaticIcon.class;
    }
}
