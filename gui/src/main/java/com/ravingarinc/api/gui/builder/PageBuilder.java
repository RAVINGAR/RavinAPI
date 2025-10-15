package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.Page;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.action.NextPageAction;
import com.ravingarinc.api.gui.component.action.PreviousPageAction;
import com.ravingarinc.api.gui.component.icon.PageFiller;
import com.ravingarinc.api.gui.component.icon.PageIcon;
import com.ravingarinc.api.gui.component.icon.StaticIcon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

public class PageBuilder implements Builder<Page> {
    private final Page page;

    private final MenuBuilder parent;
    private final String parentIdentifier;

    private final List<Builder<?>> builders;

    private int lastId = 0;

    public PageBuilder(final String identifier, final MenuBuilder parent, final int... slots) {
        parentIdentifier = parent.reference().getIdentifier();
        page = new Page(identifier, parentIdentifier, slots);
        this.parent = parent;
        builders = new ArrayList<>();
    }

    @Deprecated
    public void with(final Consumer<PageBuilder> builder) {
        builder.accept(this);
    }

    public IconBuilder<StaticIcon, PageBuilder> addNextPageIcon(final int index) {
        return addNextPageIcon(Material.ARROW, index, i -> {
        });
    }

    public IconBuilder<StaticIcon, PageBuilder> addNextPageIcon(final Material material, final int index, final Consumer<ItemStack> consumer) {
        return addNextPageIcon("&eNext Page", "&7Navigate to the\n&7next page.", material, index, consumer);
    }



    public IconBuilder<StaticIcon, PageBuilder> addPreviousPageIcon(final int index) {
        return addPreviousPageIcon(Material.ARROW, index, i -> {
        });
    }

    public IconBuilder<StaticIcon, PageBuilder> addPreviousPageIcon(final Material material, final int index, final Consumer<ItemStack> consumer) {
        return addPreviousPageIcon("&ePrevious Page", "&7Navigate to the\n&7previous page.", material, index, consumer);
    }

    public PageBuilder previousPageIcon(final int index, Consumer<IconBuilder<StaticIcon, PageBuilder>> builder) {
        final var icon = addPreviousPageIcon(index);
        builder.accept(icon);
        return this;
    }

    public PageBuilder nextPageIcon(final int index, Consumer<IconBuilder<StaticIcon, PageBuilder>> builder) {
        final var icon = addNextPageIcon(index);
        builder.accept(icon);
        return this;
    }

    public PageBuilder previousPageIcon(final int index) {
        return previousPageIcon(index, icon -> {
        });
    }

    public PageBuilder nextPageIcon(final int index) {
        return nextPageIcon(index, i -> {
        });
    }

    public IconBuilder<StaticIcon, PageBuilder> addNextPageIcon(final String display, final String lore, final Material material, final int index) {
        return addNextPageIcon(display, lore, material, index, i -> {
        });
    }

    public IconBuilder<StaticIcon, PageBuilder> addNextPageIcon(final String display, final String lore, final Material material, final int index, final Consumer<ItemStack> consumer) {
        final IconBuilder<StaticIcon, PageBuilder> newBuilder = new IconBuilder<>(this, new StaticIcon(
                page.getIdentifier() + "_NEXT_ICON",
                display,
                lore,
                parentIdentifier,
                material,
                new NextPageAction(page.getIdentifier(), parentIdentifier),
                (g, p) -> this.page.hasNextPage(), consumer, index));
        parent.addBuilder(newBuilder);

        return newBuilder;
    }

    public IconBuilder<StaticIcon, PageBuilder> addPreviousPageIcon(final String display, final String lore, final Material material, final int index) {
        return addPreviousPageIcon(display, lore, material, index, i -> {
        });

    }

    public IconBuilder<StaticIcon, PageBuilder> addPreviousPageIcon(final String display, final String lore, final Material material, final int index, final Consumer<ItemStack> consumer) {
        final IconBuilder<StaticIcon, PageBuilder> newBuilder = new IconBuilder<>(this, new StaticIcon(
                page.getIdentifier() + "_PREVIOUS_ICON",
                display,
                lore,
                parentIdentifier,
                material,
                new PreviousPageAction(page.getIdentifier(), parentIdentifier),
                (g, p) -> this.page.hasPreviousPage(), consumer, index));
        parent.addBuilder(newBuilder);

        return newBuilder;
    }

    public IconBuilder<PageIcon, PageBuilder> addPageIcon(final String identifier, final String display, final String lore, final Material material, final BiPredicate<BaseGui, Player> predicate) {
        final IconBuilder<PageIcon, PageBuilder> builder = new IconBuilder<>(this, new PageIcon(identifier, display, lore, page.getIdentifier(), material, predicate, (t) -> {
        }));
        builders.add(builder);
        return builder;
    }

    public IconBuilder<PageIcon, PageBuilder> addPageIcon(final String identifier, final String display, final String lore, final Material material) {
        final IconBuilder<PageIcon, PageBuilder> builder = new IconBuilder<>(this, new PageIcon(identifier, display, lore, page.getIdentifier(), material, (g, p) -> true, (t) -> {
        }));
        builders.add(builder);
        return builder;
    }

    public IconBuilder<PageIcon, PageBuilder> addPageIcon(final String identifier, final String display, final String lore, final Material material, final Action action) {
        final IconBuilder<PageIcon, PageBuilder> builder = new IconBuilder<>(this, new PageIcon(identifier, display, lore, page.getIdentifier(), material, (g, p) -> true, (t) -> {
        }));
        builders.add(builder);
        builder.getActionBuilder().addMiscAction(action);
        return builder;
    }

    public PageBuilder pageIcon(final String display, final String lore, final Material material,
                                final Consumer<IconBuilder<PageIcon, PageBuilder>> builder) {
        final var icon = addPageIcon("page_icon_" + lastId++, display, lore, material);
        builder.accept(icon);
        return this;
    }

    public PageBuilder pageIcon(@NotNull final Supplier<String> display, @NotNull final Supplier<String> lore, @NotNull final Supplier<Material> material, final BiPredicate<BaseGui, Player> predicate,
                                final Consumer<IconBuilder<PageIcon, PageBuilder>> builder) {
        final IconBuilder<PageIcon, PageBuilder> icon = new IconBuilder<>(this, new PageIcon("page_icon_" + lastId++, "", "", page.getIdentifier(), Material.STONE, predicate, (t) -> {
        }));
        builders.add(icon);
        icon.addItemUpdater(
                (i, p) -> display.get(),
                (i, p) -> lore.get(),
                (i, p) -> material.get());
        builder.accept(icon);
        return this;
    }

    public <T> PageFillerBuilder<T> addPageFiller(final String identifier,
                                                  final BiFunction<BaseGui, Player, Collection<T>> iterableSupplier) {
        final PageFillerBuilder<T> builder = new PageFillerBuilder<>(identifier, this, iterableSupplier);
        builders.add(builder);
        return builder;
    }

    public <T> PageFillerBuilder<T> addPageFiller(final String identifier, final Function<BaseGui, Collection<T>> iterableSupplier) {
        return addPageFiller(identifier, (gui, player) -> iterableSupplier.apply(gui));
    }

    public <T> PageFillerBuilder<T> addPageFiller(final String identifier, final Supplier<Collection<T>> iterableSupplier) {
        return addPageFiller(identifier, (gui) -> iterableSupplier.get());
    }

    public <T> PageBuilder pageFiller(final BiFunction<BaseGui, Player, Collection<T>> iterableSupplier,
                                      final Consumer<PageFillerBuilder<T>> builder) {
        final var filler = addPageFiller("page_filler_" + lastId++, iterableSupplier);
        builder.accept(filler);
        return this;
    }

    public <T> PageBuilder pageFiller(final Function<BaseGui, Collection<T>> iterableSupplier,
                                      final Consumer<PageFillerBuilder<T>> builder) {
        return pageFiller((gui, player) -> iterableSupplier.apply(gui), builder);
    }

    public <T> PageBuilder pageFiller(final Supplier<Collection<T>> iterableSupplier,
                                      final Consumer<PageFillerBuilder<T>> builder) {
        return pageFiller((gui, player) -> iterableSupplier.get(), builder);
    }

    @Override
    public Page reference() {
        return page;
    }

    @Override
    public Page get() {
        builders.forEach(builder -> page.addChild(builder::get));
        builders.clear();
        return page;
    }

    public MenuBuilder finalise() {
        builders.forEach(builder -> page.addChild(builder::get));
        builders.clear();
        return parent;
    }

    public class PageFillerBuilder<T> implements Builder<PageFiller<T>> {
        private final String identifier;
        private final BiFunction<BaseGui, Player, Collection<T>> iterableSupplier;
        private final List<BiFunction<BaseGui, T, Action>> actionsToAdd;
        private final List<BiFunction<PageIcon, T, Component>> componentsToAdd;
        private final PageBuilder parent;
        private BiFunction<BaseGui, T, String> identifierProvider = null;
        private BiFunction<BaseGui, T, String> nameProvider = null;
        private BiFunction<BaseGui, T, String> loreProvider = null;
        private BiFunction<BaseGui, T, Material> materialProvider = null;
        private BiFunction<BaseGui, T, BiPredicate<BaseGui, Player>> predicateProvider = null;
        private BiFunction<BaseGui, T, Consumer<ItemStack>> consumerProvider = null;

        public PageFillerBuilder(final String identifier, final PageBuilder builder, final Supplier<Collection<T>> iterableSupplier) {
            this(identifier, builder, (gui, player) -> iterableSupplier.get());
        }

        public PageFillerBuilder(final String identifier, final PageBuilder builder, final BiFunction<BaseGui, Player,
                Collection<T>> iterableSupplier) {
            this.iterableSupplier = iterableSupplier;
            this.identifier = identifier;
            this.parent = builder;
            this.actionsToAdd = new ArrayList<>();
            this.componentsToAdd = new ArrayList<>();
        }

        public PageFillerBuilder<T> setIdentifierProvider(@NotNull final Function<T, String> provider) {
            return setIdentifierProvider((g, t) -> provider.apply(t));
        }

        public PageFillerBuilder<T> setDisplayNameProvider(@NotNull final Function<T, String> provider) {
            return setDisplayNameProvider((g, t) -> provider.apply(t));
        }

        public PageFillerBuilder<T> setLoreProvider(@NotNull final Function<T, String> provider) {
            return setLoreProvider((g, t) -> provider.apply(t));
        }

        public PageFillerBuilder<T> setMaterialProvider(@NotNull final Function<T, Material> provider) {
            return setMaterialProvider((g, t) -> provider.apply(t));
        }

        public PageFillerBuilder<T> setPredicateProvider(@NotNull final Function<T, BiPredicate<BaseGui, Player>> provider) {
            return setPredicateProvider((g, t) -> provider.apply(t));
        }

        public PageFillerBuilder<T> setConsumerProvider(@NotNull final Function<T, Consumer<ItemStack>> provider) {
            return setConsumerProvider((g, t) -> provider.apply(t));
        }

        public PageFillerBuilder<T> addActionProvider(@NotNull final Function<T, Action> provider) {
            return addActionProvider((g, t) -> provider.apply(t));
        }

        public PageFillerBuilder<T> setIdentifierProvider(@NotNull final BiFunction<BaseGui, T, String> provider) {
            this.identifierProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> setDisplayNameProvider(@NotNull final BiFunction<BaseGui, T, String> provider) {
            this.nameProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> setLoreProvider(@NotNull final BiFunction<BaseGui, T, String> provider) {
            this.loreProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> setMaterialProvider(@NotNull final BiFunction<BaseGui, T, Material> provider) {
            this.materialProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> setPredicateProvider(@NotNull final BiFunction<BaseGui, T, BiPredicate<BaseGui, Player>> provider) {
            this.predicateProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> setConsumerProvider(@NotNull final BiFunction<BaseGui, T, Consumer<ItemStack>> provider) {
            this.consumerProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> addActionProvider(@NotNull final BiFunction<BaseGui, T, Action> provider) {
            this.actionsToAdd.add(provider);
            return this;
        }

        public PageFillerBuilder<T> addComponentProvider(@NotNull final BiFunction<PageIcon, T, Component> provider) {
            this.componentsToAdd.add(provider);
            return this;
        }

        @Override
        @Deprecated
        public PageFiller<T> reference() {
            throw new UnsupportedOperationException("Cannot get reference on PageFillerBuilder!");
        }

        @Override
        public PageFiller<T> get() {
            if (nameProvider == null || loreProvider == null || materialProvider == null) {
                throw new IllegalArgumentException("Cannot get() sizeable page icon when name, lore or material provider is null for page filler " + identifier);
            }

            if (identifierProvider == null) {
                final AtomicInteger counter = new AtomicInteger(0);
                identifierProvider = (g, i) -> identifier + "_FILLER_PAGE_ICON_" + counter.getAndIncrement();
            }
            final BiFunction<BaseGui, T, PageIcon> function = (gui, val) -> {
                final PageIcon icon = new PageIcon(
                        identifierProvider.apply(gui, val),
                        nameProvider.apply(gui, val),
                        loreProvider.apply(gui, val),
                        identifier,
                        materialProvider.apply(gui, val),
                        predicateProvider == null ? (g, p) -> true : predicateProvider.apply(gui, val),
                        consumerProvider == null ? (i) -> {
                        } : consumerProvider.apply(gui, val));
                actionsToAdd.forEach(fun -> icon.addAction(fun.apply(gui, val)));
                componentsToAdd.forEach(com -> {
                    Component component = com.apply(icon, val);
                    icon.addChild(() -> component);
                });

                return icon;
            };
            return new PageFiller<>(identifier, page, function, iterableSupplier);
        }

        public PageBuilder finalise() {
            return parent;
        }
    }
}
