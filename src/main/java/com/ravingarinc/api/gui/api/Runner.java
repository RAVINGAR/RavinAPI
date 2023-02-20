package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.async.key.KeyedBlockingRunner;
import com.ravingarinc.api.async.key.KeyedDelayedFutureTask;
import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Future;

/**
 * Singleton gui component. Only one of these should ever be created in the Component
 */
public class Runner implements Component, Active {
    private final KeyedBlockingRunner<BaseGui, KeyedDelayedFutureTask<BaseGui, Void>> runner;
    private boolean initialised = false;
    private JavaPlugin plugin = null;
    private BukkitScheduler scheduler = null;

    protected Runner() {
        this.runner = new KeyedBlockingRunner<>(new DelayQueue<>());
    }

    public void init(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
        this.runner.runTaskAsynchronously(plugin);
        initialised = true;
    }

    public Future<Void> queue(final BaseGui gui, final Runnable runnable, final long ticks) {
        final KeyedDelayedFutureTask<BaseGui, Void> task = new KeyedDelayedFutureTask<>(gui, () -> scheduler.runTask(plugin, runnable), null, ticks * 50L);
        runner.queue(task);
        return task;
    }

    public Future<Void> queue(final BaseGui gui, final Runnable runnable, final Runnable onCancel, final long ticks) {
        final KeyedDelayedFutureTask<BaseGui, Void> task = new KeyedDelayedFutureTask<>(gui, () -> scheduler.runTask(plugin, runnable), null, ticks * 50L) {
            @Override
            public boolean cancel(final boolean mayInterruptIfRunning) {
                scheduler.runTask(plugin, onCancel);
                return super.cancel(mayInterruptIfRunning);
            }
        };
        runner.queue(task);
        return task;
    }

    public void cancelFor(final BaseGui gui) {
        if (initialised) {
            runner.cancelFor(gui);
        }
    }

    @Override
    @Blocking
    public void shutdown() {
        if (initialised) {
            runner.cancelNow((runnable) -> new KeyedDelayedFutureTask<>(null, runnable, null, 0));
        }
    }

    @Override
    public String getIdentifier() {
        return "SINGLETON_QUEUER";
    }

    @Override
    public String getParent() {
        return null;
    }

    @Override
    public void fillElement(final BaseGui gui) {
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 0;
    }

    @Override
    public Class<Runner> getThisClass() {
        return Runner.class;
    }
}
