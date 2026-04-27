package dev.packetdebug.config;

import java.util.EnumMap;
import java.util.Map;

public final class PacketDebugConfig {
    private final Map<PacketCategory, Boolean> enabledCategories = new EnumMap<>(PacketCategory.class);

    public boolean simulationEnabled = false;
    public boolean liveEmulation = false;

    public double movementX = 0.0D;
    public double movementY = 64.0D;
    public double movementZ = 0.0D;
    public float yaw = 0.0F;
    public float pitch = 0.0F;
    public boolean onGround = true;

    public String chatMessage = "PacketDebug test";

    public int targetBlockX = 0;
    public int targetBlockY = 64;
    public int targetBlockZ = 0;

    public String itemId = "minecraft:stone";
    private int actionSequence = 0;

    public PacketDebugConfig() {
        for (PacketCategory category : PacketCategory.values()) {
            enabledCategories.put(category, true);
        }
    }

    public boolean isCategoryEnabled(PacketCategory category) {
        return enabledCategories.getOrDefault(category, false);
    }

    public void setCategoryEnabled(PacketCategory category, boolean enabled) {
        enabledCategories.put(category, enabled);
    }

    public double clampCoord(double value) {
        return Math.max(-30_000_000D, Math.min(30_000_000D, value));
    }

    public float clampRotation(float value) {
        return Math.max(-180F, Math.min(180F, value));
    }

    public int clampBlockY(int value) {
        return Math.max(-64, Math.min(320, value));
    }

    public int nextActionSequence() {
        // Vanilla packets with sequence should be monotonic.
        return ++actionSequence;
    }
}
