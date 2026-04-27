package dev.packetdebug.sim.rules;

import dev.packetdebug.config.PacketCategory;
import dev.packetdebug.config.PacketDebugConfig;
import dev.packetdebug.sim.PacketRule;
import dev.packetdebug.sim.PacketSimulatorManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatPacket;

public final class PlayerChatRule implements PacketRule<ServerboundChatPacket> {
    private final PacketDebugConfig config;

    public PlayerChatRule(PacketDebugConfig config) {
        this.config = config;
    }

    @Override
    public Class<ServerboundChatPacket> packetType() {
        return ServerboundChatPacket.class;
    }

    @Override
    public PacketCategory category() {
        return PacketCategory.CHAT;
    }

    @Override
    public Packet<?> rewrite(ServerboundChatPacket packet, PacketSimulatorManager.Context context) {
        String configured = config.chatMessage == null ? "" : config.chatMessage.trim();
        if (configured.isEmpty() || configured.length() > 240 || configured.startsWith("/")) {
            return packet;
        }

        return new ServerboundChatPacket(
                configured,
                packet.timeStamp(),
                packet.salt(),
                packet.lastSeenMessages()
        );
    }
}
