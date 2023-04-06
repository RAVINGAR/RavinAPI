package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Element;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.component.Menu;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public abstract class BaseIcon extends Element implements Interactive {
    protected List<Action> actions;
    protected ItemStack item;
    protected Predicate<BaseGui> predicate;

    public BaseIcon(final String identifier, final String display, final String lore, final String parent, final Material material, final Predicate<BaseGui> predicate, final Consumer<ItemStack> consumer) {
        super(identifier, parent, 3);
        if (material.isAir()) {
            throw new IllegalArgumentException("\nIcon material cannot be air!");
        }
        this.item = new ItemStack(material, 1);

        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(display == null ? ChatColor.DARK_GRAY + "" : ChatColor.translateAlternateColorCodes('&', display));
            if (lore != null && !lore.equals("")) {
                meta.setLore(new ArrayList<>(Arrays.asList(ChatColor.translateAlternateColorCodes('&', lore).split("\n"))));
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            setMeta(PersistentDataType.STRING, "identifier", this.getIdentifier());
        }
        consumer.accept(item);

        this.actions = new LinkedList<>();
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
    public void addAmount(final int delta) {
        final int amount = item.getAmount() + delta;
        if (amount > 0 && amount < 65) {
            item.setAmount(amount);
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
