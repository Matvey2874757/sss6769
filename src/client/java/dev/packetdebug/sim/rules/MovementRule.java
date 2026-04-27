package dev.packetdebug.sim.rules;

import dev.packetdebug.config.PacketCategory;
import dev.packetdebug.config.PacketDebugConfig;
import dev.packetdebug.sim.PacketRule;
import dev.packetdebug.sim.PacketSimulatorManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public final class MovementRule implements PacketRule<ServerboundMovePlayerPacket> {
    private final PacketDebugConfig config;

    public MovementRule(PacketDebugConfig config) {
        this.config = config;
    }

    @Override
    public Class<ServerboundMovePlayerPacket> packetType() {
        return ServerboundMovePlayerPacket.class;
    }

    @Override
    public PacketCategory category() {
        return PacketCategory.MOVEMENT;
    }

    @Override
    public Packet<?> rewrite(ServerboundMovePlayerPacket packet, PacketSimulatorManager.Context context) {
        double x = config.clampCoord(config.movementX);
        double y = config.clampCoord(config.movementY);
        double z = config.clampCoord(config.movementZ);

        if (config.liveEmulation) {
            x += context.random().nextDouble(-0.15D, 0.15D);
            y += context.random().nextDouble(-0.05D, 0.05D);
            z += context.random().nextDouble(-0.15D, 0.15D);
        }

        float yaw = config.clampRotation(config.yaw);
        float pitch = config.clampRotation(config.pitch);
        return new ServerboundMovePlayerPacket.PosRot(x, y, z, yaw, pitch, config.onGround);
    }
}
