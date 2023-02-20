package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

/*
A submodule of a component which will update its parent if a predicate is true
 */
public class Observer implements Component, Actionable {
    private final List<Action> actions;
    private final Interactive parent;
    private final String identifier;
    private final Predicate<ItemStack> condition;
    private final Supplier<Boolean> supplier;

    /**
     * An observer's actions will be called after it's parent as an item returns true for the predicate. It's actions in
     * most cases will point towards its parent
     *
     * @param parent
     * @param condition
     */
    public Observer(final Interactive parent, final Predicate<ItemStack> condition) {
        this.identifier = (parent == null ? "?" : parent.getIdentifier()) + "_OBSERVER";
        this.parent = parent;
        this.condition = condition;
        supplier = null;
        actions = new LinkedList<>();
    }

    public Observer(final Component parent, final Supplier<Boolean> condition) {
        this.identifier = parent.getIdentifier() + "_OBSERVER";
        this.parent = parent instanceof Interactive ? (Interactive) parent : null;

        if (this.parent == null) {
            I.log(Level.SEVERE, "Observer component was NOT child of Icon type!");
        }
        this.supplier = condition;
        this.condition = null;
        actions = new LinkedList<>();
    }

    @Override
    public void addAction(final Action action) {
        if (action != null) {
            actions.add(action);
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getParent() {
        return parent.getIdentifier();
    }

    @Override
    public void fillElement(final BaseGui gui) {
        if ((condition != null && condition.test(this.parent.getItem())) || (supplier != null && supplier.get())) {
            performAllActions(gui);
        }
    }

    @Override
    public Class<Observer> getThisClass() {
        return Observer.class;
    }

    @Override
    public void performAllActions(final BaseGui gui) {
        actions.forEach(action -> {
            action.performAction(gui);
        });
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 2;
    }
}
