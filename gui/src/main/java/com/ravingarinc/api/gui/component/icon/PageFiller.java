package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Element;
import com.ravingarinc.api.gui.builder.GuiProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;

public class PageFiller<P> extends Element {
    private final BiFunction<BaseGui, P, PageIcon> forEach;
    private final Function<BaseGui, Collection<P>> iterableSupplier;

    private final Map<String, PageIcon> lastIcons;

    public PageFiller(final String identifier, final String parent, final BiFunction<BaseGui, P, PageIcon> forEach, final Function<BaseGui, Collection<P>> iterableSupplier) {
        super(identifier, parent, 0);
        this.forEach = forEach;
        this.iterableSupplier = iterableSupplier;
        this.lastIcons = new HashMap<>();
    }


    @Override
    public void fillElement(final BaseGui gui, Player player) {
        super.fillElement(gui, player);
        gui.findComponent(Component.PAGE, parent).ifPresentOrElse(page -> {
            lastIcons.clear();
            iterableSupplier.apply(gui).stream().map(t -> forEach.apply(gui, t)).filter(i -> i.canDisplay(gui)).forEachOrdered(icon -> {
                page.queueIconToPlace(icon.getItem());
                lastIcons.put(icon.getIdentifier(), icon);
            });
        }, () -> GuiProvider.log(Level.WARNING, "Could not find page component for identifier " + parent));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T extends Component> Optional<T> findComponent(final T type, final String identifier) {
        if (INTERACTIVE.isSameType(type)) {
            final PageIcon icon = lastIcons.get(ChatColor.stripColor(identifier));
            if (icon != null) {
                return Optional.of((T) icon);
            }
        }
        return super.findComponent(type, identifier);
    }

    @Override
    public Class<PageFiller> getThisClass() {
        return PageFiller.class;
    }
}
