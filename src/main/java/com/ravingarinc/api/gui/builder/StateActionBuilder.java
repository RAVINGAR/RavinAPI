package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.action.Action;
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

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StateActionBuilder extends BaseActionBuilder {
    public StateActionBuilder(final Actionable reference, final String lastMenu) {
        super(reference, lastMenu);
    }

    @Override
    public void build() {
        getActionable();
    }

    @Override
    public StateActionBuilder addActivateDecorationAction(final String pointer, final Decoration.Pattern pattern, final Material updateMaterial, final Supplier<Long> duration) {
        super.addActivateDecorationAction(pointer, pattern, updateMaterial, duration);
        return this;
    }

    @Override
    public StateActionBuilder addChangeAmountAction(final String pointer, final int increment) {
        actionsToAdd.add(new ChangeAmountAction(pointer, increment));
        return this;
    }

    /**
     * @return An action builder for the specified EventAction
     */
    @Override
    public EventActionBuilder addEventAction(final EventAction action) {
        final EventActionBuilder eventBuilder = new EventActionBuilder(action, lastMenu, this);
        actionBuilders.add(eventBuilder);
        return eventBuilder;
    }

    @Override
    public StateActionBuilder addSoundAction(final Sound sound, final float volume, final float pitch) {
        actionsToAdd.add(new SoundAction(sound, volume, pitch));
        return this;
    }

    @Override
    public StateActionBuilder addLockPlaceableAction(final String pointer, final boolean lock) {
        actionsToAdd.add(new LockPlaceableAction(pointer, lastMenu, lock));
        return this;
    }

    @Override
    public StateActionBuilder addMenuAction(final String pointer) {
        actionsToAdd.add(new MenuAction(pointer));
        return this;
    }

    @Override
    public StateActionBuilder addUpdateComponentAction(final String pointer, final Supplier<String> name, final Supplier<String> lore, final Material material) {
        actionsToAdd.add(new UpdateComponentAction(pointer, lastMenu, name, lore, material));
        return this;
    }

    @Override
    public StateActionBuilder addUpdateComponentAction(final String pointer, final String menu, final Supplier<String> name, final Supplier<String> lore, final Material material) {
        actionsToAdd.add(new UpdateComponentAction(pointer, menu, name, lore, material));
        return this;
    }

    @Override
    protected <T, Z> BaseActionBuilder addUpdateMetaAction(final String pointer, final String menu, final PersistentDataType<T, Z> type, final String key, final Z value) {
        actionsToAdd.add(new UpdateMetaAction<>(pointer, menu, type, key, value));
        return this;
    }

    @Override
    public StateActionBuilder addRefreshGuiAction() {
        actionsToAdd.add(new RefreshGuiAction());
        return this;
    }

    @Override
    public StateActionBuilder addConsumeMetaAction(final String pointer, final Consumer<ItemMeta> consumer) {
        actionsToAdd.add(new ConsumeMetaAction(pointer, lastMenu, consumer));
        return this;
    }

    @Override
    public StateActionBuilder addMiscAction(final Action action) {
        actionsToAdd.add(action);
        return this;
    }
}
