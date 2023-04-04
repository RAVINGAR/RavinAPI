package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.ActionBuilder;
import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.action.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseActionBuilder<P> implements ActionBuilder<P> {
    protected final Actionable reference;
    protected final List<Action> actionsToAdd = new LinkedList<>();
    protected final String lastMenu;

    public BaseActionBuilder(final Actionable reference, final String lastMenu) {
        this.reference = reference;
        this.lastMenu = lastMenu;
    }

    public BaseActionBuilder<P> addActivateDecorationAction(final String pointer, final Decoration.Pattern pattern, final Material updateMaterial, final Supplier<Long> duration) {
        actionsToAdd.add(new ActivateDecorationAction(pointer, lastMenu, pattern, updateMaterial, duration));
        return this;
    }

    public BaseActionBuilder<P> addChangeAmountAction(final String pointer, final int increment) {
        actionsToAdd.add(new ChangeAmountAction(pointer, increment));
        return this;
    }

    public BaseActionBuilder<P> addSoundAction(final Sound sound, final float volume, final float pitch) {
        actionsToAdd.add(new SoundAction(sound, volume, pitch));
        return this;
    }

    public BaseActionBuilder<P> addLockPlaceableAction(final String pointer, final boolean lock) {
        actionsToAdd.add(new LockPlaceableAction(pointer, lastMenu, lock));
        return this;
    }

    public BaseActionBuilder<P> addMenuAction(final String pointer) {
        actionsToAdd.add(new MenuAction(pointer));
        return this;
    }

    public BaseActionBuilder<P> addUpdateComponentAction(final String pointer, final Supplier<String> name, final Supplier<String> lore, final Material material) {
        return addUpdateComponentAction(pointer, lastMenu, name, lore, material);
    }

    public BaseActionBuilder<P> addUpdateComponentAction(final String pointer, final String menu, final Supplier<String> name, final Supplier<String> lore, final Material material) {
        return addUpdateComponentAction(pointer, menu, (g) -> name.get(), (g) -> lore.get(), material);
    }

    public BaseActionBuilder<P> addUpdateComponentAction(final String pointer, final Function<BaseGui, String> name, final Function<BaseGui, String> lore, final Material material) {
        return addUpdateComponentAction(pointer, lastMenu, name, lore, material);
    }

    public BaseActionBuilder<P> addUpdateComponentAction(final String pointer, final String menu, final Function<BaseGui, String> name, final Function<BaseGui, String> lore, final Material material) {
        actionsToAdd.add(new UpdateComponentAction(pointer, menu, name, lore, material));
        return this;
    }

    public <T, Z> BaseActionBuilder<P> addUpdateMetaAction(final String pointer, final String menu, final PersistentDataType<T, Z> type, final String key, final Z value) {
        actionsToAdd.add(new UpdateMetaAction<>(pointer, menu, type, key, value));
        return this;
    }

    public BaseActionBuilder<P> addRefreshGuiAction() {
        actionsToAdd.add(new RefreshGuiAction());
        return this;
    }

    public BaseActionBuilder<P> addConsumeMetaAction(final String pointer, final Consumer<ItemMeta> consumer) {
        actionsToAdd.add(new ConsumeMetaAction(pointer, lastMenu, consumer));
        return this;
    }

    public BaseActionBuilder<P> addMiscAction(final Action action) {
        actionsToAdd.add(action);
        return this;
    }

    public Actionable getActionable() {
        Collections.sort(actionsToAdd);
        actionsToAdd.forEach(reference::addAction);
        actionsToAdd.clear();

        return reference;
    }
}
