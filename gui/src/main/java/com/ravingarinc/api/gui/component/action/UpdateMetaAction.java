package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.logging.Level;

public class UpdateMetaAction<T, Z> extends Action {
    private final String menu;
    private final String key;
    private final Z value;
    private final PersistentDataType<T, Z> type;

    public UpdateMetaAction(final String pointer, final String menu, final PersistentDataType<T, Z> type, final String key, final Z value) {
        super(pointer);
        this.key = key;
        this.value = value;
        this.type = type;
        this.menu = menu;
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        final Optional<Interactive> component = gui.findComponent(Component.MENU, menu)
                .flatMap(m -> m.findComponent(Component.INTERACTIVE, pointer));
        component.ifPresentOrElse(c -> c.setMeta(type, key, value)
                , () -> I.log(Level.WARNING, "Could not find dynamic child of component " + this.getPointer()));
    }
}
