package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Element;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.component.icon.StaticIcon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class Decoration extends Element {
    private final Material material;
    private final int[] index;

    public Decoration(final String identifier, final String parent) {
        super(identifier.endsWith("_DECORATION") ? identifier : identifier + "_DECORATION", parent, 3);
        material = null;
        index = null;
    }

    public Decoration(final String identifier, final String parent, final Material material, final int[] index) {
        super(identifier.endsWith("_DECORATION") ? identifier : identifier + "_DECORATION", parent, 3);
        this.material = material;
        this.index = index;
    }

    public void addDecor(final Material material, final int index) {
        final String identifier = "DECOR_" + index;
        addChild(() -> new StaticIcon(identifier, null, "", this.getIdentifier(), material, null, (g, p) -> true, i -> {
        }, index));
    }

    public void updateWithPattern(final Pattern type, final Material update, final long duration, final BaseGui gui, final Player player) {
        type.performPattern(this, update, gui, player, duration);
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {
        if (this.children.isEmpty()) {
            if (index != null && material != null) {
                for (final int i : index) {
                    addDecor(material, i);
                }
            }
        }
        super.fillElement(gui, player);
    }

    @Override
    public Class<Decoration> getThisClass() {
        return Decoration.class;
    }


    public enum Pattern {
        ASCENDING() {
            @Override
            protected void performPattern(final Decoration decoration, final Material update, final BaseGui gui, final Player player, final long duration) {
                scheduleIterativeTask(decoration.getChildren(), gui, player, update, 0, duration);
            }
        }, //Update each child incrementally
        INSTANT() {
            @Override
            protected void performPattern(final Decoration decoration, final Material update, final BaseGui gui, final Player player, final long delay) {
                scheduleInstantTask(decoration.getChildren(), gui, player, update, delay, 20, 20);
            }
        },
        EVEN() {
            @Override
            protected void performPattern(final Decoration decoration, final Material update, final BaseGui gui, final Player player, final long duration) {
                final List<Component> firstList = new LinkedList<>();
                final List<Component> secondList = new LinkedList<>();
                final Iterator<Component> iter = decoration.getChildren().iterator();
                int i = 0;
                while (iter.hasNext()) {
                    final Component c = iter.next();
                    if (i++ % 2 == 0) {
                        firstList.add(c);
                    } else {
                        secondList.add(c);
                    }
                }
                //Should alternate even colours technically or something like that
                scheduleInstantTask(firstList, gui, player, update, 0, 40, duration);
                scheduleInstantTask(secondList, gui, player, Material.WHITE_STAINED_GLASS_PANE, 20, 40, duration);
            }
        }; //Update every 2nd child in order. Will


        private static void scheduleIterativeTask(final List<Component> list, final BaseGui gui, final Player player, final Material update, final long delay, final long duration) {
            final Iterator<Component> iterator = list.iterator();
            new DecorationUpdateTask(iterator, gui, player, update).runTaskTimer(gui.getPlugin(), delay, duration / list.size());
        }

        private static void scheduleInstantTask(final List<Component> list, final BaseGui gui, final Player player, final Material update, final long delay, final long interval, final long duration) {
            new DecorationInsantTask(list, gui, player, update, interval, duration).runTaskTimer(gui.getPlugin(), delay, interval);
        }

        protected abstract void performPattern(Decoration decoration, Material update, BaseGui gui, Player player, long duration);
    }

    private static class DecorationInsantTask extends BukkitRunnable {
        private final List<Component> list;
        private final Material material;
        private final BaseGui gui;

        private final Player player;
        private final long interval;
        private long duration;

        public DecorationInsantTask(final List<Component> list, final BaseGui gui, final Player player, final Material material, final long interval, final long duration) {
            this.list = list;
            this.gui = gui;
            this.player = player;
            this.material = material;
            if (duration <= 0 || interval <= 0) {
                throw new IllegalArgumentException("Cannot assign duration or interval of 0 or less!");
            }
            this.interval = interval;
            this.duration = duration;
        }

        @Override
        public void run() {
            if (duration > 0) {
                duration -= interval;
                list.forEach(item -> {
                    ((Interactive) item).updateItem(null, null, material);
                    item.fillElement(gui, player);
                });
            } else {
                this.cancel();
            }
        }
    }

    private static class DecorationUpdateTask extends BukkitRunnable {

        private final Iterator<Component> iterator;
        private final Material material;
        private final Player player;
        private final BaseGui gui;

        public DecorationUpdateTask(final Iterator<Component> iterator, final BaseGui gui, final Player player, final Material material) {
            this.iterator = iterator;
            this.material = material;
            this.player = player;
            this.gui = gui;
        }

        @Override
        public void run() {
            if (iterator.hasNext()) {
                final Interactive component = (Interactive) iterator.next();
                component.updateItem(null, null, material);
                component.fillElement(gui, player);
            } else {
                this.cancel();
            }
        }
    }
}
