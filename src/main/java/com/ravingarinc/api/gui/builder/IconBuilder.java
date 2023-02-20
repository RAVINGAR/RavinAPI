package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.component.icon.Dynamic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

public class IconBuilder<U extends Interactive> {
    protected final U icon;
    protected final List<BaseActionBuilder> actionBuilders;

    protected IconBuilder(final U icon) {
        this.icon = icon;
        actionBuilders = new LinkedList<>();
    }

    public <T, Z> IconBuilder<U> setMeta(final PersistentDataType<T, Z> type, final String key, final Z value) {
        icon.setMeta(type, key, value);
        return this;
    }

    public IconBuilder<U> setDynamic(final Component grandparent) {
        final String parent = icon.getIdentifier();
        icon.addChild(() -> new Dynamic(parent, grandparent));
        return this;
    }

    public ObserverActionBuilder addObserver(final Predicate<ItemStack> predicate) {
        final ObserverActionBuilder observerActionBuilder = new ObserverActionBuilder(icon, predicate, this);
        actionBuilders.add(observerActionBuilder);
        return observerActionBuilder;
    }

    public ObserverActionBuilder addObserver(final Supplier<Boolean> condition) {
        final ObserverActionBuilder observerActionBuilder = new ObserverActionBuilder(icon, condition, this);
        actionBuilders.add(observerActionBuilder);
        return observerActionBuilder;
    }

    public IconActionBuilder getActionBuilder() {
        IconActionBuilder iconActionBuilder = getExistingIconActionBuilder();
        if (iconActionBuilder == null) {
            iconActionBuilder = new IconActionBuilder(icon, icon.getParent(), this);
            actionBuilders.add(iconActionBuilder);
        }

        return iconActionBuilder;
    }

    @Nullable
    private IconActionBuilder getExistingIconActionBuilder() {
        IconActionBuilder found = null;
        for (final BaseActionBuilder builder : actionBuilders) {
            if (builder instanceof IconActionBuilder f) {
                found = f;
                break;
            }
        }
        return found;
    }

    public Supplier<Component> getIcon() {
        actionBuilders.forEach(BaseActionBuilder::build);
        actionBuilders.clear();
        return () -> (Component) icon;
    }

    protected void handleActionBuilder(final BaseActionBuilder builder) {
        if (actionBuilders.remove(builder)) {
            builder.build();
        } else {
            I.log(Level.WARNING, "Attempted to handle action builder but it was not found in IconBuilder list!");
        }
    }

    protected void addChild(final Supplier<Component> child) {
        icon.addChild(child);
    }

    public MenuBuilder finalise() {
        return null;
    }
}
