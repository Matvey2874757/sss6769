package dev.packetdebug.mixin;

import dev.packetdebug.PacketDebugMod;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @ModifyVariable(
            method = "send(Lnet/minecraft/network/protocol/Packet;)V",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private Packet<?> packetdebug$interceptOutgoingSimple(Packet<?> original) {
        return packetdebug$intercept(original);
    }

    @ModifyVariable(
            method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;Z)V",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private Packet<?> packetdebug$interceptOutgoingExtended(Packet<?> original) {
        return packetdebug$intercept(original);
    }

    private Packet<?> packetdebug$intercept(Packet<?> original) {
        try {
            return PacketDebugMod.manager().intercept(original);
        } catch (Exception ignored) {
            return original;
        }
    }
}
