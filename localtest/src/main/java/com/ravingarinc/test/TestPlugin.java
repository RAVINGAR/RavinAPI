package com.ravingarinc.test;

import com.ravingarinc.api.I;
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
import java.util.List;

public class TestPlugin extends JavaPlugin {
    private static final String GUI = "Test";
    private GuiProvider provider;

    @Override
    public void onEnable() {
        I.load(this, true);
        provider = GuiProvider.getInstance(this);
        getCommand("test").setExecutor(new GuiCommand());
    }

    @Override
    public void onDisable() {
        provider.shutdown();
    }

    private GuiBuilder<BaseGui> getBuilder() {
        final GuiBuilder<BaseGui> builder = new GuiBuilder<>(this, GUI, BaseGui.class, 27);

        /*
            0  1  2  3  4  5  6  7  8
            9  10 11 12 13 14 15 16 17
            18 19 20 21 22 23 24 25 26
             */
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

        /*
        .addStaticIcon("PREVIOUS_PAGE", "&cPrevious Page", "&7Navigate to the previous page", Material.ARROW,
                            new PreviousPageAction("EDITOR_ACTOR_PAGE", "FRAME"),
                            (g) -> page.hasPreviousPage(), i -> {
                            }, 9).finalise()
                    .addStaticIcon("NEXT_PAGE", "&cNext Page", "&7Navigate to the next page", Material.ARROW,
                            new NextPageAction("EDITOR_ACTOR_PAGE", "FRAME"),
                            (g) -> page.hasNextPage(), i -> {
                            }, 17);
         */
        builder.createMenu("MAIN", null)
                .addPage("SPECIAL_PAGE", 10, 11, 12, 13, 14, 15, 16)
                .addPreviousPageIcon(9).finalise()
                .addNextPageIcon(17).finalise()
                .addPageFiller("SIZEABLE", () -> list)
                .setDisplayNameProvider((s) -> s)
                .setLoreProvider((s) -> "This is lore of " + s)
                .setMaterialProvider((s) -> Material.GLASS)
                .addActionProvider((s) -> new RunnableAction((gui) -> gui.getPlayer().sendMessage("You clicked this " + s)))
        ;

        return builder;
    }

    private class GuiCommand implements CommandExecutor {

        @Override
        public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
            if (sender instanceof Player player) {
                provider.openCustomGui(GUI, TestPlugin.this::getBuilder, player);
                return true;
            }
            return false;
        }
    }
}
