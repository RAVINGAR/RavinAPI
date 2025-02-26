package com.ravingarinc.api.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class DelayedFutureTask<V> extends FutureTask<V> implements Delayed {
    private final long readyTime;

    public DelayedFutureTask(final Callable<V> callable, final long delay) {
        super(callable);
        this.readyTime = System.currentTimeMillis() + delay;
    }

    public DelayedFutureTask(final Runnable runnable, final V result, final long delay) {
        super(runnable, result);
        this.readyTime = System.currentTimeMillis() + delay;
    }

    @Override
    public long getDelay(@NotNull final TimeUnit unit) {
        return isCancelled() ? 0 : unit.convert(this.readyTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NotNull final Delayed other) {
        if (other instanceof DelayedFutureTask) {
            final var that = (DelayedFutureTask<?>) other;
            return (int) ((this.isCancelled() ? System.currentTimeMillis() : this.readyTime) - (that.isCancelled() ? System.currentTimeMillis() : that.readyTime));
        }
        throw new IllegalArgumentException("Cannot compare DelayedEvent to generic Delayed object!");
    }
}
