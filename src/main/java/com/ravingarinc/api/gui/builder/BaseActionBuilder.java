package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.action.ActivateDecorationAction;
import com.ravingarinc.api.gui.component.action.ChangeAmountAction;
import com.ravingarinc.api.gui.component.action.ConsumeMetaAction;
import com.ravingarinc.api.gui.component.action.EventAction;
import com.ravingarinc.api.gui.component.action.LockPlaceableAction;
import com.ravingarinc.api.gui.component.action.MenuAction;
import com.ravingarinc.api.gui.component.action.RefreshGuiAction;
import com.ravingarinc.api.gui.component.action.SoundAction;
import com.ravingarinc.api.gui.component.action.UpdateComponentAction;
import com.ravingarinc.api.gui.component.action.UpdateMetaAction;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public abstract class BaseActionBuilder {
    protected final Actionable reference;
    protected final List<Action> actionsToAdd = new LinkedList<>();
    protected final List<BaseActionBuilder> actionBuilders;
    protected final String lastMenu;

    public BaseActionBuilder(final Actionable reference, final String lastMenu) {
        this.reference = reference;
        actionBuilders = new LinkedList<>();
        this.lastMenu = lastMenu;
    }

    protected BaseActionBuilder addActivateDecorationAction(final String pointer, final Decoration.Pattern pattern, final Material updateMaterial, final Supplier<Long> duration) {
        actionsToAdd.add(new ActivateDecorationAction(pointer, lastMenu, pattern, updateMaterial, duration));
        return this;
    }

    protected BaseActionBuilder addChangeAmountAction(final String pointer, final int increment) {
        actionsToAdd.add(new ChangeAmountAction(pointer, increment));
        return this;
    }

    /**
     * @return An action builder for the specified EventAction
     */
    public EventActionBuilder addEventAction(final EventAction action) {
        final EventActionBuilder eventBuilder = new EventActionBuilder(action, lastMenu, this);
        actionBuilders.add(eventBuilder);
        return eventBuilder;
    }

    protected BaseActionBuilder addSoundAction(final Sound sound, final float volume, final float pitch) {
        actionsToAdd.add(new SoundAction(sound, volume, pitch));
        return this;
    }

    protected BaseActionBuilder addLockPlaceableAction(final String pointer, final boolean lock) {
        actionsToAdd.add(new LockPlaceableAction(pointer, lastMenu, lock));
        return this;
    }

    protected BaseActionBuilder addMenuAction(final String pointer) {
        actionsToAdd.add(new MenuAction(pointer));
        return this;
    }

    protected BaseActionBuilder addUpdateComponentAction(final String pointer, final Supplier<String> name, final Supplier<String> lore, final Material material) {
        actionsToAdd.add(new UpdateComponentAction(pointer, lastMenu, name, lore, material));
        return this;
    }

    protected BaseActionBuilder addUpdateComponentAction(final String pointer, final String menu, final Supplier<String> name, final Supplier<String> lore, final Material material) {
        actionsToAdd.add(new UpdateComponentAction(pointer, menu, name, lore, material));
        return this;
    }

    protected <T, Z> BaseActionBuilder addUpdateMetaAction(final String pointer, final String menu, final PersistentDataType<T, Z> type, final String key, final Z value) {
        actionsToAdd.add(new UpdateMetaAction<>(pointer, menu, type, key, value));
        return this;
    }

    protected BaseActionBuilder addRefreshGuiAction() {
        actionsToAdd.add(new RefreshGuiAction());
        return this;
    }

    protected BaseActionBuilder addConsumeMetaAction(final String pointer, final Consumer<ItemMeta> consumer) {
        actionsToAdd.add(new ConsumeMetaAction(pointer, lastMenu, consumer));
        return this;
    }

    protected BaseActionBuilder addMiscAction(final Action action) {
        actionsToAdd.add(action);
        return this;
    }

    public BaseActionBuilder addAction(final Action action) {
        actionsToAdd.add(action);
        return this;
    }

    protected void handleActionBuilder(final BaseActionBuilder builder) {
        if (actionBuilders.remove(builder)) {
            builder.build();
        } else {
            I.log(Level.WARNING, "Attempted to handle action builder but it was not found in BaseActionBuilder list!");
        }
    }

    public Actionable getActionable() {
        new LinkedList<>(actionBuilders).forEach(builder -> {
            builder.build();
            actionBuilders.remove(builder);
        });

        Collections.sort(actionsToAdd);
        actionsToAdd.forEach(reference::addAction);
        actionsToAdd.clear();

        return reference;
    }

    public abstract void build();
}
