package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Active;
import com.ravingarinc.api.gui.api.TriConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Only one of these should ever exist at a time
 */
public class ChatInputComponent implements Active {
    private final ChatListener listener;

    private final Map<Player, InputEntry> listening;

    public ChatInputComponent() {
        this.listening = new ConcurrentHashMap<>();
        this.listener = new ChatListener();
    }

    public void addListener(final BaseGui gui, final Player player, final TriConsumer<BaseGui, Player, String> consumer) {
        this.listener.register(gui.getPlugin());
        this.listening.put(player, new InputEntry(gui, consumer));
    }

    @Override
    public void shutdown(BaseGui gui) {
        listener.unregister();
    }

    private class ChatListener implements Listener {
        private boolean isRegistered = false;
        private Plugin plugin;

        public void register(final Plugin plugin) {
            if (!isRegistered) {
                this.plugin = plugin;
                plugin.getServer().getPluginManager().registerEvents(this, plugin);
                isRegistered = true;
            }
        }

        public void unregister() {
            if (isRegistered) {
                HandlerList.unregisterAll(this);
                isRegistered = false;
            }
        }

        @EventHandler
        public void onChatEvent(final AsyncPlayerChatEvent event) {
            final var player = event.getPlayer();
            final var entry = listening.remove(player);
            if (entry == null) {
                return;
            }
            event.setCancelled(true);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                entry.consumer.accept(entry.gui, player, event.getMessage());
            });
        }
    }

    private class InputEntry {
        private final BaseGui gui;
        private final TriConsumer<BaseGui, Player, String> consumer;

        public InputEntry(final BaseGui gui, final TriConsumer<BaseGui, Player, String> consumer) {
            this.gui = gui;
            this.consumer = consumer;
        }
    }
}
