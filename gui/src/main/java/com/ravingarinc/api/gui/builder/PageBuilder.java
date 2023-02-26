package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.component.Page;
import com.ravingarinc.api.gui.component.icon.PageIcon;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PageBuilder {
    private final Page page;

    private final List<PageIconBuilder> builders;

    public PageBuilder(final String identifier, final String parent, final int... slots) {
        page = new Page(identifier, parent, slots);
        builders = new ArrayList<>();
    }

    public PageIconBuilder addPageIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate) {
        final PageIconBuilder builder = new PageIconBuilder(this, new PageIcon(identifier, display, lore, page.getIdentifier(), material, predicate, (t) -> {
        }));
        builders.add(builder);
        return builder;
    }

    public Page getPage() {
        builders.forEach(builder -> page.addChild(builder.getIcon()));
        builders.clear();
        return page;
    }

    public static class PageIconBuilder extends IconBuilder<PageIcon, PageBuilder> {
        protected PageIconBuilder(final PageBuilder owner, final PageIcon icon) {
            super(owner, icon);
        }

        public IconBuilder<PageIcon, PageBuilder> setDynamic() {
            return setDynamic(owner.page);
        }
    }
}
