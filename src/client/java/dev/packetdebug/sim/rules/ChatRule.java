package dev.packetdebug.sim.rules;

import dev.packetdebug.config.PacketCategory;
import dev.packetdebug.config.PacketDebugConfig;
import dev.packetdebug.sim.PacketRule;
import dev.packetdebug.sim.PacketSimulatorManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;

public final class ChatRule implements PacketRule<ServerboundChatCommandPacket> {
    private final PacketDebugConfig config;

    public ChatRule(PacketDebugConfig config) {
        this.config = config;
    }

    @Override
    public Class<ServerboundChatCommandPacket> packetType() {
        return ServerboundChatCommandPacket.class;
    }

    @Override
    public PacketCategory category() {
        return PacketCategory.CHAT;
    }

    @Override
    public Packet<?> rewrite(ServerboundChatCommandPacket packet, PacketSimulatorManager.Context context) {
        String configured = config.chatMessage == null ? "" : config.chatMessage.trim();
        if (configured.isEmpty() || configured.length() > 240) {
            return packet;
        }

        // Command packets are not rewritten yet to keep command signing/session data valid.
        return packet;
    }
}
