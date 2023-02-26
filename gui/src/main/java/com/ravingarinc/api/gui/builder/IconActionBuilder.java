package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class IconActionBuilder extends BaseActionBuilder {
    private final IconBuilder<?, ?> parent;

    public IconActionBuilder(final Actionable reference, final String lastMenu, final IconBuilder<?, ?> parent) {
        super(reference, lastMenu);
        this.parent = parent;

    }

    /**
     * Finalises the current action builder and returns the previous builder
     */
    public IconBuilder<?, ?> finalise() {
        parent.handleActionBuilder(this);
        return parent;
    }

    @Override
    public void build() {
        getActionable(); //This doesn't need to be added to anything as it simply "adds" the actions to the reference
        //In this case, the reference is the icon itself.
    }

    @Override
    public IconActionBuilder addMiscAction(final Action action) {
        actionsToAdd.add(action);
        return this;
    }

    @Override
    public IconActionBuilder addActivateDecorationAction(final String pointer, final Decoration.Pattern pattern, final Material updateMaterial, final Supplier<Long> duration) {
        return (IconActionBuilder) super.addActivateDecorationAction(pointer, pattern, updateMaterial, duration);
    }

    @Override
    public IconActionBuilder addChangeAmountAction(final String pointer, final int increment) {
        return (IconActionBuilder) super.addChangeAmountAction(pointer, increment);
    }

    @Override
    public IconActionBuilder addLockPlaceableAction(final String pointer, final boolean lock) {
        return (IconActionBuilder) super.addLockPlaceableAction(pointer, lock);
    }

    @Override
    public IconActionBuilder addMenuAction(final String pointer) {
        return (IconActionBuilder) super.addMenuAction(pointer);
    }


    @Override
    public IconActionBuilder addUpdateComponentAction(final String pointer, @NotNull final Supplier<String> name, @NotNull final Supplier<String> lore, final Material material) {
        return (IconActionBuilder) super.addUpdateComponentAction(pointer, name, lore, material);
    }

    @Override
    public IconActionBuilder addUpdateComponentAction(final String pointer, final String menu, @NotNull final Supplier<String> name, @NotNull final Supplier<String> lore, final Material material) {
        return (IconActionBuilder) super.addUpdateComponentAction(pointer, menu, name, lore, material);
    }

    @Override
    public <T, Z> IconActionBuilder addUpdateMetaAction(final String pointer, final String menu, final PersistentDataType<T, Z> type, final String key, final Z value) {
        return (IconActionBuilder) super.addUpdateMetaAction(pointer, menu, type, key, value);
    }

    @Override
    public IconActionBuilder addRefreshGuiAction() {
        return (IconActionBuilder) super.addRefreshGuiAction();
    }

    @Override
    public IconActionBuilder addSoundAction(final Sound sound, final float volume, final float pitch) {
        return (IconActionBuilder) super.addSoundAction(sound, volume, pitch);
    }
}
