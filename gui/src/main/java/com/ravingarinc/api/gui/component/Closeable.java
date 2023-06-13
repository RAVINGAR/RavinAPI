package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Active;
import com.ravingarinc.api.gui.api.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Closeable implements Component, Active {
    private final String parent;
    private final List<Consumer<BaseGui>> consumers = new ArrayList<>();

    public Closeable(final String parent) {
        this.parent = parent;
    }

    public void addConsumer(Consumer<BaseGui> consumer) {
        consumers.add(consumer);
    }

    @Override
    public String getIdentifier() {
        return "CLOSEABLE";
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {

    }

    @Override
    public Class<Closeable> getThisClass() {
        return Closeable.class;
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 0;
    }

    @Override
    public void shutdown(final BaseGui gui) {
        consumers.forEach(consumer -> consumer.accept(gui));
    }
}
