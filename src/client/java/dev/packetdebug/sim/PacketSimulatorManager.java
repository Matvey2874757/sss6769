package dev.packetdebug.sim;

import dev.packetdebug.PacketDebugMod;
import dev.packetdebug.config.PacketCategory;
import dev.packetdebug.config.PacketDebugConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class PacketSimulatorManager {
    private final List<PacketRule<?>> rules = new ArrayList<>();

    public void registerRule(PacketRule<?> rule) {
        rules.add(rule);
    }

    public Packet<?> intercept(Packet<?> originalPacket) {
        PacketDebugConfig config = PacketDebugMod.config();
        if (!config.simulationEnabled) {
            return originalPacket;
        }

        Packet<?> current = originalPacket;
        Context context = new Context(config, Minecraft.getInstance(), ThreadLocalRandom.current());

        for (PacketRule<?> rule : rules) {
            if (!rule.packetType().isInstance(current)) {
                continue;
            }
            if (!config.isCategoryEnabled(rule.category())) {
                continue;
            }
            try {
                current = applyRule(rule, current, context);
            } catch (Exception ex) {
                // Безопасный fallback: при любой ошибке вернуть оригинальный пакет.
                return originalPacket;
            }
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    private <T extends Packet<?>> Packet<?> applyRule(PacketRule<?> rule, Packet<?> packet, Context context) {
        PacketRule<T> typedRule = (PacketRule<T>) rule;
        return typedRule.rewrite((T) packet, context);
    }

    public void setAllCategories(boolean enabled) {
        for (PacketCategory category : PacketCategory.values()) {
            PacketDebugMod.config().setCategoryEnabled(category, enabled);
        }
    }

    public record Context(PacketDebugConfig config, Minecraft client, ThreadLocalRandom random) {
    }
}
