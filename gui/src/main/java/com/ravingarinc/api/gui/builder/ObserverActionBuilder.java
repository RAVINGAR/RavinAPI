package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.icon.Observer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ObserverActionBuilder extends BaseActionBuilder {
    private final IconBuilder<?, ?> parent;

    public ObserverActionBuilder(final Interactive icon, final Predicate<ItemStack> predicate, final IconBuilder<?, ?> parent) {
        super(new Observer(icon, predicate), icon.getParent());
        this.parent = parent;
    }

    public ObserverActionBuilder(final Interactive icon, final Supplier<Boolean> condition, final IconBuilder<?, ?> parent) {
        super(new Observer(icon, condition), icon.getParent());
        this.parent = parent;
    }

    public IconBuilder<?, ?> finalise() {
        parent.handleActionBuilder(this);
        return parent;
    }

    @Override
    public void build() {
        parent.addChild(() -> (Component) getActionable());
    }

    @Override
    public ObserverActionBuilder addActivateDecorationAction(final String pointer, final Decoration.Pattern pattern, final Material updateMaterial, final Supplier<Long> duration) {
        return (ObserverActionBuilder) super.addActivateDecorationAction(pointer, pattern, updateMaterial, duration);
    }

    @Override
    public ObserverActionBuilder addChangeAmountAction(final String pointer, final int increment) {
        return (ObserverActionBuilder) super.addChangeAmountAction(pointer, increment);
    }

    @Override
    public ObserverActionBuilder addLockPlaceableAction(final String pointer, final boolean lock) {
        return (ObserverActionBuilder) super.addLockPlaceableAction(pointer, lock);
    }

    @Override
    public ObserverActionBuilder addMenuAction(final String pointer) {
        return (ObserverActionBuilder) super.addMenuAction(pointer);
    }

    @Override
    public ObserverActionBuilder addUpdateComponentAction(final String pointer, @NotNull final Supplier<String> name, @NotNull final Supplier<String> lore, final Material material) {
        return (ObserverActionBuilder) super.addUpdateComponentAction(pointer, name, lore, material);
    }

    @Override
    public <T, Z> ObserverActionBuilder addUpdateMetaAction(final String pointer, final String menu, final PersistentDataType<T, Z> type, final String key, final Z value) {
        return (ObserverActionBuilder) super.addUpdateMetaAction(pointer, menu, type, key, value);
    }

    @Override
    public ObserverActionBuilder addRefreshGuiAction() {
        return (ObserverActionBuilder) super.addRefreshGuiAction();
    }

    @Override
    public ObserverActionBuilder addSoundAction(final Sound sound, final float volume, final float pitch) {
        return (ObserverActionBuilder) super.addSoundAction(sound, volume, pitch);
    }

    @Override
    public ObserverActionBuilder addConsumeMetaAction(final String pointer, final Consumer<ItemMeta> consumer) {
        return (ObserverActionBuilder) super.addConsumeMetaAction(pointer, consumer);
    }

    @Override
    public ObserverActionBuilder addMiscAction(final Action action) {
        return (ObserverActionBuilder) super.addMiscAction(action);
    }
}
