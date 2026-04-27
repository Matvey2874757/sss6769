package dev.packetdebug.preset;

import dev.packetdebug.config.PacketDebugConfig;
import dev.packetdebug.sim.PacketSimulatorManager;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DefaultPresets {
    private static final Map<String, SimulationPreset> PRESETS = new LinkedHashMap<>();

    private DefaultPresets() {
    }

    public static void register(PacketSimulatorManager manager, PacketDebugConfig config) {
        PRESETS.clear();
        PRESETS.put("Отладка взлёта", (m, c) -> {
            c.simulationEnabled = true;
            c.movementY = c.movementY + 20.0D;
            c.onGround = false;
        });

        PRESETS.put("Спам-тест чата", (m, c) -> {
            c.simulationEnabled = true;
            c.chatMessage = "PacketDebug spam test";
            c.liveEmulation = true;
        });

        PRESETS.put("Имитация копания на расстоянии", (m, c) -> {
            c.simulationEnabled = true;
            c.targetBlockX += 8;
            c.targetBlockY = c.clampBlockY(c.targetBlockY);
            c.targetBlockZ += 8;
        });
    }

    public static Map<String, SimulationPreset> presets() {
        return PRESETS;
    }
}
