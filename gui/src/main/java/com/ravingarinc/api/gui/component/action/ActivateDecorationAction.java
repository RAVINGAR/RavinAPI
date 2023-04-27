package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.builder.GuiProvider;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ActivateDecorationAction extends Action {
    private final String menu;
    private final Decoration.Pattern pattern;
    private final Material material;

    private final Supplier<Long> duration;

    public ActivateDecorationAction(final String pointer, final String menu, final Decoration.Pattern pattern, final Material material, final Supplier<Long> duration) {
        super(pointer);
        this.pattern = pattern;
        this.material = material;
        this.menu = menu;
        this.duration = duration;
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        final Optional<Menu> menuOp = gui.findComponent(Component.MENU, menu);
        if (menuOp.isPresent()) {
            final Optional<Decoration> decor = menuOp.get().findComponent(Component.DECORATION, pointer + "_DECORATION");
            decor.ifPresentOrElse(
                    d -> d.updateWithPattern(pattern, material, duration.get(), gui, performer)
                    , () -> GuiProvider.log(Level.WARNING, "Could not find decoration of " + pointer));
        }
    }
}
