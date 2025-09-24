package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Builder;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.*;

public class MenuBuilder implements Builder<Menu> {
    private final List<Builder<?>> builders;
    private final Menu lastMenu;

    private int lastId = 0;

    public MenuBuilder(final GuiBuilder<?> owner, final String identifier, final String parent, final int backIcon) {
        lastMenu = new Menu(identifier, parent, owner.getPrimaryBorder(), owner.getSecondaryBorder(), backIcon);
        builders = new LinkedList<>();
    }

    @Deprecated
    public void with(final Consumer<MenuBuilder> builder) {
        builder.accept(this);
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
    @Deprecated
    public MenuBuilder addMenuIcon(final String display, final String lore, final Material material, final BiPredicate<BaseGui, Player> predicate) {
        lastMenu.addMenuIcon(display, lore, material, predicate);
        return this;
    }

    @Deprecated
    public MenuBuilder addMenuIcon(final String display, final Material material, final BiPredicate<BaseGui, Player> predicate) {
        lastMenu.addMenuIcon(display, "", material, predicate);
        return this;
    }

    @Deprecated
    public MenuBuilder addMenuIcon(final String display, final String lore, final Material material) {
        lastMenu.addMenuIcon(display, lore, material, (g, p) -> true);
        return this;
    }

    @Deprecated
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

    public IconBuilder<Icon, MenuBuilder> addIcon(final String identifier, final String display, final String lore, final Material material, final Action action) {
        return addIcon(identifier, display, lore, material, action, (a, b) -> true, i -> {
        });
    }

    public IconBuilder<Icon, MenuBuilder> addIcon(final String identifier, final String display, final String lore, final Material material) {
        return addIcon(identifier, display, lore, material, null, (a, b) -> true, i -> {
        });
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

    public MenuBuilder staticIcon(final String display, final String lore, final Material material, final int index,
                                  Consumer<IconBuilder<StaticIcon, MenuBuilder>> builder) {
        final var icon = addStaticIcon("static_icon_" + lastId++, display, lore, material, index);
        builder.accept(icon);
        return this;
    }

    public MenuBuilder staticIcon(final Supplier<String> dynamicDisplay,
                                  final Supplier<String> dynamicLore,
                                  final Supplier<Material> dynamicMaterial,
                                  final int index, Consumer<IconBuilder<StaticIcon, MenuBuilder>> builder) {
        return staticIcon(
                (i, p) -> dynamicDisplay.get(),
                (i, p) -> dynamicLore.get(),
                (i, p) -> dynamicMaterial.get(),
                index, builder);
    }

    public MenuBuilder staticIcon(final BiFunction<Interactive, Player, String> dynamicDisplay,
                                  final BiFunction<Interactive, Player, String> dynamicLore,
                                  final BiFunction<Interactive, Player, Material> dynamicMaterial,
                                  final int index, Consumer<IconBuilder<StaticIcon, MenuBuilder>> builder) {
        final var icon = addStaticIcon("static_icon_" + lastId++, "", "", Material.STONE, index);
        icon.addItemUpdater(dynamicDisplay, dynamicLore, dynamicMaterial);
        builder.accept(icon);
        return this;
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

    public <T> MenuBuilder stateIcon(final Action action, final int index, final Function<BaseGui, T> determiner,
                                     Consumer<StateIconBuilder<T>> builder) {
        final var state = addStateIcon("state_icon_" + lastId++, action, index, determiner);
        builder.accept(state);
        return this;
    }

    public GuiObserverActionBuilder<Menu, MenuBuilder> addObserver(final Predicate<BaseGui> predicate) {
        final GuiObserverActionBuilder<Menu, MenuBuilder> newBuilder = new GuiObserverActionBuilder<>(lastMenu, predicate, this);
        builders.add(newBuilder);
        return newBuilder;
    }

    public MenuBuilder observer(final Predicate<BaseGui> predicate, final Consumer<GuiObserverActionBuilder<Menu, MenuBuilder>> builder) {
        final var observer = addObserver(predicate);
        builder.accept(observer);
        return this;
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

    public IconBuilder<PlaceableIcon, MenuBuilder> addPlaceableIcon(final String identifier, final int index, final ItemStack placeholder, final Predicate<ItemStack> validator, final Consumer<ItemStack> onPlaceItem) {
        return addPlaceableIcon(identifier, index, placeholder, validator, (i, p) -> {
            onPlaceItem.accept(i);
        });
    }

    public IconBuilder<PlaceableIcon, MenuBuilder> addPlaceableIcon(final String identifier, final int index, final ItemStack placeholder, final Predicate<ItemStack> validator, final BiConsumer<ItemStack, Player> onPlaceItem) {
        final IconBuilder<PlaceableIcon, MenuBuilder> newBuilder = new IconBuilder<>(this, new PlaceableIcon(identifier, lastMenu.getIdentifier(), index, placeholder, validator, onPlaceItem));
        builders.add(newBuilder);
        return newBuilder;
    }

    public IconBuilder<PlaceableIcon, MenuBuilder> addPlaceableIcon(final String identifier, final int index,
                                                                    final ItemStack placeholder,
                                                                    final boolean removeItemOnPickup,
                                                                    final Predicate<ItemStack> validator, final BiConsumer<ItemStack, Player> onPlaceItem) {
        final IconBuilder<PlaceableIcon, MenuBuilder> newBuilder = new IconBuilder<>(this,
                new PlaceableIcon(identifier, lastMenu.getIdentifier(), index, placeholder, removeItemOnPickup,
                        validator,
                        onPlaceItem));
        builders.add(newBuilder);
        return newBuilder;
    }

    public MenuBuilder placeableIcon(final String placeholderName, final String placeholderLore,
                                     final Material placeholderMaterial, final int index,
                                     Consumer<PlaceableIconPreBuilder> preBuilder, Consumer<IconBuilder<PlaceableIcon,
                    MenuBuilder>> postBuilder) {
        final var placeableBuilder = new PlaceableIconPreBuilder(placeholderName, placeholderLore, placeholderMaterial, index);
        preBuilder.accept(placeableBuilder);
        final var component = placeableBuilder.build("placeable_icon_" + lastId++, lastMenu.getIdentifier());
        final IconBuilder<PlaceableIcon, MenuBuilder> newBuilder = new IconBuilder<>(this, component);
        builders.add(newBuilder);
        postBuilder.accept(newBuilder);
        return this;
    }

    public PageBuilder addPage(final String identifier, final int... slots) {
        final PageBuilder newBuilder = new PageBuilder(identifier, this, slots);
        builders.add(newBuilder);
        return newBuilder;
    }

    public MenuBuilder page(Consumer<PageBuilder> builder, final int... slots) {
        final var page = addPage("page_" + lastId++, slots);
        builder.accept(page);
        return this;
    }

    protected void addBuilder(final Builder<?> builder) {
        this.builders.add(builder);
    }

    public MenuBuilder addDecoration(final String identifier, final Material material, final int[] slots) {
        lastMenu.addChild(() -> new Decoration(identifier, lastMenu.getIdentifier(), material, slots));
        return this;
    }

    public MenuBuilder decoration(final Material material, final int... slots) {
        lastMenu.addChild(() -> new Decoration("decoration_" + lastId, lastMenu.getIdentifier(), material, slots));
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
