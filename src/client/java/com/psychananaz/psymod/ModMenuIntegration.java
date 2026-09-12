package com.psychananaz.psymod;

import static net.minecraft.network.chat.Component.literal;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(literal("Efficient Tool Switcher"))
                .category(ConfigCategory.createBuilder()
                        .name(literal("General"))
                        .tooltip(literal(
                                "Automatically switches to the most efficient tool for the block you are breaking."))
                        .option(Option.<Boolean>createBuilder()
                                .name(literal("Enable Efficient Tool Switching"))
                                .binding(
                                        ModConfig.DEFAULT_USE_AUTO_TOOL,
                                        () -> ModConfig.INSTANCE.useAutoTool,
                                        value -> ModConfig.INSTANCE.useAutoTool = value)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())
                .save(ModConfig.INSTANCE::save)
                .build()
                .generateScreen(parentScreen);
    }
}
