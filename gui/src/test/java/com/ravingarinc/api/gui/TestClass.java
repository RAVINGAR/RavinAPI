package com.ravingarinc.api.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestClass {

    static final int MAX_LINE_LENGTH = 32;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

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

    static List<Component> formatList(@Nullable final String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }

        final String message = convertToMiniMessage(input).replaceAll("\n", "<newline>");
        final List<Component> result = new ArrayList<>();
        // Split by space, this will keep tags attached to words.
        final String[] words = message.split(" ");

        StringBuilder currentLineBuilder = new StringBuilder();
        // This will hold the sequence of open tags for the next line, like "<red><bold>"
        String activeTagsPrefix = "";

        // This pattern finds simple, attribute-less tags.
        final Pattern tagPattern = Pattern.compile("<(/?[a-zA-Z0-9_]+)>");

        for (String word : words) {
            // Get the plain text of the word itself to check its length against the line limit.
            String plainWord = word.replaceAll("<[^>]+>", "");
            // Get the plain text of the current line so far.
            String plainCurrentLine = currentLineBuilder.toString().replaceAll("<[^>]+>", "");

            // Check if adding the new word would make the line too long.
            if (!currentLineBuilder.isEmpty() && plainCurrentLine.length() + plainWord.length() + 1 > MAX_LINE_LENGTH) {
                // The line is full. Finalize it and add it to the results.
                result.add(miniMessage.deserialize(currentLineBuilder.toString())
                        .decoration(TextDecoration.ITALIC, false));

                // Start a new line, prepending the tags that were active from the end of the last line.
                currentLineBuilder.setLength(0);
                currentLineBuilder.append(activeTagsPrefix);
            }

            // Append the word, with a leading space if needed.
            if (!currentLineBuilder.isEmpty() && !plainCurrentLine.isEmpty()) {
                currentLineBuilder.append(" ");
            }
            currentLineBuilder.append(word);

            // After adding the word, recalculate the active tags prefix for the *next* line.
            // This is done by simulating a stack based on all tags in the current line builder.
            List<String> tagStack = new ArrayList<>();
            Matcher matcher = tagPattern.matcher(currentLineBuilder.toString());
            while (matcher.find()) {
                String tagContent = matcher.group(1);
                if (tagContent.startsWith("/")) {
                    // Closing tag: remove the corresponding opening tag from our stack.
                    String tagName = tagContent.substring(1);
                    int lastIndex = tagStack.lastIndexOf(tagName);
                    if (lastIndex != -1) {
                        tagStack.remove(lastIndex);
                    }
                } else {
                    // Opening tag: add it to our stack.
                    tagStack.add(tagContent);
                }
            }

            // Reconstruct the prefix for the next line from the current tag stack.
            StringBuilder prefixBuilder = new StringBuilder();
            for (String tagName : tagStack) {
                prefixBuilder.append("<").append(tagName).append(">");
            }
            activeTagsPrefix = prefixBuilder.toString();
        }

        // Add the final line to the result list if it has content.
        if (!currentLineBuilder.isEmpty()) {
            result.add(miniMessage.deserialize(currentLineBuilder.toString()).decoration(TextDecoration.ITALIC, false));
        }

        return result;
    }

    @Test
    void testFormatLore1() {
        String rawInput = "<red>This is a really long test of a thing! <yellow>We</yellow> should hope to get a similar result!</red>";

        final var result = formatList(rawInput);

        List<Component> components = new ArrayList<>();
        // Note: The expected components should be deserialized by MiniMessage to be valid for comparison.
        // The original test was comparing a MiniMessage component with a plain text one.
        components.add(MiniMessage.miniMessage()
                .deserialize("<red>This is a really long test of a</red>")
                .decoration(TextDecoration.ITALIC, false));
        components.add(MiniMessage.miniMessage()
                .deserialize("<red>thing! <yellow>We</yellow> should hope to get a</red>")
                .decoration(TextDecoration.ITALIC, false));
        components.add(MiniMessage.miniMessage()
                .deserialize("<red>similar result!</red>")
                .decoration(TextDecoration.ITALIC, false));

        System.out.println("--- testFormatLore1 ---");
        result.forEach(it -> System.out.println(PlainTextComponentSerializer.plainText().serialize(it)));
        assertEquals(components, result);
    }

    @Test
    void testFormatLore2() {
        String rawInput = "&7This is a really long test of a thing! We should hope to get a similar result!";

        final var result = formatList(rawInput);

        List<Component> components = new ArrayList<>();
        components.add(MiniMessage.miniMessage()
                .deserialize("<gray>This is a really long test of a</gray>")
                .decoration(TextDecoration.ITALIC, false));
        components.add(MiniMessage.miniMessage()
                .deserialize("<gray>thing! We should hope to get a</gray>")
                .decoration(TextDecoration.ITALIC, false));
        components.add(MiniMessage.miniMessage()
                .deserialize("<gray>similar result!</gray>")
                .decoration(TextDecoration.ITALIC, false));

        System.out.println("--- testFormatLore2 ---");
        result.forEach(it -> System.out.println(PlainTextComponentSerializer.plainText().serialize(it)));
        assertEquals(components, result);
    }

    @Test
    void testFormatLore3() {
        String rawInput = "&7This is a really long test of a thing!\n\nWe should hope to get a similar result!";

        final var result = formatList(rawInput);

        List<Component> components = new ArrayList<>();
        components.add(MiniMessage.miniMessage()
                .deserialize("<gray>This is a really long test of a</gray>")
                .decoration(TextDecoration.ITALIC, false));
        components.add(MiniMessage.miniMessage()
                .deserialize("<gray>thing!</gray>")
                .decoration(TextDecoration.ITALIC, false));
        components.add(MiniMessage.miniMessage()
                .deserialize("<newline><newline><gray>We should hope to get a</gray>")
                .decoration(TextDecoration.ITALIC, false));
        components.add(MiniMessage.miniMessage()
                .deserialize("<gray>similar result!</gray>")
                .decoration(TextDecoration.ITALIC, false));


        System.out.println("--- testFormatLore3 ---");
        result.forEach(it -> System.out.println(PlainTextComponentSerializer.plainText().serialize(it)));
        assertEquals(components, result);
    }
}
