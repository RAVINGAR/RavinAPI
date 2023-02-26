package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Builder;
import com.ravingarinc.api.gui.component.Page;
import com.ravingarinc.api.gui.component.icon.PageIcon;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PageBuilder implements Builder<Page> {
    private final Page page;

    private final List<IconBuilder<PageIcon, PageBuilder>> builders;

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
}
