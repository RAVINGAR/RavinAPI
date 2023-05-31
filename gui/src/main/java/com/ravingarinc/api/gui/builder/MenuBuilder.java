package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.InputComponent;
import com.ravingarinc.api.gui.component.Menu;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.icon.Icon;
import com.ravingarinc.api.gui.component.icon.PlaceableIcon;
import com.ravingarinc.api.gui.component.icon.StateIcon;
import com.ravingarinc.api.gui.component.icon.StaticIcon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.*;

public class MenuBuilder implements Builder<Menu> {
    private final List<Builder<?>> builders;
    private final Menu lastMenu;

    public MenuBuilder(final GuiBuilder<?> owner, final String identifier, final String parent, final int backIcon) {
        lastMenu = new Menu(identifier, parent, owner.getPrimaryBorder(), owner.getSecondaryBorder(), backIcon);
        builders = new LinkedList<>();
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
    public MenuBuilder addMenuIcon(final String display, final String lore, final Material material, final BiPredicate<BaseGui, Player> predicate) {
        lastMenu.addMenuIcon(display, lore, material, predicate);
        return this;
    }

    public MenuBuilder addMenuIcon(final String display, final Material material, final BiPredicate<BaseGui, Player> predicate) {
        lastMenu.addMenuIcon(display, "", material, predicate);
        return this;
    }

    public MenuBuilder addMenuIcon(final String display, final String lore, final Material material) {
        lastMenu.addMenuIcon(display, lore, material, (g, p) -> true);
        return this;
    }

    public MenuBuilder addMenuIcon(final String display, final Material material) {
        lastMenu.addMenuIcon(display, "", material, (g, p) -> true);
        return this;
    }

    public IconBuilder<Icon, MenuBuilder> addIcon(final String identifier, final String display, final String lore, final Material material, final BiPredicate<BaseGui, Player> predicate) {
        return addIcon(identifier, display, lore, material, predicate, i -> {
        });
    }

    public IconBuilder<Icon, MenuBuilder> addIcon(final String identifier, final String display, final String lore, final Material material, final BiPredicate<BaseGui, Player> predicate, final Consumer<ItemStack> consumer) {
        return addIcon(identifier, display, lore, material, null, predicate, consumer);
    }

    public IconBuilder<Icon, MenuBuilder> addIcon(final String identifier, final String display, final String lore, final Material material, final Action action, final BiPredicate<BaseGui, Player> predicate, final Consumer<ItemStack> consumer) {
        final IconBuilder<Icon, MenuBuilder> newBuilder = new IconBuilder<>(this, new Icon(identifier, display, lore, lastMenu.getIdentifier(), material, action, predicate, consumer));
        builders.add(newBuilder);
        return newBuilder;
    }

    public IconBuilder<StaticIcon, MenuBuilder> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final int index) {
        return addStaticIcon(identifier, display, lore, material, (g, p) -> true, i -> {
        }, index);
    }

    public IconBuilder<StaticIcon, MenuBuilder> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final BiPredicate<BaseGui, Player> predicate, final int index) {
        return addStaticIcon(identifier, display, lore, material, predicate, i -> {
        }, index);
    }

    public IconBuilder<StaticIcon, MenuBuilder> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final Consumer<ItemStack> consumer, final int index) {
        return addStaticIcon(identifier, display, lore, material, (g, p) -> true, consumer, index);
    }

    public IconBuilder<StaticIcon, MenuBuilder> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final BiPredicate<BaseGui, Player> predicate, final Consumer<ItemStack> consumer, final int index) {
        return addStaticIcon(identifier, display, lore, material, null, predicate, consumer, index);
    }

    public IconBuilder<StaticIcon, MenuBuilder> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final Action action, final int index) {
        return addStaticIcon(identifier, display, lore, material, action, (g, p) -> true, i -> {
        }, index);
    }

    public IconBuilder<StaticIcon, MenuBuilder> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final Action action, final BiPredicate<BaseGui, Player> predicate, final Consumer<ItemStack> consumer, final int index) {
        final IconBuilder<StaticIcon, MenuBuilder> newBuilder = new IconBuilder<>(this, new StaticIcon(identifier, display, lore, lastMenu.getIdentifier(), material, action, predicate, consumer, index));
        builders.add(newBuilder);
        return newBuilder;
    }

    @Deprecated
    public <T> StateIconBuilder<T> addStateIcon(final String identifier, final Action action, final int index, final Supplier<T> determiner) {
        return addStateIcon(identifier, action, index, (g) -> determiner.get());
    }

    public <T> StateIconBuilder<T> addStateIcon(final String identifier, final Action action, final int index, final Function<BaseGui, T> determiner) {
        final StateIconBuilder<T> newBuilder = new StateIconBuilder<>(this, new StateIcon<>(identifier, lastMenu.getIdentifier(), action, (g, p) -> true, index, determiner));
        builders.add(newBuilder);
        return newBuilder;
    }

    public GuiObserverActionBuilder<Menu, MenuBuilder> addObserver(final Predicate<BaseGui> predicate) {
        final GuiObserverActionBuilder<Menu, MenuBuilder> newBuilder = new GuiObserverActionBuilder<>(lastMenu, predicate, this);
        builders.add(newBuilder);
        return newBuilder;
    }


    public IconBuilder<PlaceableIcon, MenuBuilder> addPlaceableIcon(final String identifier, final int index, final Predicate<ItemStack> validator) {
        final IconBuilder<PlaceableIcon, MenuBuilder> newBuilder = new IconBuilder<>(this, new PlaceableIcon(identifier, lastMenu.getIdentifier(), index, validator));
        builders.add(newBuilder);
        return newBuilder;
    }

    public IconBuilder<PlaceableIcon, MenuBuilder> addPlaceableIcon(final String identifier, final int index, final Predicate<ItemStack> validator, final ItemStack placeholder) {
        final IconBuilder<PlaceableIcon, MenuBuilder> newBuilder = new IconBuilder<>(this, new PlaceableIcon(identifier, lastMenu.getIdentifier(), index, validator, placeholder));
        builders.add(newBuilder);
        return newBuilder;
    }

    public PageBuilder addPage(final String identifier, final int... slots) {
        final PageBuilder newBuilder = new PageBuilder(identifier, this, slots);
        builders.add(newBuilder);
        return newBuilder;
    }

    protected void addBuilder(final Builder<?> builder) {
        this.builders.add(builder);
    }

    public MenuBuilder addDecoration(final String identifier, final Material material, final int[] slots) {
        lastMenu.addChild(() -> new Decoration(identifier, lastMenu.getIdentifier(), material, slots));
        return this;
    }

    public MenuBuilder addInputComponent(final String identifier, final String description, final Consumer<String> onResponse) {
        return addInputComponent(identifier, description, (gui, str) -> onResponse.accept(str));
    }

    public MenuBuilder addInputComponent(final String identifier, final String description, final BiConsumer<BaseGui, String> onResponse) {
        lastMenu.addChild(() -> new InputComponent(identifier, lastMenu.getIdentifier(), description, onResponse));
        return this;
    }

    public MenuBuilder addMiscComponent(final Supplier<Component> component) {
        lastMenu.addChild(component);
        return this;
    }

    @Override
    public Menu reference() {
        return lastMenu;
    }

    @Override
    public Menu get() {
        lastMenu.finalise();
        builders.forEach(builder -> {
            final Component c = builder.get();
            lastMenu.addChild(() -> c);
        });
        builders.clear();
        return lastMenu;
    }
}
