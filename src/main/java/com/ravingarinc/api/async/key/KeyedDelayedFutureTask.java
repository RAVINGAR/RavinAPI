package com.ravingarinc.api.async.key;

import com.ravingarinc.api.async.DelayedFutureTask;

import java.util.concurrent.Callable;

public class KeyedDelayedFutureTask<K, V> extends DelayedFutureTask<V> implements Keyed<K> {
    private final K key;

    public KeyedDelayedFutureTask(final K key, final Callable<V> callable, final long delay) {
        super(callable, delay);
        this.key = key;
    }

    public KeyedDelayedFutureTask(final K key, final Runnable runnable, final V result, final long delay) {
        super(runnable, result, delay);
        this.key = key;
    }

    @Override
    public K getKey() {
        return key;
    }
}
