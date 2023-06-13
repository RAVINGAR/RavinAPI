package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Actionable;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.Closeable;
import com.ravingarinc.api.gui.component.Menu;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class GuiBuilder<T extends BaseGui> {
    protected T gui;

    protected Material border1;
    protected Material border2;

    protected int defaultBackIdx;
    protected List<MenuBuilder> menusToAdd;

    protected ComponentActionBuilder<T> lastActionBuilder;

    protected QueueableActionBuilder<T> queueableActionBuilder;

    protected @Nullable Closeable onDestroy = null;


    public GuiBuilder(final Plugin plugin, final String guiName, final Class<T> type) {
        this(plugin, guiName, type, 45);
    }

    public GuiBuilder(final Plugin plugin, final String guiName, final Class<T> type, final Integer inventorySize) {
        try {
            final Constructor<T> constructor = type.getConstructor(Plugin.class, String.class, Integer.class);
            this.gui = constructor.newInstance(plugin, guiName, inventorySize);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            GuiProvider.log(Level.SEVERE, "Something went wrong finding constructor for BaseGui!" + e);
        } catch (final InvocationTargetException e) {
            GuiProvider.log(Level.SEVERE, "Gui constructor threw exception!", e.getTargetException());
        }
        init();
    }

    private void init() {
        border1 = Material.AIR;
        border2 = Material.AIR;
        defaultBackIdx = -1;
        menusToAdd = new LinkedList<>();

        if (gui == null) {
            throw new IllegalArgumentException("Could not create GuiBuilder due to gui being null!");
        }
    }

    @NotNull
    public String identifier() {
        return gui.getIdentifier();
    }

    @NotNull
    public Material getPrimaryBorder() {
        return border1;
    }

    public GuiBuilder<T> setPrimaryBorder(@NotNull final Material material) {
        border1 = material;
        return this;
    }

    public QueueableActionBuilder<T> addQueueableOnClose(final boolean persistent) {
        handleLastQueueable();
        queueableActionBuilder = new QueueableActionBuilder<>(this, persistent, "MAIN");
        return queueableActionBuilder;
    }

    public void handleLastQueueable() {
        if (queueableActionBuilder != null) {
            queueableActionBuilder.build();
            queueableActionBuilder = null;
        }
    }

    public void setBackIconIndex(final int idx) {
        this.defaultBackIdx = idx;
    }

    @NotNull
    public Material getSecondaryBorder() {
        return border2;
    }

    public GuiBuilder<T> setSecondaryBorder(@NotNull final Material material) {
        border2 = material;
        return this;
    }

    public ComponentActionBuilder<T> addActionableComponent(final Actionable actionable) {
        handleLastActionBuilder();
        lastActionBuilder = new ComponentActionBuilder<>(this, actionable, "MAIN");
        return lastActionBuilder;
    }

    public MenuBuilder createMenu(final String identifier, final String parent) {
        return createMenu(identifier, parent, defaultBackIdx);
    }

    public MenuBuilder createMenu(final String identifier, String parent, final int backIdx) {
        if (identifier.equalsIgnoreCase("main")) {
            parent = gui.getIdentifier(); //Main menu
        }
        final MenuBuilder builder = new MenuBuilder(this, identifier, parent, backIdx);
        menusToAdd.add(builder);
        return builder;
    }

    public GuiBuilder<T> addMiscComponent(final Supplier<Component> component) {
        gui.addChild(component);
        return this;
    }

    /**
     * The provided consumer is added to a closeable component
     * When this GUI is destroyed (via player logging out or plugin reload), all added consumers are executed in the
     * order that they were added.
     */
    public GuiBuilder<T> runOnDestroy(final Consumer<BaseGui> consumer) {
        if (onDestroy == null) {
            onDestroy = new Closeable(gui.getIdentifier());
            gui.addChild(() -> onDestroy);
        }
        onDestroy.addConsumer(consumer);
        return this;
    }

    protected void handleLastActionBuilder() {
        if (lastActionBuilder != null) {
            lastActionBuilder.build();
            lastActionBuilder = null;
        }
    }

    public T get() {
        return gui;
    }

    public T build() {
        handleLastActionBuilder();
        handleLastQueueable();

        boolean mainExists = false;
        for (final MenuBuilder builder : menusToAdd) {
            final Menu menu = builder.get();
            gui.addChild(() -> menu);
            if (menu.getIdentifier().equalsIgnoreCase("MAIN")) {
                mainExists = true;
            }
        }
        if (!mainExists) {
            GuiProvider.log(Level.SEVERE, "Main Menu was never added for GuiBuilder for " + gui.getIdentifier() + "! This is a developer error!");
        }
        return gui;
    }
}
