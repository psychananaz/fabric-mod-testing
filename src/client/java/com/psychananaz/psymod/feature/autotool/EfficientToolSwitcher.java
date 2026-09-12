package com.psychananaz.psymod.feature.autotool;

import com.psychananaz.psymod.ModConfig;
import com.psychananaz.psymod.PsyMod;
import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class EfficientToolSwitcher {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private static final int HOTBAR_SIZE = 9;
    private static final float MINIMUM_TOOL_SPEED = 1.0f;
    private static KeyMapping toggleKey;
    private static boolean toggleKeyWasDown;

    private static boolean isEnabled() {
        return ModConfig.INSTANCE.useAutoTool;
    }

    public static void initialize() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(PsyMod.MOD_ID, "autotool"));
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.psymod.autotool_toggle", InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(), category));
        ClientTickEvents.START_CLIENT_TICK.register((_) -> tick());
    }

    private static void handleKeyMapping() {
        boolean clicked = false;
        // Always drain clicks, including while disabled or in a screen.
        while (toggleKey.consumeClick()) {
            clicked = true;
        }

        if (clicked && !toggleKeyWasDown && ModConfig.INSTANCE.useAutoToolKeybind
                && CLIENT.player != null && CLIENT.gui.screen() == null) {
            ModConfig.INSTANCE.useAutoTool = !ModConfig.INSTANCE.useAutoTool;
            ModConfig.INSTANCE.save();
            PsyMod.logVerbose("AutoTool toggled {} using its keybind", ModConfig.INSTANCE.useAutoTool ? "on" : "off");
        }
        toggleKeyWasDown = toggleKey.isDown();
    }

    private static void tick() {
        handleKeyMapping();
        final LocalPlayer player = CLIENT.player;
        final ClientLevel level = CLIENT.level;

        if (level == null || player == null || player.isSpectator() || player.isCreative()) {
            return;
        }

        if (!isEnabled() || !CLIENT.options.keyAttack.isDown() || CLIENT.gameMode == null) {
            return;
        }

        final HitResult hitResult = CLIENT.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        selectBestTool(player, level, ((BlockHitResult) hitResult).getBlockPos());
    }

    private static void selectBestTool(LocalPlayer player, ClientLevel level, BlockPos blockPos) {
        final BlockState blockState = level.getBlockState(blockPos);
        int bestTool = -1;
        float bestSpeed = player.getInventory().getSelectedItem().getDestroySpeed(blockState);

        final Holder<Enchantment> efficiencyHolder = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.EFFICIENCY);

        for (int slot = 0; slot < HOTBAR_SIZE; slot++) {
            final ItemStack stack = player.getInventory().getItem(slot);
            final int efficiencyLevel = EnchantmentHelper.getItemEnchantmentLevel(efficiencyHolder, stack);
            final int miningEfficiency = efficiencyLevel * efficiencyLevel + 1;
            float currentSpeed = stack.getDestroySpeed(blockState);

            if (currentSpeed <= MINIMUM_TOOL_SPEED) {
                continue;
            }

            currentSpeed *= miningEfficiency;

            if (currentSpeed > bestSpeed && stack.canDestroyBlock(blockState, level, blockPos, player)) {
                bestSpeed = currentSpeed;
                bestTool = slot;
            }
        }

        if (bestTool != -1 && bestSpeed > MINIMUM_TOOL_SPEED) {
            player.getInventory().setSelectedSlot(bestTool);
        }
    }
}
