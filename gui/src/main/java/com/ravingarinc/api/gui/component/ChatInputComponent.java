package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Active;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Only one of these should ever exist at a time
 */
public class ChatInputComponent implements Active {
    private final ChatListener listener;

    private final Map<Player, BiConsumer<Player, String>> listening;

    public ChatInputComponent() {
        this.listening = new ConcurrentHashMap<>();
        this.listener = new ChatListener();
    }

    public void addListener(final BaseGui gui, final Player player, final BiConsumer<Player, String> consumer) {
        this.listener.register(gui.getPlugin());
        this.listening.put(player, consumer);
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
            final var consumer = listening.remove(player);
            if (consumer == null) {
                return;
            }
            event.setCancelled(true);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                consumer.accept(player, event.getMessage());
            });
        }
    }
}
