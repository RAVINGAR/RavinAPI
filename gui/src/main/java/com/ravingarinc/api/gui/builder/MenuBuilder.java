package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.Menu;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.icon.Icon;
import com.ravingarinc.api.gui.component.icon.PlaceableIcon;
import com.ravingarinc.api.gui.component.icon.StateIcon;
import com.ravingarinc.api.gui.component.icon.StaticIcon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MenuBuilder {
    private final List<InternalIconBuilder<?>> iconBuilders;
    private final Menu lastMenu;
    private PageBuilder lastPageBuilder = null;

    public MenuBuilder(final GuiBuilder<?> owner, final String identifier, final String parent, final int backIcon) {
        lastMenu = new Menu(identifier, parent, owner.getPrimaryBorder(), owner.getSecondaryBorder(), backIcon);
        iconBuilders = new LinkedList<>();
    }

    public MenuBuilder setBackground(final Material material) {
        lastMenu.setBackground(material);
        return this;
    }

    /**
     * Add's an icon to the menu which when clicked will open another menu corresponding to it's display name
     * Display names should be in the format of [Example] or Example, as this will point to a menu with the identifier EXAMPLE
     *
     * @return MenuBuilder to further modify the Menu
     */
    public MenuBuilder addMenuIcon(final String display, final String lore, final Material material, final Predicate<BaseGui> predicate) {
        lastMenu.addMenuIcon(display, lore, material, predicate);
        return this;
    }

    public MenuBuilder addMenuIcon(final String display, final Material material, final Predicate<BaseGui> predicate) {
        lastMenu.addMenuIcon(display, "", material, predicate);
        return this;
    }

    public MenuBuilder addMenuIcon(final String display, final String lore, final Material material) {
        lastMenu.addMenuIcon(display, lore, material, t -> true);
        return this;
    }

    public MenuBuilder addMenuIcon(final String display, final Material material) {
        lastMenu.addMenuIcon(display, "", material, t -> true);
        return this;
    }

    public InternalIconBuilder<Icon> addIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate) {
        return addIcon(identifier, display, lore, material, predicate, i -> {
        });
    }

    public InternalIconBuilder<Icon> addIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate, final Consumer<ItemStack> consumer) {
        final InternalIconBuilder<Icon> newBuilder = new InternalIconBuilder<>(this, new Icon(identifier, display, lore, lastMenu.getIdentifier(), material, null, predicate, consumer));
        iconBuilders.add(newBuilder);
        return newBuilder;
    }

    public InternalIconBuilder<StaticIcon> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final int index) {
        return addStaticIcon(identifier, display, lore, material, p -> true, i -> {
        }, index);
    }

    public InternalIconBuilder<StaticIcon> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate, final int index) {
        return addStaticIcon(identifier, display, lore, material, predicate, i -> {
        }, index);
    }

    public InternalIconBuilder<StaticIcon> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final Consumer<ItemStack> consumer, final int index) {
        return addStaticIcon(identifier, display, lore, material, p -> true, consumer, index);
    }

    public InternalIconBuilder<StaticIcon> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate, final Consumer<ItemStack> consumer, final int index) {
        final InternalIconBuilder<StaticIcon> newBuilder = new InternalIconBuilder<>(this, new StaticIcon(identifier, display, lore, lastMenu.getIdentifier(), material, null, predicate, consumer, index));
        iconBuilders.add(newBuilder);
        return newBuilder;
    }

    public <T> InternalStateIconBuilder<T> addStateIcon(final String identifier, final Action action, final int index, final Supplier<T> determiner) {
        final InternalStateIconBuilder<T> newBuilder = new InternalStateIconBuilder<>(this, new StateIcon<>(identifier, lastMenu.getIdentifier(), action, t -> true, index, determiner));
        iconBuilders.add(newBuilder);
        return newBuilder;
    }


    public InternalIconBuilder<PlaceableIcon> addPlaceableIcon(final String identifier, final int index, final Predicate<ItemStack> validator) {
        final InternalIconBuilder<PlaceableIcon> newBuilder = new InternalIconBuilder<>(this, new PlaceableIcon(identifier, lastMenu.getIdentifier(), index, validator));
        iconBuilders.add(newBuilder);
        return newBuilder;
    }

    public InternalIconBuilder<PlaceableIcon> addPlaceableIcon(final String identifier, final int index, final Predicate<ItemStack> validator, final ItemStack placeholder) {
        final InternalIconBuilder<PlaceableIcon> newBuilder = new InternalIconBuilder<>(this, new PlaceableIcon(identifier, lastMenu.getIdentifier(), index, validator, placeholder));
        iconBuilders.add(newBuilder);
        return newBuilder;
    }

    public PageBuilder addPage(final String identifier, final int... slots) {
        handleLastPageBuilder();
        lastPageBuilder = new PageBuilder(identifier, lastMenu.getIdentifier(), slots);
        return lastPageBuilder;
    }

    public MenuBuilder addDecoration(final String identifier, final Material material, final int[] slots) {
        lastMenu.addChild(() -> new Decoration(identifier, lastMenu.getIdentifier(), material, slots));
        return this;
    }

    public void handleLastPageBuilder() {
        if (lastPageBuilder != null) {
            lastMenu.addChild(() -> lastPageBuilder.getPage());
            lastPageBuilder = null;
        }
    }

    protected Menu getMenu() {
        lastMenu.finalise();
        handleLastPageBuilder();
        iconBuilders.forEach(builder -> lastMenu.addChild(builder.getIcon()));
        iconBuilders.clear();
        return lastMenu;
    }

    public static class InternalIconBuilder<I extends Interactive> extends IconBuilder<I, MenuBuilder> {

        protected InternalIconBuilder(final MenuBuilder owner, final I icon) {
            super(owner, icon);
        }

        public IconBuilder<I, MenuBuilder> setDynamic() {
            return setDynamic(owner.lastMenu);
        }
    }

    public static class InternalStateIconBuilder<T> extends InternalIconBuilder<StateIcon<T>> {
        private final List<StateActionBuilder> builders;

        protected InternalStateIconBuilder(final MenuBuilder owner, final StateIcon<T> icon) {
            super(owner, icon);
            builders = new ArrayList<>();
        }

        public StateActionBuilder addState(final T state) {
            icon.addState(state);
            return getStateActionBuilder(state);
        }

        public StateActionBuilder getStateActionBuilder(final T type) {
            final Optional<StateIcon.State<T>> opt = icon.getState(type);
            if (opt.isPresent()) {
                final StateActionBuilder builder = new StateActionBuilder(opt.get(), icon.getParent());
                builders.add(builder);
                return builder;
            }
            throw new IllegalArgumentException("Unknown type called '" + type.toString() + "' for state icon " + icon.getIdentifier());
        }

        @Override
        public Supplier<Component> getIcon() {
            builders.forEach(StateActionBuilder::build);
            builders.clear();
            return super.getIcon();
        }

        public StateIcon<T> get() {
            return icon;
        }
    }
}
