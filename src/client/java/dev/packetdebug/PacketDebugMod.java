package dev.packetdebug;

import dev.packetdebug.command.PacketDebugCommand;
import dev.packetdebug.config.PacketDebugConfig;
import dev.packetdebug.gui.PacketDebugScreen;
import dev.packetdebug.preset.DefaultPresets;
import dev.packetdebug.sim.PacketSimulatorManager;
import dev.packetdebug.sim.rules.ChatRule;
import dev.packetdebug.sim.rules.MovementRule;
import dev.packetdebug.sim.rules.PlayerChatRule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class PacketDebugMod implements ClientModInitializer {
    public static final String MOD_ID = "packetdebug";

    private static final PacketSimulatorManager MANAGER = new PacketSimulatorManager();
    private static final PacketDebugConfig CONFIG = new PacketDebugConfig();

    private static KeyMapping openGuiKey;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.packetdebug.open_gui",
                GLFW.GLFW_KEY_R,
                "category.packetdebug"
        ));

        MANAGER.registerRule(new MovementRule(CONFIG));
        MANAGER.registerRule(new ChatRule(CONFIG));
        MANAGER.registerRule(new PlayerChatRule(CONFIG));
        DefaultPresets.register(MANAGER, CONFIG);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                PacketDebugCommand.register(dispatcher, this::openScreen));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.consumeClick()) {
                openScreen();
            }
        });
    }

    private void openScreen() {
        Minecraft client = Minecraft.getInstance();
        client.setScreen(new PacketDebugScreen(MANAGER, CONFIG));
    }

    public static PacketSimulatorManager manager() {
        return MANAGER;
    }

    public static PacketDebugConfig config() {
        return CONFIG;
    }
}
