package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.Bukkit;

import java.util.function.Function;

public class CommandAction extends Action {
    private final boolean asConsole;
    private final Function<String, String> parser;

    public CommandAction(final String command, final boolean asConsole, final Function<String, String> parser) {
        super(command);
        this.asConsole = asConsole;
        this.parser = parser;
    }

    public CommandAction(final String command, final boolean asConsole) {
        this(command, asConsole, s -> s);
    }

    @Override
    public void performAction(final BaseGui gui) {
        gui.getPlugin().getServer().dispatchCommand(asConsole ? Bukkit.getConsoleSender() : gui.getPlayer(), parser.apply(pointer));
    }
}
