package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Background implements Component {
    private final ItemStack background;

    public Background(final Material background) {
        this.background = new ItemStack(background, 1);
    }

    public Background() {
        this.background = new ItemStack(Material.AIR, 1);
    }

    @Override
    public String getIdentifier() {
        return "BACKGROUND";
    }

    @Override
    public String getParent() {
        return null;
    }

    public void setMaterial(final Material material) {
        this.background.setType(material);
    }

    @Override
    public void fillElement(final BaseGui gui) {
        final Inventory inv = gui.getInventory();

        final int rows = inv.getSize() / 9;
        for (int r = 1; r < rows - 1; r++) {
            for (int c = 1; c < 8; c++) {
                inv.setItem((r * 9 + c), background);
            }
        }
    }

    @Override
    public Class<Background> getThisClass() {
        return Background.class;
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 0;
    }
}
