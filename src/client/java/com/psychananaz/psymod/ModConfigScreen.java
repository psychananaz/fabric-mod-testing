package com.psychananaz.psymod;

import static net.minecraft.network.chat.Component.literal;

import java.util.List;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.TextColor;

public final class ModConfigScreen {
    private ModConfigScreen() {
    }

    public static Screen createGui(Screen parent) {
        ModConfig config = ModConfig.INSTANCE;

        return YetAnotherConfigLib.createBuilder()
                .title(literal("psymod Settings"))
                .category(ConfigCategory.createBuilder()
                        .name(literal("General"))
                        .tooltip(literal("Settings that apply to the entire mod."))
                        .options(List.of(
                                booleanOption("Enable Mod",
                                        "Pause mod features without changing their individual settings.",
                                        ModConfig.DEFAULT_ENABLED, () -> config.enabled, value -> config.enabled = value),
                                booleanOption("Show Notifications",
                                        "Show brief on-screen confirmations when settings are saved.",
                                        ModConfig.DEFAULT_SHOW_NOTIFICATIONS,
                                        () -> config.showNotifications, value -> config.showNotifications = value),
                                booleanOption("Verbose Logging",
                                        "Write additional diagnostic details to the game log.",
                                        ModConfig.DEFAULT_VERBOSE_LOGGING,
                                        () -> config.verboseLogging, value -> config.verboseLogging = value)))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(literal("AutoTool"))
                        .tooltip(literal("Automatically switch to the most efficient tool when breaking blocks.")
                                .withStyle(ChatFormatting.GRAY))
                        .options(List.of(
                                booleanOption("Enabled", "Whether AutoTool is enabled.",
                                        ModConfig.DEFAULT_USE_AUTO_TOOL,
                                        () -> config.useAutoTool, value -> config.useAutoTool = value),
                                booleanOption("Bypass", "Prevents tool switching when holding the configured key.",
                                        ModConfig.DEFAULT_ENABLE_BYPASS,
                                        () -> config.enableBypass, value -> config.enableBypass = value),
                                ButtonOption.createBuilder()
                                        .name(literal("Controls"))
                                        .text(literal("Open Key Binds").withColor(TextColor.GRAY))
                                        .description(OptionDescription.of(literal("Open Minecraft's Key Binds screen.")))
                                        .action((screen, button) -> {
                                            Minecraft client = Minecraft.getInstance();
                                            client.gui.setScreen(new KeyBindsScreen(screen, client.options));
                                        })
                                        .build()))
                        .build())
                .save(() -> {
                    config.save();
                    PsyModClient.showNotification(literal("psymod settings saved."));
                })
                .build()
                .generateScreen(parent);
    }

    private static Option<Boolean> booleanOption(String name, String description, boolean defaultValue,
            java.util.function.Supplier<Boolean> getter, java.util.function.Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(literal(name))
                .description(OptionDescription.of(literal(description)))
                .binding(defaultValue, getter, setter)
                .controller(option -> BooleanControllerBuilder.create(option)
                        .onOffFormatter()
                        .coloured(true))
                .build();
    }
}
