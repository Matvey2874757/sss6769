package dev.packetdebug.gui;

import dev.packetdebug.config.PacketCategory;
import dev.packetdebug.config.PacketDebugConfig;
import dev.packetdebug.preset.DefaultPresets;
import dev.packetdebug.preset.SimulationPreset;
import dev.packetdebug.sim.PacketSenders;
import dev.packetdebug.sim.PacketSimulatorManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Map;

public final class PacketDebugScreen extends Screen {
    private final PacketSimulatorManager manager;
    private final PacketDebugConfig config;

    private EditBox xField;
    private EditBox yField;
    private EditBox zField;
    private EditBox yawField;
    private EditBox pitchField;
    private EditBox chatField;

    public PacketDebugScreen(PacketSimulatorManager manager, PacketDebugConfig config) {
        super(Component.literal("Packet Simulator & Debugger"));
        this.manager = manager;
        this.config = config;
    }

    @Override
    protected void init() {
        int left = this.width / 2 - 155;
        int top = this.height / 2 - 90;

        this.addRenderableWidget(Button.builder(toggleLabel(), b -> {
            config.simulationEnabled = !config.simulationEnabled;
            b.setMessage(toggleLabel());
        }).pos(left, top).size(150, 20).build());

        this.addRenderableWidget(Button.builder(categoryLabel(PacketCategory.MOVEMENT), b -> toggleCategory(PacketCategory.MOVEMENT, b))
                .pos(left + 160, top).size(150, 20).build());
        this.addRenderableWidget(Button.builder(categoryLabel(PacketCategory.BLOCK_ACTIONS), b -> toggleCategory(PacketCategory.BLOCK_ACTIONS, b))
                .pos(left, top + 160).size(150, 20).build());
        this.addRenderableWidget(Button.builder(categoryLabel(PacketCategory.ITEMS), b -> toggleCategory(PacketCategory.ITEMS, b))
                .pos(left + 160, top + 160).size(150, 20).build());
        this.addRenderableWidget(Button.builder(categoryLabel(PacketCategory.CHAT), b -> toggleCategory(PacketCategory.CHAT, b))
                .pos(left, top + 184).size(150, 20).build());
        this.addRenderableWidget(Button.builder(categoryLabel(PacketCategory.MISC), b -> toggleCategory(PacketCategory.MISC, b))
                .pos(left + 160, top + 184).size(150, 20).build());

        xField = numericField(left, top + 30, String.valueOf(config.movementX));
        yField = numericField(left + 104, top + 30, String.valueOf(config.movementY));
        zField = numericField(left + 208, top + 30, String.valueOf(config.movementZ));
        yawField = numericField(left, top + 56, String.valueOf(config.yaw));
        pitchField = numericField(left + 104, top + 56, String.valueOf(config.pitch));

        chatField = new EditBox(this.font, left, top + 82, 310, 20, Component.literal("Chat"));
        chatField.setValue(config.chatMessage);
        this.addRenderableWidget(chatField);

        this.addRenderableWidget(Button.builder(Component.literal("Send movement"), b -> {
            saveFields();
            PacketSenders.sendMovement(config);
        }).pos(left, top + 108).size(100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Send block action"), b -> {
            saveFields();
            PacketSenders.sendBlockAction(config);
        }).pos(left + 105, top + 108).size(100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Use item"), b -> PacketSenders.sendUseItem(config))
                .pos(left + 210, top + 108).size(100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Send chat"), b -> {
            saveFields();
            PacketSenders.sendChat(config);
        }).pos(left, top + 134).size(100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Live emulation: " + yesNo(config.liveEmulation)), b -> {
            config.liveEmulation = !config.liveEmulation;
            b.setMessage(Component.literal("Live emulation: " + yesNo(config.liveEmulation)));
        }).pos(left + 105, top + 134).size(205, 20).build());

        int offset = 210;
        for (Map.Entry<String, SimulationPreset> entry : DefaultPresets.presets().entrySet()) {
            String presetName = entry.getKey();
            SimulationPreset preset = entry.getValue();
            this.addRenderableWidget(Button.builder(Component.literal(presetName), b -> {
                preset.apply(manager, config);
                loadFields();
            }).pos(left, top + offset).size(310, 20).build());
            offset += 24;
        }
    }

    private EditBox numericField(int x, int y, String value) {
        EditBox box = new EditBox(this.font, x, y, 100, 20, Component.empty());
        box.setValue(value);
        this.addRenderableWidget(box);
        return box;
    }

    private void toggleCategory(PacketCategory category, Button button) {
        boolean current = config.isCategoryEnabled(category);
        config.setCategoryEnabled(category, !current);
        button.setMessage(categoryLabel(category));
    }

    private Component toggleLabel() {
        return Component.literal("Simulation: " + yesNo(config.simulationEnabled));
    }

    private Component categoryLabel(PacketCategory category) {
        return Component.literal(category.name() + ": " + yesNo(config.isCategoryEnabled(category)));
    }

    private String yesNo(boolean value) {
        return value ? "ON" : "OFF";
    }

    private void loadFields() {
        xField.setValue(String.valueOf(config.movementX));
        yField.setValue(String.valueOf(config.movementY));
        zField.setValue(String.valueOf(config.movementZ));
        yawField.setValue(String.valueOf(config.yaw));
        pitchField.setValue(String.valueOf(config.pitch));
        chatField.setValue(config.chatMessage);
    }

    private void saveFields() {
        config.movementX = config.clampCoord(parseDouble(xField.getValue(), config.movementX));
        config.movementY = config.clampCoord(parseDouble(yField.getValue(), config.movementY));
        config.movementZ = config.clampCoord(parseDouble(zField.getValue(), config.movementZ));
        config.yaw = config.clampRotation((float) parseDouble(yawField.getValue(), config.yaw));
        config.pitch = config.clampRotation((float) parseDouble(pitchField.getValue(), config.pitch));
        config.chatMessage = chatField.getValue();
    }

    private double parseDouble(String raw, double fallback) {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 104, 0xFFFFFF);
        guiGraphics.drawString(this.font, "X", this.width / 2 - 155, this.height / 2 - 70, 0xCCCCCC);
        guiGraphics.drawString(this.font, "Y", this.width / 2 - 51, this.height / 2 - 70, 0xCCCCCC);
        guiGraphics.drawString(this.font, "Z", this.width / 2 + 53, this.height / 2 - 70, 0xCCCCCC);
        guiGraphics.drawString(this.font, "Yaw", this.width / 2 - 155, this.height / 2 - 44, 0xCCCCCC);
        guiGraphics.drawString(this.font, "Pitch", this.width / 2 - 51, this.height / 2 - 44, 0xCCCCCC);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        saveFields();
        super.onClose();
    }
}
