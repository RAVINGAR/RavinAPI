package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.builder.GuiProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

public class Dynamic implements Component {
    //todo refactor this whole component to be more efficient
    /*
     * Represents a GUI icon in which it's state / appearance can be altered by another GUI icon
     */
    private String identifier;
    private Interactive parent;
    private int minAmount;
    private int maxAmount;
    private ItemStack currentItem;
    private Component grandparent;
    private String parentString;

    public Dynamic(final String parent, final Component grandparent, final int min, final int max) {
        this.parentString = parent;
        this.grandparent = grandparent;
        this.parent = null;
        this.identifier = parentString + "_DYNAMIC";
        minAmount = min;
        maxAmount = max;
    }

    public Dynamic(final String parent, final Component grandparent) {
        //We handle it this way so that Dynamic objects are only initialised if parent is null after the fact
        //Basically lazy initialisation
        this(parent, grandparent, -1, -1);
    }

    private void setInvalidParams() {
        this.parentString = null;
        this.grandparent = null;
        this.parent = null;
        this.identifier = null;
        minAmount = 0;
        maxAmount = 0;
    }

    private void init() {
        if (parent == null) {
            final Optional<Interactive> parent = grandparent.findComponent(Component.INTERACTIVE, parentString);
            if (parent.isPresent()) {
                this.parent = parent.get();
            } else {
                I.log(Level.SEVERE, "Dynamic component was NOT child of Interactive type!");
                setInvalidParams();

            }
        }
    }

    //Note to self : This logic will only work if Dynamic is the ONLY thing that can update the icon.

    public void changeItemAmount(final int amount) {
        init();

        if (currentItem == null) {
            currentItem = parent.getItem();
            if (currentItem == null) {
                return;
            }
        }
        final int currentAmount = currentItem.getAmount();
        if (minAmount == -1 || maxAmount == -1) {
            if (currentAmount + amount <= 0) {
                currentItem.setType(Material.AIR);
            } else {
                currentItem.setAmount(currentAmount + amount);
            }
        } else {
            if (currentAmount + amount >= minAmount && currentAmount + amount <= maxAmount) {
                currentItem.setAmount(currentAmount + amount);
            }
        }

        parent.setItem(currentItem);
        currentItem = null;
    }

    public void updateItem(final String name, final String lore, final Material material) {
        init();

        if (currentItem == null) {
            currentItem = parent.getItem();
            if (currentItem == null) {
                return;
            }
        }

        if (material != null) {
            currentItem.setType(material);
        }

        if (!currentItem.getType().isAir()) {
            final ItemMeta meta = currentItem.getItemMeta();

            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }
            if (lore != null) {
                meta.setLore(new ArrayList<>(Arrays.asList((ChatColor.translateAlternateColorCodes('&', lore)).split("\n"))));
            }

            currentItem.setItemMeta(meta);
        }

        parent.setItem(currentItem);
        currentItem = null;
    }

    public void consumeMeta(final Consumer<ItemMeta> consumer) {
        init();
        if (currentItem == null) {
            currentItem = parent.getItem();
            if (currentItem == null) {
                return;
            }
        }
        final ItemMeta meta = currentItem.getItemMeta();
        if (meta != null) {
            consumer.accept(meta);
            currentItem.setItemMeta(meta);
        }
        parent.setItem(currentItem);
        currentItem = null;
    }

    public <T, Z> void setMeta(final PersistentDataType<T, Z> type, final String key, final Z value) {
        init();
        if (currentItem == null) {
            currentItem = parent.getItem();
            if (currentItem == null) {
                return;
            }
        }
        final ItemMeta meta = currentItem.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(GuiProvider.getKey(key), type, value);
        currentItem.setItemMeta(meta);

        parent.setItem(currentItem);
        currentItem = null;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getParent() {
        init();
        return parent.getIdentifier();
    }

    @Override
    public void fillElement(final BaseGui gui) {
    }

    @Override
    public Class<Dynamic> getThisClass() {
        return Dynamic.class;
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 1;
    }
}
