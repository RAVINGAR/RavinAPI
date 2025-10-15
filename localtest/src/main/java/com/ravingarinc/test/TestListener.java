package com.ravingarinc.test;

import org.bukkit.event.Listener;

public class TestListener implements Listener {
    //private final ProtocolManager protocol = ProtocolLibrary.getProtocolManager();

    public TestListener(TestPlugin plugin) {
        /*
        protocol.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                I.log(Level.INFO, "Received dig packet" + event.getPacket().getPlayerDigTypes().read(0).name());
                BlockPosition pos = event.getPacket().getBlockPositionModifier().read(0);
                I.log(Level.INFO, "Got block at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
            }
        });*/
    }
}
