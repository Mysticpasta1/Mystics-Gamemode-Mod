package com.mystic.gamemode.mixin;

import com.mystic.gamemode.usage.GameModeUsage;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class CreativeInventoryBooleanMixin {

    @Shadow private final GameMode gameMode;

    @Mutable @Final @Shadow private ClientPlayNetworkHandler networkHandler;

    public CreativeInventoryBooleanMixin(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Inject(at = @At("HEAD"), method = "hasCreativeInventory()Z", cancellable = true)
    private void hasCreativeMenu(CallbackInfoReturnable<Boolean> cir){
        cir.cancel();
        if(gameMode == GameModeUsage.UNLOCKABLE){
            cir.setReturnValue(true);
        }
        else if(gameMode == GameMode.CREATIVE){
            cir.setReturnValue(gameMode.isCreative());
        }
    }

    @Inject(at = @At("HEAD"), method = "hasStatusBars()Z", cancellable = true)
    private void hasStatusBars(CallbackInfoReturnable<Boolean> cir){
        cir.cancel();
        if(gameMode == GameModeUsage.UNLOCKABLE){
            cir.setReturnValue(true);
        }
        else if(gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE){
            cir.setReturnValue(gameMode.isSurvivalLike());
        }
    }


    @Inject(at = @At("HEAD"), method = "hasExperienceBar()Z", cancellable = true)
    private void hasExperienceBar(CallbackInfoReturnable<Boolean> cir){
        cir.cancel();
        if(gameMode == GameModeUsage.UNLOCKABLE){
            cir.setReturnValue(true);
        }
        else if(gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE){
            cir.setReturnValue(gameMode.isSurvivalLike());
        }
    }

    @Inject(at = @At("HEAD"), method = "dropCreativeStack(Lnet/minecraft/item/ItemStack;)V", cancellable = true)
    public void dropCreativeStack(ItemStack stack, CallbackInfo ci){
        ci.cancel();
        if (gameMode == GameModeUsage.UNLOCKABLE && !stack.isEmpty()) {
            this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, stack));
        }
        else if (this.gameMode.isCreative() && !stack.isEmpty()) {
            this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, stack));
        }
    }

    @Inject(at = @At("HEAD"), method = "clickCreativeStack(Lnet/minecraft/item/ItemStack;I)V", cancellable = true)
    public void clickCreativeStack(ItemStack stack, int slotId, CallbackInfo ci){
        ci.cancel();
        if (gameMode == GameModeUsage.UNLOCKABLE) {
            networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slotId, stack));
        }
        else if (this.gameMode.isCreative()) {
            networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slotId, stack));
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z"), method = "interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", cancellable = true)
    private void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
        ItemStack itemStack1 = player.getStackInHand(hand);
        if (gameMode == GameModeUsage.UNLOCKABLE) { //prevent item count from going down
            int i = itemStack1.getCount();
            itemStack1.setCount(i);
        }
    }

}
