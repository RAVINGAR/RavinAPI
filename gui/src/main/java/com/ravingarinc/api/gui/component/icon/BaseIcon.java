package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Element;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.builder.GuiProvider;
import com.ravingarinc.api.gui.component.Menu;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public abstract class BaseIcon extends Element implements Interactive {
    protected List<Action> actions;
    protected ItemStack item;
    protected Predicate<BaseGui> predicate;

    public BaseIcon(final String identifier, final String display, final String lore, final String parent, final Material material, final Action action, final Predicate<BaseGui> predicate, final Consumer<ItemStack> consumer) {
        super(identifier, parent, 3);

        init(display, lore, material, action, predicate, consumer);
    }


    private void init(final String display, final String lore, final Material material, final Action action, final Predicate<BaseGui> predicate, final Consumer<ItemStack> consumer) {
        if (material.isAir()) {
            throw new IllegalArgumentException("\nIcon material cannot be air!");
        }
        this.item = new ItemStack(material, 1);

        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (display != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', display));
            }
            if (lore != null && !lore.equals("")) {
                meta.setLore(new ArrayList<>(Arrays.asList(ChatColor.translateAlternateColorCodes('&', lore).split("\n"))));
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            setMeta(PersistentDataType.STRING, "identifier", this.getIdentifier());
        }

        consumer.accept(item);

        actions = new LinkedList<>();
        if (action != null) {
            actions.add(action);
        }

        this.predicate = predicate;
    }

    @Override
    public void addAction(final Action action) {
        if (action != null) {
            actions.add(action);
        }
    }

    @Override
    public void performAllActions(final BaseGui gui) {
        actions.forEach(action -> action.performAction(gui));
    }

    public <T, Z> void setMeta(final PersistentDataType<T, Z> type, final String key, final Z value) {
        if (item == null) {
            return;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(GuiProvider.getKey(key), type, value);
        item.setItemMeta(meta);
    }

    public <T, Z> Z getMeta(final PersistentDataType<T, Z> type, final String key) {
        if (item == null) {
            return null;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(GuiProvider.getKey(key), type);
    }

    public <T, Z> boolean hasMeta(final PersistentDataType<T, Z> type, final String key) {
        return getMeta(type, key) != null;
    }

    @Override
    public boolean handleClickedItem(final BaseGui gui, final InventoryClickEvent event) {
        performAllActions(gui);
        return true;
    }

    public boolean canDisplay(final BaseGui gui) {
        return predicate.test(gui);
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public void setItem(final ItemStack item) {
        if (item == null || item.getType().isAir()) {
            this.item.setType(Material.AIR);
            this.item = null;
        } else {
            this.item.setType(item.getType());
            this.item.setAmount(item.getAmount());
            this.item.setItemMeta(item.getItemMeta());
            this.item.setData(item.getData());
        }
    }

    @Override
    public void fillElement(final BaseGui gui) {
        super.fillElement(gui);
        fillIcon(gui);
    }

    protected void fillIcon(final BaseGui gui) {
        final Optional<Menu> parent = gui.findComponent(Component.MENU, this.parent);
        parent.ifPresentOrElse(menu -> {
            if (BaseIcon.this.canDisplay(gui)) {
                menu.queueIconToPlace(this.item);
            }
        }, () -> I.log(Level.SEVERE, "Parent of icon was not menu!"));
        //This could be replaced with getCurrentMenu, but this is safer as it should only fillElement based on the icon's parent
    }
}
