package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.api.ParentBuilder;
import com.ravingarinc.api.gui.component.icon.BaseIcon;
import com.ravingarinc.api.gui.component.observer.ItemUpdater;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.*;

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

    public IconBuilder<C, P> setPredicate(final BiPredicate<BaseGui, Player> predicate) {
        if (icon instanceof BaseIcon baseIcon) {
            baseIcon.setPredicate(predicate);
        } else {
            throw new UnsupportedOperationException("Cannot set predicate for interactive icon that is not an instance of BaseIcon! Predicates cannot be used on other types of interactive elements.");
        }
        return this;
    }

    @Deprecated
    public void with(final Consumer<IconBuilder<C, P>> builder) {
        builder.accept(this);
    }

    @Deprecated
    public IconBuilder<C, P> setDynamic() {
        return this;
    }

    public IconBuilder<C, P> modifyItem(Consumer<ItemStack> consumer) {
        consumer.accept(this.icon.getItem());
        return this;
    }

    public ItemObserverActionBuilder<C, P> addObserver(final Predicate<ItemStack> predicate) {
        final ItemObserverActionBuilder<C, P> observerActionBuilder = new ItemObserverActionBuilder<>(icon, predicate, this);
        actionBuilders.add(observerActionBuilder);
        return observerActionBuilder;
    }

    /**
     * Add an observer to this interactive component. An observer will check the internal state of the item stored
     * at this component and if it is true, it will execute any configured actions.
     */
    public IconBuilder<C, P> observer(final Predicate<ItemStack> predicate, Consumer<ItemObserverActionBuilder<C, P>> builder) {
        final var observer = addObserver(predicate);
        builder.accept(observer);
        return this;
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

    public IconBuilder<C, P> addNameUpdater(final BiFunction<Interactive, Player, String> provider) {
        icon.findComponent(Component.ITEM_UPDATER, icon.getIdentifier() + "_UPDATER").ifPresentOrElse((updater) -> {
            updater.setDisplayNameProvider(provider);
        }, () -> {
            addChild((i) -> () -> {
                final ItemUpdater updater = new ItemUpdater(i);
                updater.setDisplayNameProvider(provider);
                return updater;
            });
        });
        return this;
    }

    public IconBuilder<C, P> addLoreUpdater(final BiFunction<Interactive, Player, String> provider) {
        icon.findComponent(Component.ITEM_UPDATER, icon.getIdentifier() + "_UPDATER").ifPresentOrElse((updater) -> {
            updater.setLoreProvider(provider);
        }, () -> {
            addChild((i) -> () -> {
                final ItemUpdater updater = new ItemUpdater(i);
                updater.setLoreProvider(provider);
                return updater;
            });
        });
        return this;
    }

    public IconBuilder<C, P> addMaterialUpdater(final BiFunction<Interactive, Player, Material> provider) {
        icon.findComponent(Component.ITEM_UPDATER, icon.getIdentifier() + "_UPDATER").ifPresentOrElse((updater) -> {
            updater.setMaterialProvider(provider);
        }, () -> {
            addChild((i) -> () -> {
                final ItemUpdater updater = new ItemUpdater(i);
                updater.setMaterialProvider(provider);
                return updater;
            });
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

    /**
     * Using the action builder for this icon, add any actions which will be executed when this icon is clicked.
     */
    public IconBuilder<C, P> actions(Consumer<IconActionBuilder<C, P>> builder) {
        final var actions = getActionBuilder();
        builder.accept(actions);
        return this;
    }

    /**
     * Using the action builder for this icon, add any actions which will be executed when this icon is shift-clicked.
     */
    public IconBuilder<C, P> actionsOnShiftClick(Consumer<IconShiftActionBuilder<C, P>> builder) {
        final var actions = getShiftClickActionBuilder();
        builder.accept(actions);
        return this;
    }

    public IconShiftActionBuilder<C, P> getShiftClickActionBuilder() {
        IconShiftActionBuilder<C, P> iconActionBuilder = getExistingIconShiftActionBuilder();
        if (iconActionBuilder == null) {
            iconActionBuilder = new IconShiftActionBuilder<>(icon, icon.getParent(), this);
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

    @Nullable
    private IconShiftActionBuilder<C, P> getExistingIconShiftActionBuilder() {
        IconShiftActionBuilder<C, P> found = null;
        for (final ActionBuilder<IconBuilder<C, P>> builder : actionBuilders) {
            if (builder instanceof IconShiftActionBuilder<C, P> f) {
                found = f;
                break;
            }
        }
        return found;
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
        actionBuilders.forEach(ActionBuilder::build);
        actionBuilders.clear();
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
