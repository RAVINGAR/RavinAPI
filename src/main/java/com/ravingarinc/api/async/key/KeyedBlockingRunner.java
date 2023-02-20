package com.ravingarinc.api.async.key;

import com.ravingarinc.api.async.BlockingRunner;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

public class KeyedBlockingRunner<K, T extends Future<?> & Runnable & Keyed<K>> extends BlockingRunner<T> {

    public KeyedBlockingRunner(final BlockingQueue<T> queue) {
        super(queue);
    }

    public void cancelFor(final K key) {
        queue.stream().filter(task -> task.getKey().equals(key)).forEach(t -> t.cancel(false));
    }
}
