package com.psychananaz.psymod;

import static net.minecraft.network.chat.Component.literal;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

public final class ModConfig {

    public static final boolean DEFAULT_USE_AUTO_TOOL = false;
    public static final ModConfig INSTANCE = new ModConfig();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("psymod.toml");
    public boolean useAutoTool = DEFAULT_USE_AUTO_TOOL;

    private ModConfig() {
    }

    public Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(literal("psymod Settings"))
                .category(ConfigCategory.createBuilder()
                        .name(literal("psymod"))
                        .option(Option.<Boolean>createBuilder()
                                .name(literal("Auto Tool"))
                                .description(OptionDescription.of(literal(
                                        "Automatically select the best tool in your hotbar while mining.")))
                                .binding(
                                        DEFAULT_USE_AUTO_TOOL,
                                        () -> useAutoTool,
                                        value -> useAutoTool = value)
                                .controller(option -> BooleanControllerBuilder.create(option)
                                        .onOffFormatter()
                                        .coloured(false))
                                .build())
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
            useAutoTool = enabled;

            if (!config.contains("useAutoTool")) {
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
            config.setComment("useAutoTool", "Automatically switch to the most efficient tool when breaking blocks.");
        }
        config.save();
    }
}
