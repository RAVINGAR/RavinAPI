package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.*;
import com.ravingarinc.api.gui.component.observer.ItemUpdater;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.*;
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

    @Deprecated
    public IconBuilder<C, P> setDynamic() {
        return this;
    }

    public ItemObserverActionBuilder<C, P> addObserver(final Predicate<ItemStack> predicate) {
        final ItemObserverActionBuilder<C, P> observerActionBuilder = new ItemObserverActionBuilder<>(icon, predicate, this);
        actionBuilders.add(observerActionBuilder);
        return observerActionBuilder;
    }

    public IconBuilder<C, P> addItemUpdater(final BiFunction<Interactive, Player, String> displayNameProvider, final BiFunction<Interactive, Player, String> loreProvider, final BiFunction<Interactive, Player, Material> materialProvider) {
        return addItemUpdater(displayNameProvider, loreProvider, materialProvider, null);
    }

    public IconBuilder<C, P> addItemUpdater(final BiFunction<Interactive, Player, String> displayNameProvider, final BiFunction<Interactive, Player, String> loreProvider, final BiFunction<Interactive, Player, Material> materialProvider, final BiFunction<Interactive, Player, Consumer<ItemMeta>> metaProvider) {
        addChild(i -> () -> {
            final ItemUpdater updater = new ItemUpdater(i);
            updater.setDisplayNameProvider(displayNameProvider);
            updater.setLoreProvider(loreProvider);
            updater.setMaterialProvider(materialProvider);
            updater.setMetaProvider(metaProvider);
            return updater;
        });
        return this;
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
            GuiProvider.log(Level.WARNING, "Attempted to handle action builder but it was not found in IconBuilder list!");
        }
    }

    public IconBuilder<C, P> addChild(final Function<C, Supplier<Component>> child) {
        icon.addChild(child.apply(icon));
        return this;
    }

    public IconBuilder<C, P> addChild(final Supplier<Component> child) {
        icon.addChild(child);
        return this;
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
