package dev.packetdebug.sim;

import dev.packetdebug.config.PacketDebugConfig;
import dev.packetdebug.config.PacketCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;

public final class PacketSenders {
    private PacketSenders() {
    }

    public static boolean sendMovement(PacketDebugConfig config) {
        if (!config.isCategoryEnabled(PacketCategory.MOVEMENT)) {
            return false;
        }
        Connection connection = connection();
        if (connection == null) {
            return false;
        }

        Packet<?> packet = new ServerboundMovePlayerPacket.PosRot(
                config.clampCoord(config.movementX),
                config.clampCoord(config.movementY),
                config.clampCoord(config.movementZ),
                config.clampRotation(config.yaw),
                config.clampRotation(config.pitch),
                config.onGround
        );

        connection.send(packet);
        return true;
    }

    public static boolean sendChat(PacketDebugConfig config) {
        if (!config.isCategoryEnabled(PacketCategory.CHAT)) {
            return false;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.getConnection() == null) {
            return false;
        }

        String text = config.chatMessage == null ? "" : config.chatMessage.trim();
        if (text.isEmpty()) {
            return false;
        }

        if (text.startsWith("/")) {
            client.getConnection().sendCommand(text.substring(1));
        } else {
            client.getConnection().sendChat(text);
        }
        return true;
    }

    public static boolean sendBlockAction(PacketDebugConfig config) {
        if (!config.isCategoryEnabled(PacketCategory.BLOCK_ACTIONS)) {
            return false;
        }
        Connection connection = connection();
        if (connection == null) {
            return false;
        }

        BlockPos pos = new BlockPos(config.targetBlockX, config.clampBlockY(config.targetBlockY), config.targetBlockZ);
        Packet<?> packet = new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                pos,
                Direction.UP,
                config.nextActionSequence()
        );
        connection.send(packet);
        return true;
    }

    public static boolean sendUseItem(PacketDebugConfig config) {
        if (!config.isCategoryEnabled(PacketCategory.ITEMS)) {
            return false;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.gameMode == null || client.player == null) {
            return false;
        }
        client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
        return true;
    }

    private static Connection connection() {
        Minecraft client = Minecraft.getInstance();
        if (client.getConnection() == null) {
            return null;
        }
        return client.getConnection().getConnection();
    }
}
