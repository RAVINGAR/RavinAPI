package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Active;
import com.ravingarinc.api.gui.api.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Functionally how this works. A GUI is only built once per player. However it still can be cleared
 * What this component does in interact directly with the ItemBuilder component. When a player walks x
 * amount of blocks away from the crafting station, the items within the station are placed on the top of it
 * TODO
 */
public class Closeable implements Component, Active {
    private final String parent;
    private Location origin;

    public Closeable(final String parent, final Location origin) {
        this.parent = parent;
        this.origin = origin;
    }

    public void updateOrigin(final Location location) {
        this.origin = location;
    }

    public Location getOrigin() {
        return origin;
    }

    @Override
    public String getIdentifier() {
        return "CLOSEABLE";
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {

    }

    @Override
    public Class<Closeable> getThisClass() {
        return null;
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 0;
    }

    @Override
    public void shutdown(final BaseGui gui) {

    }
}
