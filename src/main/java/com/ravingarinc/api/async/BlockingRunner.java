package com.ravingarinc.api.async;

import com.ravingarinc.api.I;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Blocking;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
     * Queues the cancellation for this task and blocks until that task is executed.
     *
     * @param function The function to create a task for this given type
     */
    @Blocking
    public synchronized void blockUntilCancelled(final Function<Runnable, T> function) {
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

    @Blocking
    public synchronized void cancelNow(final Function<Runnable, T> function) {
        final T task = function.apply(() -> {
            cancelled.setRelease(true);
            if (!isCancelled()) {
                cancel();
            }
        });
        getRemaining().forEach(t -> t.cancel(false));
        this.queue.add(task);
        try {
            task.get(1000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            I.log(Level.SEVERE, "Encountered exception waiting for BlockingRunner to cancel!", e);
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        this.cancel(false);
    }

    public synchronized void cancel(final boolean mayInterruptIfRunning) throws IllegalStateException {
        super.cancel();
        getRemaining().forEach(task -> task.cancel(mayInterruptIfRunning));
    }
}