package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.component.Page;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.icon.PageFiller;
import com.ravingarinc.api.gui.component.icon.PageIcon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PageBuilder implements Builder<Page> {
    private final Page page;

    private final List<Builder<?>> builders;

    public PageBuilder(final String identifier, final String parent, final int... slots) {
        page = new Page(identifier, parent, slots);
        builders = new ArrayList<>();
    }

    public IconBuilder<PageIcon, PageBuilder> addPageIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate) {
        final IconBuilder<PageIcon, PageBuilder> builder = new IconBuilder<>(this, new PageIcon(identifier, display, lore, page.getIdentifier(), material, predicate, (t) -> {
        }));
        builders.add(builder);
        return builder;
    }

    public <T> PageFillerBuilder<T> addPageFiller(final String identifier, final Supplier<Collection<T>> iterableSupplier) {
        final PageFillerBuilder<T> builder = new PageFillerBuilder<>(identifier, this, iterableSupplier);
        builders.add(builder);
        return builder;
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

    public class PageFillerBuilder<T> implements Builder<PageFiller<T>> {
        private final String identifier;
        private final Supplier<Collection<T>> iterableSupplier;
        private final List<Function<T, Action>> actionsToAdd;
        private final PageBuilder parent;
        private Function<T, String> nameProvider = null;
        private Function<T, String> loreProvider = null;
        private Function<T, Material> materialProvider = null;
        private Function<T, Predicate<BaseGui>> predicateProvider = null;
        private Function<T, Consumer<ItemStack>> consumerProvider = null;

        public PageFillerBuilder(final String identifier, final PageBuilder builder, final Supplier<Collection<T>> iterableSupplier) {
            this.iterableSupplier = iterableSupplier;
            this.identifier = identifier;
            this.parent = builder;
            this.actionsToAdd = new ArrayList<>();
        }

        public PageFillerBuilder<T> setDisplayNameProvider(@NotNull final Function<T, String> provider) {
            this.nameProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> setLoreProvider(@NotNull final Function<T, String> provider) {
            this.loreProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> setMaterialProvider(@NotNull final Function<T, Material> provider) {
            this.materialProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> setPredicateProvider(@NotNull final Function<T, Predicate<BaseGui>> provider) {
            this.predicateProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> setConsumerProvider(@NotNull final Function<T, Consumer<ItemStack>> provider) {
            this.consumerProvider = provider;
            return this;
        }

        public PageFillerBuilder<T> addActionProvider(@NotNull final Function<T, Action> provider) {
            this.actionsToAdd.add(provider);
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
                throw new IllegalArgumentException("Cannot get() sizeable page icon when name, lore or material provider is null!");
            }
            final Function<T, PageIcon> function = (val) -> {
                final String name = nameProvider.apply(val);
                final PageIcon icon = new PageIcon(ChatColor.stripColor(name).toUpperCase().replaceAll(" ", "_"),
                        name,
                        loreProvider.apply(val),
                        identifier,
                        materialProvider.apply(val),
                        predicateProvider == null ? (g) -> true : predicateProvider.apply(val),
                        consumerProvider == null ? (i) -> {
                        } : consumerProvider.apply(val));
                actionsToAdd.forEach(fun -> icon.addAction(fun.apply(val)));
                return icon;
            };
            return new PageFiller<>(identifier, page.getIdentifier(), function, iterableSupplier);
        }

        public PageBuilder finalise() {
            return parent;
        }
    }
}
