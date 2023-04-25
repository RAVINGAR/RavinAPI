package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;
import java.util.function.Consumer;

public class ConsumeMetaAction extends Action {
    private final String menu;
    private final Consumer<ItemMeta> meta;

    public ConsumeMetaAction(final String pointer, final String menu, final Consumer<ItemMeta> metaConsumer) {
        super(pointer);
        this.menu = menu;
        this.meta = metaConsumer;
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        final Optional<Interactive> component = gui.findComponent(Component.MENU, menu)
                .flatMap(m -> m.findComponent(Component.INTERACTIVE, pointer));
        if (component.isPresent()) {
            final Interactive interactive = component.get();
            interactive.updateMeta(meta);
        }
    }
}
