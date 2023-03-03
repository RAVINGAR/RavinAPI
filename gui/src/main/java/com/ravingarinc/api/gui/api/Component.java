package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.component.Background;
import com.ravingarinc.api.gui.component.Border;
import com.ravingarinc.api.gui.component.Closeable;
import com.ravingarinc.api.gui.component.Decoration;
import com.ravingarinc.api.gui.component.InputComponent;
import com.ravingarinc.api.gui.component.Menu;
import com.ravingarinc.api.gui.component.Page;
import com.ravingarinc.api.gui.component.Queueable;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.icon.Dynamic;
import com.ravingarinc.api.gui.component.icon.Icon;
import com.ravingarinc.api.gui.component.icon.Observer;
import com.ravingarinc.api.gui.component.icon.PageFiller;
import com.ravingarinc.api.gui.component.icon.PlaceableIcon;
import com.ravingarinc.api.gui.component.icon.StateIcon;
import com.ravingarinc.api.gui.component.icon.StaticIcon;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public interface Component {
    Interactive INTERACTIVE = new Interactive() {
        @Override
        public String getIdentifier() {
            return "INTERACTIVE";
        }

        @Override
        public boolean handleClickedItem(final BaseGui gui, final InventoryClickEvent event) {
            return true;
        }

        @Override
        public ItemStack getItem() {
            return null;
        }

        @Override
        public void setItem(final ItemStack item) {
        }

        @Override
        public void addChild(final Supplier<Component> component) {
        }

        @Override
        public void addAction(final Action action) {
        }

        @Override
        public void performAllActions(final BaseGui gui) {
        }

        @Override
        public String getParent() {
            return null;
        }

        @Override
        public void fillElement(final BaseGui gui) {
        }

        @Override
        public Class<Interactive> getThisClass() {
            return Interactive.class;
        }

        @NotNull
        @Override
        public Integer getPriority() {
            return 0;
        }
    };
    Background BACKGROUND = new Background();
    Border BORDER = new Border(Material.AIR, Material.AIR);
    Closeable CLOSEABLE = new Closeable(null, null);
    Decoration DECORATION = new Decoration("DECORATION", null);
    Menu MENU = new Menu("MENU", null, Material.AIR, Material.AIR, -1);
    Dynamic DYNAMIC = new Dynamic(null, null);
    Icon ICON = new Icon("ICON", "", "", "", Material.STONE, null, p -> true, (i) -> {
    });
    Observer OBSERVER = new Observer(ICON, () -> true);
    PlaceableIcon PLACEABLE_ICON = new PlaceableIcon("PLACEABLE_ICON", null, 0, (t) -> true);
    StaticIcon STATIC_ICON = new StaticIcon("STATIC_ICON", "", "", "", Material.STONE, null, p -> true, i -> {
    }, 0);
    StateIcon<?> STATE_ICON = new StateIcon<>("STATE_ICON", "", null, t -> true, 0, () -> 0);
    Queueable QUEUEABLE = new Queueable("", false);

    Page PAGE = new Page("PAGE", "");

    InputComponent INPUT_COMPONENT = new InputComponent("INPUT_COMPONENT", null, "", (string) -> {
    });

    PageFiller<?> PAGE_FILLER = new PageFiller<>("PAGE_FILLER", null, null, null);

    String getIdentifier();

    String getParent();

    void fillElement(BaseGui gui);

    /**
     * Get priority of a component. Lower values mean higher priority
     */
    @NotNull
    Integer getPriority();

    Class<? extends Component> getThisClass();

    @NotNull
    default <T extends Component> Optional<T> findComponent(final T type, final String identifier) {
        return Optional.empty();
    }

    default boolean isSameType(final Component component) {
        return component.getThisClass().equals(this.getThisClass());
    }
}
