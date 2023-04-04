package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class InputComponent implements Component {
    private final ChatListener listener;
    private final String identifier;
    private final String parent;
    private final String description;
    private final BiConsumer<BaseGui, String> onResponse;
    private final AtomicReference<Player> listening;

    private final AtomicReference<String> response;

    private BaseGui lastGui;

    public InputComponent(final String identifier, final String parent, final String description, final BiConsumer<BaseGui, String> onResponse) {
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
    public void fillElement(final BaseGui gui) {
        final String message = response.getAcquire();
        if (message != null && gui.getPlayer().equals(listening.getAcquire())) {
            listener.unregister();
            listening.setRelease(null);
            response.setRelease(null);
            onResponse.accept(gui, message);
        }
    }

    public void getResponse(final BaseGui gui) {
        final Player player = gui.getPlayer();
        lastGui = gui;
        gui.closeGui();
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

        public void register(final JavaPlugin plugin) {
            if (!isRegistered) {
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
                lastGui.openGui();
            }
        }
    }
}
