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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        @Override
        public void addShiftAction(Action action) {

        }

        @Override
        public void performAllShiftActions(BaseGui gui, Player player) {

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

    PageFiller<?> PAGE_FILLER = new PageFiller<>("PAGE_FILLER", "", (a, b) -> null, (g, p) -> new ArrayList<>());

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

    int MAX_LINE_LENGTH = 32;

    default net.kyori.adventure.text.Component formatString(@Nullable final String input) {
        return Component.format(input);
    }
    MiniMessage miniMessage = MiniMessage.miniMessage();

    static net.kyori.adventure.text.Component format(@Nullable final String input) {
        if (input == null) {
            return net.kyori.adventure.text.Component.text("").color(NamedTextColor.DARK_GRAY);
        }
        return MiniMessage.miniMessage().deserialize(convertToMiniMessage(input)).decoration(TextDecoration.ITALIC,
                false);
    }

    /**
     * Converts legacy bukkit chat colour codes to minimessage format.
     */
    public static String convertToMiniMessage(String message) {
        if (message == null) {
            return "";
        }
        // This pattern will find all bukkit colour codes, including the ampersand.
        Pattern pattern = Pattern.compile("&([0-9a-fk-or])");
        Matcher matcher = pattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            char code = matcher.group(1).charAt(0);
            String replacement = getMiniMessageTag(code);
            if (replacement != null) {
                matcher.appendReplacement(sb, replacement);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String getMiniMessageTag(char code) {
        return switch (Character.toLowerCase(code)) {
            case '0' -> "<black>";
            case '1' -> "<dark_blue>";
            case '2' -> "<dark_green>";
            case '3' -> "<dark_aqua>";
            case '4' -> "<dark_red>";
            case '5' -> "<dark_purple>";
            case '6' -> "<gold>";
            case '7' -> "<gray>";
            case '8' -> "<dark_gray>";
            case '9' -> "<blue>";
            case 'a' -> "<green>";
            case 'b' -> "<aqua>";
            case 'c' -> "<red>";
            case 'd' -> "<light_purple>";
            case 'e' -> "<yellow>";
            case 'f' -> "<white>";
            case 'k' -> "<obfuscated>";
            case 'l' -> "<bold>";
            case 'm' -> "<strikethrough>";
            case 'n' -> "<underline>";
            case 'o' -> "<italic>";
            case 'r' -> "<reset>";
            default -> null;
        };
    }

    static List<net.kyori.adventure.text.Component> formatList(@Nullable final String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }

        final String message = convertToMiniMessage(input).replace("<newline>", "\n");
        final List<net.kyori.adventure.text.Component> result = new ArrayList<>();
        // Split by the newline character. The -1 limit preserves trailing empty strings,
        // which is important for inputs ending in one or more newlines.
        final String[] manualLines = message.split("\n", -1);

        String activeTagsPrefix = "";
        final Pattern tagPattern = Pattern.compile("<(/?[a-zA-Z0-9_]+)>");

        for (String manualLine : manualLines) {
            // If a line is empty (from a double newline, e.g., "...\n\n..."),
            // add a blank component and move to the next line.
            if (manualLine.isEmpty()) {
                result.add(net.kyori.adventure.text.Component.text(""));
                continue;
            }

            final String[] words = manualLine.split(" ");
            StringBuilder currentLineBuilder = new StringBuilder(activeTagsPrefix);

            for (String word : words) {
                String plainWord = word.replaceAll("<[^>]+>", "");
                String plainCurrentLine = currentLineBuilder.toString().replaceAll("<[^>]+>", "");

                // Check if adding the new word would make the line too long.
                // An extra check ensures we don't break on the very first word of a line.
                if (!plainCurrentLine.isEmpty() && plainCurrentLine.length() + plainWord.length() + 1 > MAX_LINE_LENGTH) {
                    result.add(miniMessage.deserialize(currentLineBuilder.toString())
                            .decoration(TextDecoration.ITALIC, false));
                    currentLineBuilder.setLength(0);
                    currentLineBuilder.append(activeTagsPrefix);
                }

                // Append the word, with a leading space if the line isn't empty.
                if (!currentLineBuilder.toString().replaceAll("<[^>]+>", "").isEmpty()) {
                    currentLineBuilder.append(" ");
                }
                currentLineBuilder.append(word);

                // After adding the word, recalculate the active tags prefix for the *next* line.
                List<String> tagStack = new ArrayList<>();
                Matcher matcher = tagPattern.matcher(currentLineBuilder.toString());
                while (matcher.find()) {
                    String tagContent = matcher.group(1);
                    if (tagContent.startsWith("/")) {
                        String tagName = tagContent.substring(1);
                        int lastIndex = tagStack.lastIndexOf(tagName);
                        if (lastIndex != -1) {
                            tagStack.remove(lastIndex);
                        }
                    } else {
                        tagStack.add(tagContent);
                    }
                }

                StringBuilder prefixBuilder = new StringBuilder();
                for (String tagName : tagStack) {
                    prefixBuilder.append("<").append(tagName).append(">");
                }
                activeTagsPrefix = prefixBuilder.toString();
            }

            // Add the final line to the result list if it has content.
            if (!currentLineBuilder.toString().replaceAll("<[^>]+>", "").isEmpty()) {
                result.add(miniMessage.deserialize(currentLineBuilder.toString())
                        .decoration(TextDecoration.ITALIC, false));
            }
        }

        return result;
    }

    default List<net.kyori.adventure.text.Component> formatLore(@Nullable final String input) {
        return formatList(input);
    }
}
