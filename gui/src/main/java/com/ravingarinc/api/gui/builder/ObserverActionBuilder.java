package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.component.icon.Observer;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ObserverActionBuilder<C extends Interactive, P extends Builder<? extends Component>> extends BaseActionBuilder<IconBuilder<C, P>> {
    private final IconBuilder<C, P> parent;

    public ObserverActionBuilder(final Interactive icon, final Predicate<ItemStack> predicate, final IconBuilder<C, P> parent) {
        super(new Observer(icon, predicate), icon.getParent());
        this.parent = parent;
    }

    public ObserverActionBuilder(final Interactive icon, final Supplier<Boolean> condition, final IconBuilder<C, P> parent) {
        super(new Observer(icon, condition), icon.getParent());
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
