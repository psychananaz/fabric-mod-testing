package com.psychananaz.psymod.feature.autotool;

import com.mojang.blaze3d.platform.InputConstants;
import com.psychananaz.psymod.ModHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class EfficientToolSwitcher {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(ModHelper.id("autotool"));
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private static final int HOTBAR_SIZE = 9;
    private static final float MINIMUM_TOOL_SPEED = 1.0f;

    private static KeyMapping toggleKey;
    private static boolean enabled;

    private EfficientToolSwitcher() {
    }

    public static void initialize() {
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.psymod.autotool_toggle",
                InputConstants.Type.KEYSYM, InputConstants.KEY_G, CATEGORY));

        ClientTickEvents.START_CLIENT_TICK.register((_) -> tick());
    }

    private static void tick() {
        final LocalPlayer player = CLIENT.player;
        final ClientLevel level = CLIENT.level;

        if (level == null || player == null || player.isSpectator() || player.isCreative()) {
            return;
        }

        if (toggleKey.consumeClick()) {
            enabled = !enabled;
            player.sendOverlayMessage(Component.literal(enabled ? "Auto tool enabled" : "Auto tool disabled").withColor(enabled ? TextColor.GREEN : TextColor.RED));

        }

        if (!enabled || !CLIENT.options.keyAttack.isDown() || CLIENT.gameMode == null) {
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
