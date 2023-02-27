package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.InputComponent;
import com.ravingarinc.api.gui.component.Menu;
import com.ravingarinc.api.gui.component.Page;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.icon.Icon;
import com.ravingarinc.api.gui.component.icon.PlaceableIcon;
import com.ravingarinc.api.gui.component.icon.StateIcon;
import com.ravingarinc.api.gui.component.icon.StaticIcon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MenuBuilder implements Builder<Menu> {
    private final List<IconBuilder<?, MenuBuilder>> iconBuilders;
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

    public IconBuilder<Icon, MenuBuilder> addIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate) {
        return addIcon(identifier, display, lore, material, predicate, i -> {
        });
    }

    public IconBuilder<Icon, MenuBuilder> addIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate, final Consumer<ItemStack> consumer) {
        final IconBuilder<Icon, MenuBuilder> newBuilder = new IconBuilder<>(this, new Icon(identifier, display, lore, lastMenu.getIdentifier(), material, null, predicate, consumer));
        iconBuilders.add(newBuilder);
        return newBuilder;
    }

    public IconBuilder<StaticIcon, MenuBuilder> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final int index) {
        return addStaticIcon(identifier, display, lore, material, p -> true, i -> {
        }, index);
    }

    public IconBuilder<StaticIcon, MenuBuilder> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate, final int index) {
        return addStaticIcon(identifier, display, lore, material, predicate, i -> {
        }, index);
    }

    public IconBuilder<StaticIcon, MenuBuilder> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final Consumer<ItemStack> consumer, final int index) {
        return addStaticIcon(identifier, display, lore, material, p -> true, consumer, index);
    }

    public IconBuilder<StaticIcon, MenuBuilder> addStaticIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate, final Consumer<ItemStack> consumer, final int index) {
        final IconBuilder<StaticIcon, MenuBuilder> newBuilder = new IconBuilder<>(this, new StaticIcon(identifier, display, lore, lastMenu.getIdentifier(), material, null, predicate, consumer, index));
        iconBuilders.add(newBuilder);
        return newBuilder;
    }

    public <T> StateIconBuilder<T> addStateIcon(final String identifier, final Action action, final int index, final Supplier<T> determiner) {
        final StateIconBuilder<T> newBuilder = new StateIconBuilder<>(this, new StateIcon<>(identifier, lastMenu.getIdentifier(), action, t -> true, index, determiner));
        iconBuilders.add(newBuilder);
        return newBuilder;
    }


    public IconBuilder<PlaceableIcon, MenuBuilder> addPlaceableIcon(final String identifier, final int index, final Predicate<ItemStack> validator) {
        final IconBuilder<PlaceableIcon, MenuBuilder> newBuilder = new IconBuilder<>(this, new PlaceableIcon(identifier, lastMenu.getIdentifier(), index, validator));
        iconBuilders.add(newBuilder);
        return newBuilder;
    }

    public IconBuilder<PlaceableIcon, MenuBuilder> addPlaceableIcon(final String identifier, final int index, final Predicate<ItemStack> validator, final ItemStack placeholder) {
        final IconBuilder<PlaceableIcon, MenuBuilder> newBuilder = new IconBuilder<>(this, new PlaceableIcon(identifier, lastMenu.getIdentifier(), index, validator, placeholder));
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

    public MenuBuilder addInputComponent(final String identifier, final String description, final Consumer<String> onResponse) {
        lastMenu.addChild(() -> new InputComponent(identifier, lastMenu.getIdentifier(), description, onResponse));
        return this;
    }

    public MenuBuilder addMiscComponent(final Supplier<Component> component) {
        lastMenu.addChild(component);
        return this;
    }

    public void handleLastPageBuilder() {
        if (lastPageBuilder != null) {
            final Page page = lastPageBuilder.get();
            lastMenu.addChild(() -> page);
            lastPageBuilder = null;
        }
    }

    @Override
    public Menu reference() {
        return lastMenu;
    }

    @Override
    public Menu get() {
        lastMenu.finalise();
        handleLastPageBuilder();
        iconBuilders.forEach(builder -> {
            final Component c = builder.get();
            lastMenu.addChild(() -> c);
        });
        iconBuilders.clear();
        return lastMenu;
    }
}
