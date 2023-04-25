package com.ravingarinc.api.gui.component.action;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundAction extends Action {
    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundAction(final Sound sound, final float volume, final float pitch) {
        super("SOUND_ACTION", 4);
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void performAction(final BaseGui gui, Player performer) {
        performer.playSound(performer.getLocation(), sound, volume, pitch); //TODO change this to use the BaseGui's location
    }
}
