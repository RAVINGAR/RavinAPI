package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.action.ActivateDecorationAction;
import com.ravingarinc.api.gui.component.action.ChangeAmountAction;
import com.ravingarinc.api.gui.component.action.EventAction;
import com.ravingarinc.api.gui.component.action.LockPlaceableAction;
import com.ravingarinc.api.gui.component.action.MenuAction;
import com.ravingarinc.api.gui.component.action.RefreshGuiAction;
import com.ravingarinc.api.gui.component.action.SoundAction;
import com.ravingarinc.api.gui.component.action.UpdateComponentAction;
import com.ravingarinc.api.gui.component.action.UpdateMetaAction;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class EventActionBuilder extends BaseActionBuilder {
    private final List<Action> postActionsToAdd = new ArrayList<>();
    private final BaseActionBuilder parent;

    public EventActionBuilder(final Actionable reference, final String lastMenu, final BaseActionBuilder parent) {
        super(reference, lastMenu);
        this.parent = parent;
    }

    public BaseActionBuilder finalise() {
        parent.handleActionBuilder(this);
        return parent;
    }

    @Override
    public void build() {
        parent.addAction((Action) getActionable());
    }

    @Override
    public EventActionBuilder addActivateDecorationAction(final String pointer, final Decoration.Pattern pattern, final Material updateMaterial, final Supplier<Long> duration) {
        return (EventActionBuilder) super.addActivateDecorationAction(pointer, pattern, updateMaterial, duration);
    }

    @Override
    public EventActionBuilder addSoundAction(final Sound sound, final float volume, final float pitch) {
        return (EventActionBuilder) super.addSoundAction(sound, volume, pitch);
    }

    @Override
    public EventActionBuilder addChangeAmountAction(final String pointer, final int increment) {
        return (EventActionBuilder) super.addChangeAmountAction(pointer, increment);
    }

    @Override
    public EventActionBuilder addLockPlaceableAction(final String pointer, final boolean lock) {
        return (EventActionBuilder) super.addLockPlaceableAction(pointer, lock);
    }

    @Override
    public EventActionBuilder addMenuAction(final String pointer) {
        return (EventActionBuilder) super.addMenuAction(pointer);
    }

    @Override
    public EventActionBuilder addUpdateComponentAction(final String pointer, @NotNull final Supplier<String> name, @NotNull final Supplier<String> lore, final Material material) {
        return (EventActionBuilder) super.addUpdateComponentAction(pointer, name, lore, material);
    }

    @Override
    public <T, Z> EventActionBuilder addUpdateMetaAction(final String pointer, final String menu, final PersistentDataType<T, Z> type, final String key, final Z value) {
        return (EventActionBuilder) super.addUpdateMetaAction(pointer, menu, type, key, value);
    }

    @Override
    public EventActionBuilder addRefreshGuiAction() {
        return (EventActionBuilder) super.addRefreshGuiAction();
    }

    //Post Actions

    public EventActionBuilder addPostActivateDecorationAction(final String pointer, final Decoration.Pattern pattern, final Material updateMaterial, final Supplier<Long> duration) {
        postActionsToAdd.add(new ActivateDecorationAction(pointer, lastMenu, pattern, updateMaterial, duration));
        return this;
    }

    public EventActionBuilder addPostSoundAction(final Sound sound, final float volume, final float pitch) {
        postActionsToAdd.add(new SoundAction(sound, volume, pitch));
        return this;
    }

    public EventActionBuilder addPostChangeAmountAction(final String pointer, final int increment) {
        postActionsToAdd.add(new ChangeAmountAction(pointer, increment));
        return this;
    }

    public EventActionBuilder addPostLockPlaceableAction(final String pointer, final boolean lock) {
        postActionsToAdd.add(new LockPlaceableAction(pointer, lastMenu, lock));
        return this;
    }

    public EventActionBuilder addPostMenuAction(final String pointer) {
        postActionsToAdd.add(new MenuAction(pointer));
        return this;
    }

    public EventActionBuilder addPostUpdateComponentAction(final String pointer, @NotNull final Supplier<String> name, @NotNull final Supplier<String> lore, final Material material) {
        postActionsToAdd.add(new UpdateComponentAction(pointer, lastMenu, name, lore, material));
        return this;
    }

    public <T, Z> EventActionBuilder addPostUpdateMetaAction(final String pointer, final String menu, final PersistentDataType<T, Z> type, final String key, final Z value) {
        postActionsToAdd.add(new UpdateMetaAction<>(pointer, menu, type, key, value));
        return this;
    }

    public EventActionBuilder addPostRefreshGuiAction() {
        postActionsToAdd.add(new RefreshGuiAction());
        return this;
    }

    @Override
    public Actionable getActionable() {
        Collections.sort(postActionsToAdd);
        postActionsToAdd.forEach(action -> {
            if (reference instanceof EventAction e) {
                e.addPostAction(action);
            }
        });

        return super.getActionable();
    }
}
