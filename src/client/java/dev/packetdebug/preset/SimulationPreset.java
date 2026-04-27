package dev.packetdebug.preset;

import dev.packetdebug.config.PacketDebugConfig;
import dev.packetdebug.sim.PacketSimulatorManager;

@FunctionalInterface
public interface SimulationPreset {
    void apply(PacketSimulatorManager manager, PacketDebugConfig config);
}
