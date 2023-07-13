package com.ravingarinc.test;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.ravingarinc.api.I;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class TestListener implements Listener {
    private final ProtocolManager protocol = ProtocolLibrary.getProtocolManager();

    public TestListener(TestPlugin plugin) {
        protocol.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                I.log(Level.INFO, "Received dig packet" + event.getPacket().getPlayerDigTypes().read(0).name());
                BlockPosition pos = event.getPacket().getBlockPositionModifier().read(0);
                I.log(Level.INFO, "Got block at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
            }
        });
    }
}
