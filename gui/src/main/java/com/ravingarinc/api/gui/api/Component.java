package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.component.*;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.icon.*;
import com.ravingarinc.api.gui.component.observer.ItemUpdater;
import com.ravingarinc.api.gui.component.observer.Observer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

public interface Component {

    Interactive INTERACTIVE = new Interactive() {
        @Override
        public String getIdentifier() {
            return "INTERACTIVE";
        }

        @Override
        public boolean handleClickedItem(final BaseGui gui, final InventoryClickEvent event, Player player) {
            return true;
        }

        @Override
        public ItemStack getItem() {
            return null;
        }

        @Override
        public void addAmount(final int delta) {

        }

        @Override
        public void setAmount(int amount) {

        }

        @Override
        public void addChild(final Supplier<Component> component) {
        }

        @Override
        public void addAction(final Action action) {
        }

        @Override
        public void performAllActions(final BaseGui gui, Player player) {
        }

        @Override
        public String getParent() {
            return null;
        }

        @Override
        public void fillElement(final BaseGui gui, Player player) {
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
    Closeable CLOSEABLE = new Closeable(null);
    Decoration DECORATION = new Decoration("DECORATION", null);
    Menu MENU = new Menu("MENU", null, Material.AIR, Material.AIR, -1);
    Icon ICON = new Icon("ICON", "", "", "", Material.STONE, null, (g, p) -> true, (i) -> {
    });
    Observer OBSERVER = new Observer(ICON) {
        @Override
        public void fillElement(final BaseGui gui, Player player) {

        }
    };
    PlaceableIcon PLACEABLE_ICON = new PlaceableIcon("PLACEABLE_ICON", null, 0, (t) -> true);
    StaticIcon STATIC_ICON = new StaticIcon("STATIC_ICON", "", "", "", Material.STONE, null, (g, p) -> true, i -> {
    }, 0);
    StateIcon<?> STATE_ICON = new StateIcon<>("STATE_ICON", "", null, (g, p) -> true, 0, (g) -> 0);
    Queueable QUEUEABLE = new Queueable("", false);

    Page PAGE = new Page("PAGE", "");

    PageIcon PAGE_ICON = new PageIcon("PAGE_ICON", "", "", "", Material.STONE, (g, p) -> true, itemStack -> {
    });

    ChatInputComponent CHAT_INPUT_COMPONENT = new ChatInputComponent();

    PageFiller<?> PAGE_FILLER = new PageFiller<>("PAGE_FILLER", "", (a, b) -> null, g -> new ArrayList<>());

    ItemUpdater ITEM_UPDATER = new ItemUpdater(null);

    String getIdentifier();

    String getParent();

    void fillElement(BaseGui gui, Player player);

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

    @SuppressWarnings("deprecated")
    default NamespacedKey getKey(final String key) {
        return new NamespacedKey("ravinapi_gui", key);
    }

    static net.kyori.adventure.text.Component format(@Nullable final String input) {
        if (input == null) {
            return net.kyori.adventure.text.Component.text("").color(NamedTextColor.DARK_GRAY);
        }
        if (input.contains("&") || input.contains("§")) {
            final var builder = net.kyori.adventure.text.Component.text();
            for (String part : ChatColor.translateAlternateColorCodes('&', input.replaceAll("§", "&")).split("\n")) {
                builder.append(net.kyori.adventure.text.Component.text(part));
            }
            return builder.build();
        } else {
            return MiniMessage.miniMessage().deserialize(input).decoration(TextDecoration.ITALIC, false);
        }
    }

    default net.kyori.adventure.text.Component formatString(@Nullable final String input) {
        return Component.format(input);
    }
}
