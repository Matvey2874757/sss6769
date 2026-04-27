package dev.packetdebug.sim;

import dev.packetdebug.config.PacketCategory;
import net.minecraft.network.protocol.Packet;

public interface PacketRule<T extends Packet<?>> {
    Class<T> packetType();

    PacketCategory category();

    Packet<?> rewrite(T packet, PacketSimulatorManager.Context context);
}
