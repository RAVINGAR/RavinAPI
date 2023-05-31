package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.TriConsumer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class InputComponent implements Component {
    private final ChatListener listener;
    private final String identifier;
    private final String parent;
    private final String description;
    private final TriConsumer<BaseGui, Player, String> onResponse;
    private final AtomicReference<Player> listening;

    private final AtomicReference<String> response;

    private BaseGui lastGui;

    public InputComponent(final String identifier, final String parent, final String description, final TriConsumer<BaseGui, Player, String> onResponse) {
        this.identifier = identifier;
        this.listening = new AtomicReference<>(null);
        this.response = new AtomicReference<>(null);
        this.description = ChatColor.translateAlternateColorCodes('&', description);
        this.parent = parent;
        this.onResponse = onResponse;
        this.listener = new ChatListener();
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {
        //todo refactor this to work with multiple players (put things in a list instead!)
        final String message = response.getAcquire();
        if (message != null && player.equals(listening.getAcquire())) {
            listener.unregister();
            listening.setRelease(null);
            response.setRelease(null);
            onResponse.accept(gui, player, message);
        }
    }

    public void getResponse(final BaseGui gui, final Player player) {
        player.closeInventory();
        lastGui = gui;
        for (final String line : description.split("\n")) {
            player.sendMessage(line);
        }
        listening.setRelease(player);
        listener.register(gui.getPlugin());
    }

    @Override
    public @NotNull Integer getPriority() {
        return 0;
    }

    @Override
    public Class<InputComponent> getThisClass() {
        return InputComponent.class;
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
            if (event.getPlayer().equals(listening.getAcquire())) {
                response.setRelease(event.getMessage());
                final Player player = event.getPlayer();
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> player.openInventory(lastGui.getInventory()));
            }
        }
    }
}
