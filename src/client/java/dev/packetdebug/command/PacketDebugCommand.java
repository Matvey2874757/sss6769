package dev.packetdebug.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public final class PacketDebugCommand {
    private PacketDebugCommand() {
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, Runnable openGuiAction) {
        dispatcher.register(ClientCommandManager.literal("packetdebug")
                .then(ClientCommandManager.literal("gui")
                        .executes(context -> {
                            openGuiAction.run();
                            context.getSource().sendFeedback(Component.literal("PacketDebug GUI opened."));
                            return 1;
                        }))
        );
    }
}
