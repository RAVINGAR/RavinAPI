package com.ravingarinc.api.gui.component.observer;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ItemUpdater implements Component {
    private final Interactive parent;

    private BiFunction<Interactive, Player, String> displayNameProvider = null;
    private BiFunction<Interactive, Player, String> loreProvider = null;
    private BiFunction<Interactive, Player, Material> materialProvider = null;

    private BiFunction<Interactive, Player, Consumer<ItemMeta>> metaProvider = null;

    public ItemUpdater(Interactive parent) {
        this.parent = parent;
    }

    @Override
    public String getIdentifier() {
        return parent.getIdentifier() + "_UPDATER";
    }

    @Override
    public String getParent() {
        return parent.getIdentifier();
    }

    public void setDisplayNameProvider(final BiFunction<Interactive, Player, String> provider) {
        displayNameProvider = provider;
    }

    public void setLoreProvider(final BiFunction<Interactive, Player, String> provider) {
        loreProvider = provider;
    }

    public void setMaterialProvider(final BiFunction<Interactive, Player, Material> provider) {
        materialProvider = provider;
    }

    public void setMetaProvider(final BiFunction<Interactive, Player, Consumer<ItemMeta>> provider) {
        metaProvider = provider;
    }

    @Override
    public void fillElement(BaseGui gui, Player player) {
        String name = displayNameProvider == null ? null : displayNameProvider.apply(parent, player);
        String lore = loreProvider == null ? null : loreProvider.apply(parent, player);
        Material material = materialProvider == null ? null : materialProvider.apply(parent, player);
        parent.updateItem(name, lore, material);

        Consumer<ItemMeta> meta = metaProvider == null ? null : metaProvider.apply(parent, player);
        if (meta != null) {
            parent.updateMeta(meta);
        }
    }

    @Override
    public @NotNull Integer getPriority() {
        return 2;
    }

    @Override
    public Class<ItemUpdater> getThisClass() {
        return ItemUpdater.class;
    }
}
