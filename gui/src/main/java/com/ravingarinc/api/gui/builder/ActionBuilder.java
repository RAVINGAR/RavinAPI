package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.api.TriConsumer;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.action.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ActionBuilder<P> {
    protected final Actionable reference;
    protected final List<Action> actionsToAdd = new LinkedList<>();
    protected final String lastMenu;


    public ActionBuilder(final Actionable reference, final String lastMenu) {
        this.reference = reference;
        this.lastMenu = lastMenu;
    }

    public ActionBuilder<P> addChatInputAction(final String description, BiConsumer<Player, String> onResponse) {
        actionsToAdd.add(new ChatInputAction(description, onResponse));
        return this;
    }

    public ActionBuilder<P> addChatInputAction(final String description, TriConsumer<BaseGui, Player, String> onResponse) {
        actionsToAdd.add(new ChatInputAction(description, onResponse));
        return this;
    }

    public ActionBuilder<P> addActivateDecorationAction(final String pointer, final Decoration.Pattern pattern, final Material updateMaterial, final Supplier<Long> duration) {
        actionsToAdd.add(new ActivateDecorationAction(pointer, lastMenu, pattern, updateMaterial, duration));
        return this;
    }

    public ActionBuilder<P> addChangeAmountAction(final String pointer, final int increment) {
        actionsToAdd.add(new ChangeAmountAction(pointer, increment));
        return this;
    }

    public ActionBuilder<P> addSoundAction(final Sound sound, final float volume, final float pitch) {
        actionsToAdd.add(new SoundAction(sound, volume, pitch));
        return this;
    }

    public ActionBuilder<P> addLockPlaceableAction(final String pointer, final boolean lock) {
        actionsToAdd.add(new LockPlaceableAction(pointer, lastMenu, lock));
        return this;
    }

    public ActionBuilder<P> addMenuAction(final String pointer) {
        actionsToAdd.add(new MenuAction(pointer));
        return this;
    }

    public ActionBuilder<P> addUpdateComponentAction(final String pointer, final Supplier<String> name, final Supplier<String> lore, final Material material) {
        return addUpdateComponentAction(pointer, lastMenu, name, lore, material);
    }

    public ActionBuilder<P> addUpdateComponentAction(final String pointer, final String menu, final Supplier<String> name, final Supplier<String> lore, final Material material) {
        return addUpdateComponentAction(pointer, menu, (g) -> name.get(), (g) -> lore.get(), material);
    }

    public ActionBuilder<P> addUpdateComponentAction(final String pointer, final Function<BaseGui, String> name, final Function<BaseGui, String> lore, final Material material) {
        return addUpdateComponentAction(pointer, lastMenu, name, lore, material);
    }

    public ActionBuilder<P> addUpdateComponentAction(final String pointer, final String menu, final Function<BaseGui, String> name, final Function<BaseGui, String> lore, final Material material) {
        actionsToAdd.add(new UpdateComponentAction(pointer, menu, name, lore, material));
        return this;
    }

    public <T, Z> ActionBuilder<P> addUpdateMetaAction(final String pointer, final String menu, final PersistentDataType<T, Z> type, final String key, final Z value) {
        actionsToAdd.add(new UpdateMetaAction<>(pointer, menu, type, key, value));
        return this;
    }

    public ActionBuilder<P> addRefreshGuiAction() {
        actionsToAdd.add(new RefreshGuiAction());
        return this;
    }

    public ActionBuilder<P> addConsumeMetaAction(final String pointer, final Consumer<ItemMeta> consumer) {
        actionsToAdd.add(new ConsumeMetaAction(pointer, lastMenu, consumer));
        return this;
    }

    public ActionBuilder<P> addSetPageAction(final int page) {
        actionsToAdd.add(new SetPageAction(page));
        return this;
    }

    public ActionBuilder<P> addMiscAction(final Action action) {
        actionsToAdd.add(action);
        return this;
    }

    public ActionBuilder<P> addRunnableAction(final Consumer<Player> consumer) {
        return addRunnableAction((gui, player) -> consumer.accept(player));
    }

    public ActionBuilder<P> addRunnableAction(final BiConsumer<BaseGui, Player> consumer) {
        actionsToAdd.add(new RunnableAction(consumer));
        return this;
    }

    public ActionBuilder<P> addRunnableAction(final Runnable runnable) {
        actionsToAdd.add(new RunnableAction(runnable));
        return this;
    }

    /**
     * Finalises this builder and returns the parent
     *
     * @return The parent of type P
     */
    @Deprecated()
    public abstract P finalise();

    public void build() {
        if (actionsToAdd.isEmpty()) {
            return;
        }
        Collections.sort(actionsToAdd);
        actionsToAdd.forEach(reference::addAction);
        actionsToAdd.clear();
    }
}
