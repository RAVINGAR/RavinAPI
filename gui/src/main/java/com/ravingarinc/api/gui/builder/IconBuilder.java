package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.api.ActionBuilder;
import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.api.ParentBuilder;
import com.ravingarinc.api.gui.component.icon.Dynamic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * @param <C> The Interactive type this IconBuilder represents
 * @param <P> The parent builder type.
 */
public class IconBuilder<C extends Interactive, P extends Builder<? extends Component>> implements ParentBuilder, Builder<C> {
    protected final P owner;
    protected final C icon;
    protected final List<ActionBuilder<IconBuilder<C, P>>> actionBuilders;

    protected IconBuilder(final P owner, final C icon) {
        this.owner = owner;
        this.icon = icon;
        actionBuilders = new LinkedList<>();
    }

    public <T, Z> IconBuilder<C, P> setMeta(final PersistentDataType<T, Z> type, final String key, final Z value) {
        icon.setMeta(type, key, value);
        return this;
    }

    public IconBuilder<C, P> setDynamic() {
        final String parent = icon.getIdentifier();
        icon.addChild(() -> new Dynamic(parent, owner.reference()));
        return this;
    }

    public ObserverActionBuilder<C, P> addObserver(final Predicate<ItemStack> predicate) {
        final ObserverActionBuilder<C, P> observerActionBuilder = new ObserverActionBuilder<>(icon, predicate, this);
        actionBuilders.add(observerActionBuilder);
        return observerActionBuilder;
    }

    public ObserverActionBuilder<C, P> addObserver(final Supplier<Boolean> condition) {
        final ObserverActionBuilder<C, P> observerActionBuilder = new ObserverActionBuilder<>(icon, condition, this);
        actionBuilders.add(observerActionBuilder);
        return observerActionBuilder;
    }

    public IconActionBuilder<C, P> getActionBuilder() {
        IconActionBuilder<C, P> iconActionBuilder = getExistingIconActionBuilder();
        if (iconActionBuilder == null) {
            iconActionBuilder = new IconActionBuilder<>(icon, icon.getParent(), this);
            actionBuilders.add(iconActionBuilder);
        }

        return iconActionBuilder;
    }

    @Nullable
    private IconActionBuilder<C, P> getExistingIconActionBuilder() {
        IconActionBuilder<C, P> found = null;
        for (final ActionBuilder<IconBuilder<C, P>> builder : actionBuilders) {
            if (builder instanceof IconActionBuilder<C, P> f) {
                found = f;
                break;
            }
        }
        return found;
    }

    @Override
    public void handleActionBuilder(final ActionBuilder<? extends ParentBuilder> builder) {
        if (actionBuilders.remove(builder)) {
            builder.build();
        } else {
            I.log(Level.WARNING, "Attempted to handle action builder but it was not found in IconBuilder list!");
        }
    }

    protected void addChild(final Supplier<Component> child) {
        icon.addChild(child);
    }

    public P finalise() {
        return owner;
    }

    @Override
    public C reference() {
        return icon;
    }

    @Override
    public C get() {
        actionBuilders.forEach(ActionBuilder::build);
        actionBuilders.clear();
        return icon;
    }
}
