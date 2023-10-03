package com.ravingarinc.api.concurrent.key;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class KeyedRunnable<K> extends BukkitRunnable implements Keyed<K> {
    private final K identifier;

    public KeyedRunnable(final K identifier) {
        this.identifier = identifier;
    }

    @Override
    public K getKey() {
        return identifier;
    }
}
