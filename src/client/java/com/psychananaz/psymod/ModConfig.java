package com.psychananaz.psymod;

import static net.minecraft.network.chat.Component.literal;

import java.nio.file.Path;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;

public final class ModConfig {

    public static final boolean DEFAULT_USE_AUTO_TOOL = false;
    public static final boolean DEFAULT_USE_AUTO_TOOL_KEYBIND = false;
    public static final ModConfig INSTANCE = new ModConfig();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("psymod.toml");
    public boolean useAutoTool = DEFAULT_USE_AUTO_TOOL;
    public boolean useAutoToolKeybind = DEFAULT_USE_AUTO_TOOL_KEYBIND;

    private ModConfig() {
    }

    public Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(literal("psymod Settings"))
                .category(ConfigCategory.createBuilder()
                        .name(literal("AutoTool"))
                        .tooltip(literal(
                                "Automatically switch to the most efficient tool when breaking blocks.").withStyle(ChatFormatting.GRAY))
                        .options(List.of(
                                Option.<Boolean>createBuilder()
                                        .name(literal("Enable AutoTool"))
                                        .description(OptionDescription.of(literal(
                                                "Whether AutoTool is enabled.")))
                                        .binding(
                                                DEFAULT_USE_AUTO_TOOL,
                                                () -> useAutoTool,
                                                value -> useAutoTool = value)
                                        .controller(option -> BooleanControllerBuilder.create(option)
                                                .onOffFormatter()
                                                .coloured(true))
                                        .build(),
                                Option.<Boolean>createBuilder()
                                        .name(literal("Enable Keybind"))
                                        .description(OptionDescription.of(literal(
                                                "Whether AutoTool can be toggled using the assigned key.")))
                                        .binding(
                                                DEFAULT_USE_AUTO_TOOL_KEYBIND,
                                                () -> useAutoToolKeybind,
                                                value -> useAutoToolKeybind = value)
                                        .controller(option -> BooleanControllerBuilder.create(option)
                                                .onOffFormatter()
                                                .coloured(true))
                                        .build(),
                                ButtonOption.createBuilder()
                                        .name(literal("Controls"))
                                        .text(literal("Configure Keybind"))
                                        .description(OptionDescription.of(literal(
                                                "Open Minecraft's Key Binds screen.")))
                                        .action((screen, button) -> {
                                            Minecraft client = Minecraft.getInstance();
                                            client.gui.setScreen(new KeyBindsScreen(screen, client.options));
                                        })
                                        .build()))
                        .build())
                .save(this::save)
                .build()
                .generateScreen(parent);
    }

    public void load() {
        try (CommentedFileConfig config = openConfig()) {
            config.load();
            Object value = config.getOrElse("useAutoTool", DEFAULT_USE_AUTO_TOOL);
            if (!(value instanceof Boolean enabled)) {
                throw new IllegalStateException("useAutoTool must be a boolean in " + CONFIG_PATH);
            }
            Object keybindValue = config.getOrElse("useAutoToolKeybind", DEFAULT_USE_AUTO_TOOL_KEYBIND);
            if (!(keybindValue instanceof Boolean keybindEnabled)) {
                throw new IllegalStateException("useAutoToolKeybind must be a boolean in " + CONFIG_PATH);
            }
            useAutoTool = enabled;
            useAutoToolKeybind = keybindEnabled;

            if (!config.contains("useAutoTool") || !config.contains("useAutoToolKeybind")) {
                write(config);
            }
        }
    }

    public void save() {
        try (CommentedFileConfig config = openConfig()) {
            // Preserve other entries and comments when saving the edited settings.
            config.load();
            write(config);
        }
    }

    private CommentedFileConfig openConfig() {
        return CommentedFileConfig.builder(CONFIG_PATH).sync().build();
    }

    private void write(CommentedFileConfig config) {
        config.set("useAutoTool", useAutoTool);
        if (config.getComment("useAutoTool") == null) {
            config.setComment("useAutoTool", " Automatically switch to the most efficient tool when breaking blocks.");
        }
        config.set("useAutoToolKeybind", useAutoToolKeybind);
        if (config.getComment("useAutoToolKeybind") == null) {
            config.setComment("useAutoToolKeybind", " Enable the AutoTool toggle key assigned in Minecraft's Controls menu.");
        }
        config.save();
    }
}
