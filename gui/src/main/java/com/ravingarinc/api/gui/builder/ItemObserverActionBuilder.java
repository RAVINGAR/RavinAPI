package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.component.observer.ItemObserver;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class ItemObserverActionBuilder<C extends Interactive, P extends Builder<? extends Component>> extends BaseActionBuilder<IconBuilder<C, P>> {
    private final IconBuilder<C, P> parent;

    public ItemObserverActionBuilder(final Interactive icon, final Predicate<ItemStack> predicate, final IconBuilder<C, P> parent) {
        super(new ItemObserver(icon, predicate), icon.getParent());
        this.parent = parent;
    }

    @Override
    public IconBuilder<C, P> finalise() {
        parent.handleActionBuilder(this);
        return parent;
    }

    @Override
    public void build() {
        parent.addChild(() -> (Component) getActionable());
    }
}
