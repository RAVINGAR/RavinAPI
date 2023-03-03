package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class Border implements Component {
    private final ItemStack pattern1;
    private final ItemStack pattern2;

    public Border(@NotNull final Material pattern1, @NotNull final Material pattern2) {
        this.pattern1 = new ItemStack(pattern1, 1);
        ItemMeta meta = this.pattern1.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_GRAY + "");
            this.pattern1.setItemMeta(meta);
        }
        this.pattern2 = new ItemStack(pattern2, 1);
        meta = this.pattern2.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_GRAY + "");
            this.pattern2.setItemMeta(meta);
        }
    }

    @Override
    public String getIdentifier() {
        return "BORDER";
    }

    @Override
    public String getParent() {
        return null;
    }

    public void setPrimaryMaterial(final Material material) {
        this.pattern1.setType(material);
    }

    public void setSecondaryMaterial(final Material material) {
        this.pattern2.setType(material);
    }

    @Override
    public void fillElement(final BaseGui gui) {
        final Inventory inv = gui.getInventory();
        final int size = inv.getSize();
        final int rows = size / 9;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < 9; c++) {
                final int i = r * 9 + c;
                if (r == 0 || r == rows - 1 || c == 0 || c == 8) {
                    inv.setItem(i, i % 2 == 0 ? pattern1 : pattern2);
                }
            }
        }
    }

    @Override
    public Class<Border> getThisClass() {
        return Border.class;
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 0;
    }
}
