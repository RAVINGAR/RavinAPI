package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Element;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.builder.GuiProvider;
import com.ravingarinc.api.gui.component.Menu;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class BaseIcon extends Element implements Interactive {
    protected List<Action> actions;
    protected List<Action> shiftActions;
    protected ItemStack item;
    protected BiPredicate<BaseGui, Player> predicate;

    public BaseIcon(final String identifier, final String display, final String lore, final String parent, final Material material, final BiPredicate<BaseGui, Player> predicate, final Consumer<ItemStack> consumer) {
        super(identifier, parent, 3);
        if (material.isAir()) {
            throw new IllegalArgumentException("\nIcon material cannot be air!");
        }
        this.item = new ItemStack(material, 1);
        consumer.accept(item);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(formatString(display));
            if (lore != null && !lore.isEmpty()) {
                meta.lore(formatLore(lore));
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            setMeta(PersistentDataType.STRING, "identifier", this.getIdentifier());
        }

        this.shiftActions = new LinkedList<>();
        this.actions = new LinkedList<>();
        this.predicate = predicate;
    }

    public void setPredicate(BiPredicate<BaseGui, Player> predicate) {
        this.predicate = predicate;
    }

    @Override
    public void addShiftAction(Action action) {
        if (action != null) actions.add(action);
    }

    @Override
    public void performAllShiftActions(BaseGui gui, Player player) {
        shiftActions.forEach(action -> action.performAction(gui, player));
    }

    @Override
    public void addAction(final Action action) {
        if (action != null) {
            actions.add(action);
        }
    }

    @Override
    public void performAllActions(final BaseGui gui, Player player) {
        actions.forEach(action -> action.performAction(gui, player));
    }

    @Override
    public boolean handleClickedItem(final BaseGui gui, final InventoryClickEvent event, Player player) {
        if (event.isShiftClick()) {
            performAllShiftActions(gui, player);
        } else {
            performAllActions(gui, player);
        }
        return true;
    }

    public boolean canDisplay(final BaseGui gui, final Player player) {
        return predicate.test(gui, player);
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
    public void setAmount(int amount) {
        if (amount > 0 && amount < 65) {
            item.setAmount(amount);
        }
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {
        super.fillElement(gui, player);
        fillIcon(gui, player);
    }

    protected void fillIcon(final BaseGui gui, Player player) {
        final Optional<Menu> parent = gui.findComponent(Component.MENU, this.parent);
        parent.ifPresentOrElse(menu -> {
            if (BaseIcon.this.canDisplay(gui, player)) {
                menu.queueIconToPlace(this.item);
            }
        }, () -> GuiProvider.log(Level.SEVERE, "Parent of icon was not menu!"));
        //This could be replaced with getCurrentMenu, but this is safer since it should only fillElement based on the icon's parent
    }
}
