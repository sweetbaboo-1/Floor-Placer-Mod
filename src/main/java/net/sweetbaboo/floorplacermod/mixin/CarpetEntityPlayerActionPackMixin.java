package net.sweetbaboo.floorplacermod.mixin;

import access.ServerPlayerEntityAccess;
import carpet.helpers.EntityPlayerActionPack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.sweetbaboo.floorplacermod.BlockSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This is called with the /player use command. Currently, it places a block then selects the next block.
 * This has the unfortunate side effect of meaning the first block could be wrong. Not a big deal but
 * something to keep in mind.
 */
@Mixin(targets="carpet/helpers/EntityPlayerActionPack$ActionType$1")
public class CarpetEntityPlayerActionPackMixin {

  @Inject(method="execute",
          at=@At(value="INVOKE",
                  target="Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
  private void buildFloor(ServerPlayerEntity player, EntityPlayerActionPack.Action action, CallbackInfoReturnable<Boolean> cir) {
    ServerPlayerEntityAccess playerAccess = (ServerPlayerEntityAccess) player;
    if (!playerAccess.isBuildingFloor()) return;
    BlockSelector.selectNextBlock(player);
    player.closeHandledScreen();
  }
}

