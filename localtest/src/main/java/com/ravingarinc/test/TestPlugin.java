package com.ravingarinc.test;

import com.ravingarinc.api.I;
import com.ravingarinc.api.Pair;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.builder.GuiBuilder;
import com.ravingarinc.api.gui.builder.GuiProvider;
import com.ravingarinc.api.gui.component.action.RunnableAction;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class TestPlugin extends JavaPlugin {
    private static final String GUI = "Test";

    @Override
    public void onEnable() {
        I.load(this, true);
        getCommand("test").setExecutor(new GuiCommand());


        getServer().getPluginManager().registerEvents(new TestListener(this), this);
    }

    @Override
    public void onDisable() {
        GuiProvider.unregister();
    }

    private class GuiCommand implements CommandExecutor {
        private final BaseGui gui;

        public GuiCommand() {
            final GuiBuilder<BaseGui> builder = new GuiBuilder<>(TestPlugin.this, GUI, BaseGui.class, 27);

            builder.setPrimaryBorder(Material.GRAY_STAINED_GLASS);
            builder.setSecondaryBorder(Material.GRAY_STAINED_GLASS);
            builder.setBackIconIndex(22);

            final List<String> list = new ArrayList<>();
            list.add("Test 1");
            list.add("Test 2");
            list.add("Test 3");
            list.add("Test 4");
            list.add("Test 5");
            list.add("Test 6");
            list.add("Test 7");
            list.add("Test 8");
            list.add("Test 9");
            list.add("Test 10");

            builder.createMenu("MAIN", null)
                    .addPage("SPECIAL_PAGE", 10, 11, 12, 13, 14, 15, 16)
                    .addPreviousPageIcon(9).finalise()
                    .addNextPageIcon(17).finalise()
                    .addPageFiller("SIZEABLE", () -> list)
                    .setDisplayNameProvider((s) -> s)
                    .setLoreProvider((s) -> "This is lore of " + s)
                    .setMaterialProvider((s) -> Material.GLASS)
                    .addActionProvider((s) -> new RunnableAction((gui, p) -> p.sendMessage("You clicked this " + s)));

            gui = builder.build();
        }

        @Override
        public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
            //TEST
            var set = new HashSet<Pair<Integer, Integer>>();
            set.add(new Pair<>(2, 4));
            set.add(new Pair<>(6, 4));
            set.add(new Pair<>(-1, 7));
            set.add(new Pair<>(8, -42));

            var pair = new Pair<>(6, 4);
            I.log(Level.WARNING, "This is a test! Does set contain pair? %s", set.contains(pair));

            if (sender instanceof Player player) {
                player.openInventory(gui.getInventory());
                return true;
            }
            return false;
        }
    }
}
