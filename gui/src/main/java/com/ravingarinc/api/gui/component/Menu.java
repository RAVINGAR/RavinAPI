package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Element;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.action.MenuAction;
import com.ravingarinc.api.gui.component.icon.Icon;
import com.ravingarinc.api.gui.component.icon.StaticIcon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

public class Menu extends Element {
    private final Material border1;
    private final Material border2;
    private final int backIcon;
    protected List<ItemStack> iconsToPlace;
    private Material background;

    /**
     * @param identifier  The identifier of the menu.
     * @param parent      The parent of the menu which HAS to be another menu.
     * @param border1     Material to use for border pattern
     * @param border2     Other material to use for border pattern
     * @param backIconIdx The index where the default back icon should be placed, or -1 if you don't want a back icon
     */
    public Menu(final String identifier, final String parent, @NotNull final Material border1, @NotNull final Material border2, final int backIconIdx) {
        super(identifier, parent, 5);
        iconsToPlace = null;
        this.border1 = border1;
        this.border2 = border2;
        this.backIcon = backIconIdx;
        this.background = Material.AIR;
    }

    public void queueIconToPlace(final ItemStack icon) {
        if (iconsToPlace == null) {
            iconsToPlace = new LinkedList<>();
        }
        iconsToPlace.add(icon);
    }

    public void finalise() {
        if (border1 != Material.AIR && border2 != Material.AIR) {
            addChild(() -> new Border(border1, border2));
        }
        addChild(() -> new Background(background));
        if (backIcon != -1) {
            addChild(() -> new StaticIcon("BACK", ChatColor.RED + "Back", "", this.getIdentifier(), Material.BARRIER, new MenuAction(this.parent), p -> true, i -> {
            }, backIcon));
        }
    }

    public void addIcon(final String identifier, final String display, final String lore, final Material material, final Action action, final Predicate<BaseGui> predicate) {
        addChild(() -> new Icon(identifier, display, lore, this.identifier, material, action, predicate, i -> {
        }));
    }

    public void addIcon(final String identifier, final String display, final String lore, final Material material, final Action action) {
        addIcon(identifier, display, lore, material, action, t -> true);
    }

    /**
     * Adds an icon which points to a menu as specified by it's the icon's name itself
     */
    public void addMenuIcon(final String display, final String lore, final Material material, final Predicate<BaseGui> predicate) {
        final String[] split = display.toUpperCase().replaceAll("\\[", "").replaceAll("]", "").split(" ");
        final String identifier = ChatColor.stripColor(split[split.length - 1]);
        addIcon(identifier, display, lore, material, new MenuAction(identifier), predicate);
    }

    private void placeIcons(final BaseGui gui) {
        if (iconsToPlace != null) {
            final int amount = iconsToPlace.size();

            //Count icons and arrange accordingly.
            /*  45 Size
                10 | 11 | 12 | 13 | 14 | 15 | 16
                19 | 20 | 21 | 22 | 23 | 24 | 25
                28 | 29 | 30 | 31 | 32 | 33 | 34

                Size 63
                10 | 11 | 12 | 13 | 14 | 15 | 16
                19 | 20 | 21 | 22 | 23 | 24 | 25
                28 | 29 | 30 | 31 | 32 | 33 | 34
                37 | 38 | 39 | 40 | 41 | 42 | 43
                46 | 47 | 48 | 49 | 50 | 51 | 52
                */
            /*
            search format works as follows. Counting
                1 -> size % 2 == 0 ? : size / 2
            */

            final Inventory inv = gui.getInventory();
            final int size = inv.getSize();
            if (amount > (size - 18 - (size / 9 - 2) * 2)) {
                //If amount of items is greater than available blank inventory space
                I.log(Level.WARNING, "Amount of items for menu " + this.identifier + " was too large!");
            }
            //Mental note: I hate math

            switch (amount) {
                case 1, 2, 3, 4 -> setItemsInRow(amount, (int) ((double) size / 9.0 / 2.0), inv);
                case 5, 6, 7, 8 -> {
                    final int row = (int) (((double) size / 9.0) * (1.0 / 3.0));
                    final int half = amount - (amount / 2);
                    setItemsInRow(half, row, inv);
                    setItemsInRow(amount - half, row + 2, inv);
                }
                case 9, 10, 11, 12 -> {
                    final int row = (int) (((double) size / 9.0) * (1.0 / 3.0));
                    final int third = amount - 4;
                    setItemsInRow(4, row, inv);
                    setItemsInRow(third / 2, row + 1, inv);
                    setItemsInRow(third - (third / 2), row + 2, inv);
                }
                default -> {
                    for (int r = 1; r < size / 9 - 2; r++) {
                        setItemsInRow(Math.min(7, iconsToPlace.size()), r, inv);
                    }
                    if (!iconsToPlace.isEmpty()) {
                        I.log(Level.WARNING, "Could not fit all components on one page!");
                    }
                }
            }
            iconsToPlace = null;
        }
    }

    private void setItemsInRow(final int amount, int row, final Inventory inv) {
        row = row * 9 + 5 - amount;
        for (int i = row; i <= (row + (amount - 1) * 2) && !iconsToPlace.isEmpty(); i += 2) {
            inv.setItem(i, iconsToPlace.remove(0));
        }
    }

    public void setBackground(final Material background) {
        this.background = background;
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {
        super.fillElement(gui, player);
        placeIcons(gui);
    }

    @Override
    public Class<Menu> getThisClass() {
        return Menu.class;
    }
}
