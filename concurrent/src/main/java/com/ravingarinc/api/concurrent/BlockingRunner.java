package com.ravingarinc.api.concurrent;

import com.ravingarinc.api.I;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Blocking;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Level;

public class BlockingRunner<T extends Future<?> & Runnable> extends BukkitRunnable {
    protected final BlockingQueue<T> queue;

    protected final AtomicBoolean cancelled;

    public BlockingRunner(final BlockingQueue<T> queue) {
        this.queue = queue;
        this.cancelled = new AtomicBoolean(false);
    }

    public <V extends T> void queue(final V task) {
        if (!this.cancelled.getAcquire()) {
            this.queue.add(task);
        }
    }

    public Collection<T> getRemaining() {
        return new HashSet<>(this.queue);
    }

    public void queueAll(final Collection<T> collection) {
        if (!this.cancelled.getAcquire()) {
            this.queue.addAll(collection);
        }
    }

    @Override
    public void run() {
        while (!isCancelled() && !cancelled.getAcquire()) {
            try {
                queue.take().run();
            } catch (final InterruptedException ignored) {
            }
        }
    }

    /**
     * Cancels this blocking runner by queueing a cancel task at the end of the queue. All current tasks in the queue
     * must be executed before the runner is cancelled.
     *
     * @param function The function to create the cancel task.
     */
    @Blocking
    public void cancelAndWait(final Function<Runnable, T> function) {
        final T task = function.apply(() -> {
            cancelled.setRelease(true);
            if (!isCancelled()) {
                super.cancel();
            }
        });
        this.queue.add(task);
        try {
            task.get(1000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            I.log(Level.SEVERE, "Encountered exception waiting for BlockingRunner to cancel!", e);
        }
    }

    /**
     * Cancels all current tasks in this runner and then queues a final cancellation task. This method blocks until
     * the cancellation task is run.
     *
     * @param function The function to create the cancel task
     */
    @Blocking
    public void cancelNow(final Function<Runnable, T> function) {
        getRemaining().forEach(task -> task.cancel(false));
        cancelAndWait(function);
    }
}