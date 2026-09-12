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
import net.minecraft.network.chat.TextColor;

public final class ModConfig {

    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_SHOW_NOTIFICATIONS = true;
    public static final boolean DEFAULT_VERBOSE_LOGGING = false;
    public static final boolean DEFAULT_USE_AUTO_TOOL = false;
    public static final boolean DEFAULT_ENABLE_BYPASS = false;
    public static final ModConfig INSTANCE = new ModConfig();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("psymod.toml");

    public boolean enabled = DEFAULT_ENABLED;
    public boolean showNotifications = DEFAULT_SHOW_NOTIFICATIONS;
    public boolean verboseLogging = DEFAULT_VERBOSE_LOGGING;
    public boolean useAutoTool = DEFAULT_USE_AUTO_TOOL;
    public boolean enableBypass = DEFAULT_ENABLE_BYPASS;

    private ModConfig() {
    }

    public Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(literal("psymod Settings"))
                .category(ConfigCategory.createBuilder()
                        .name(literal("General"))
                        .tooltip(literal("Settings that apply to the entire mod."))
                        .options(List.of(
                                Option.<Boolean>createBuilder()
                                        .name(literal("Enable Mod"))
                                        .description(OptionDescription.of(literal(
                                                "Pause mod features without changing their individual settings.")))
                                        .binding(
                                                DEFAULT_ENABLED,
                                                () -> enabled,
                                                value -> enabled = value)
                                        .controller(option -> BooleanControllerBuilder.create(option)
                                                .onOffFormatter()
                                                .coloured(true))
                                        .build(),
                                Option.<Boolean>createBuilder()
                                        .name(literal("Show Notifications"))
                                        .description(OptionDescription.of(literal(
                                                "Show brief on-screen confirmations when settings are saved.")))
                                        .binding(
                                                DEFAULT_SHOW_NOTIFICATIONS,
                                                () -> showNotifications,
                                                value -> showNotifications = value)
                                        .controller(option -> BooleanControllerBuilder.create(option)
                                                .onOffFormatter()
                                                .coloured(true))
                                        .build(),
                                Option.<Boolean>createBuilder()
                                        .name(literal("Verbose Logging"))
                                        .description(OptionDescription.of(literal(
                                                "Write additional diagnostic details to the game log.")))
                                        .binding(
                                                DEFAULT_VERBOSE_LOGGING,
                                                () -> verboseLogging,
                                                value -> verboseLogging = value)
                                        .controller(option -> BooleanControllerBuilder.create(option)
                                                .onOffFormatter()
                                                .coloured(true))
                                        .build()))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(literal("AutoTool"))
                        .tooltip(literal(
                                "Automatically switch to the most efficient tool when breaking blocks.").withStyle(ChatFormatting.GRAY))
                        .options(List.of(
                                Option.<Boolean>createBuilder()
                                        .name(literal("Enabled"))
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
                                        .name(literal("Bypass"))
                                        .description(OptionDescription.of(literal(
                                                "Prevents tool switching when holding the configured key.")))
                                        .binding(
                                                DEFAULT_ENABLE_BYPASS,
                                                () -> enableBypass,
                                                value -> enableBypass = value)
                                        .controller(option -> BooleanControllerBuilder.create(option)
                                                .onOffFormatter()
                                                .coloured(true))
                                        .build(),
                                ButtonOption.createBuilder()
                                        .name(literal("Controls"))
                                        .text(literal("Open Key Binds").withColor(TextColor.GRAY))
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
            enabled = readBoolean(config, "general.enabled", DEFAULT_ENABLED);
            showNotifications = readBoolean(config, "general.showNotifications", DEFAULT_SHOW_NOTIFICATIONS);
            verboseLogging = readBoolean(config, "general.verboseLogging", DEFAULT_VERBOSE_LOGGING);
            useAutoTool = readBoolean(config, "useAutoTool", DEFAULT_USE_AUTO_TOOL);
            enableBypass = readBoolean(config, "enableBypass", DEFAULT_ENABLE_BYPASS);

            if (!config.contains("general.enabled") || !config.contains("general.showNotifications")
                    || !config.contains("general.verboseLogging") || !config.contains("useAutoTool")
                    || !config.contains("enableBypass")) {
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
        PsyModClient.showNotification(literal("psymod settings saved."));
    }

    private CommentedFileConfig openConfig() {
        return CommentedFileConfig.builder(CONFIG_PATH).sync().build();
    }

    private void write(CommentedFileConfig config) {
        config.set("general.enabled", enabled);
        if (config.getComment("general.enabled") == null) {
            config.setComment("general.enabled", " Enable mod features without changing their individual settings.");
        }
        config.set("general.showNotifications", showNotifications);
        if (config.getComment("general.showNotifications") == null) {
            config.setComment("general.showNotifications", " Show brief on-screen confirmations when settings are saved.");
        }
        config.set("general.verboseLogging", verboseLogging);
        if (config.getComment("general.verboseLogging") == null) {
            config.setComment("general.verboseLogging", " Log additional diagnostic details for all mod features.");
        }

        config.set("useAutoTool", useAutoTool);
        if (config.getComment("useAutoTool") == null) {
            config.setComment("useAutoTool", " Automatically switch to the most efficient tool when breaking blocks.");
        }

        config.set("enableBypass", enableBypass);
        if (config.getComment("enableBypass") == null) {
            config.setComment("enableBypass", " Enable bypass functionality.");
        }

        config.save();
    }

    private boolean readBoolean(CommentedFileConfig config, String key, boolean defaultValue) {
        Object value = config.getOrElse(key, defaultValue);
        if (!(value instanceof Boolean setting)) {
            throw new IllegalStateException(key + " must be a boolean in " + CONFIG_PATH);
        }
        return setting;
    }
}
