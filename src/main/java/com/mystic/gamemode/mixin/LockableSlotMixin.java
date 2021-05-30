package com.mystic.gamemode.mixin;

import com.mystic.gamemode.usage.GameModeUsage;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeInventoryScreen.LockableSlot.class)
public class LockableSlotMixin {

    @Inject(at = @At("RETURN"), method = "canTakeItems", cancellable = true)
    public void canTakeItems(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> infoReturnable) {
        infoReturnable.cancel();

        if (GameModeUsage.getGameMode() == GameModeUsage.UNLOCKABLE) {

            infoReturnable.setReturnValue(true);

        } /*else {

            infoReturnable.setReturnValue(true);
        }*/
    }
}

