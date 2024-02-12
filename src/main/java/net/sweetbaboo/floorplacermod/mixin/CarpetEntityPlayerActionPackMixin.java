package net.sweetbaboo.floorplacermod.mixin;

import access.ServerPlayerEntityAccess;
import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.sweetbaboo.floorplacermod.BlockGenerator;
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

    EntityPlayerActionPack actionPack = ((ServerPlayerInterface) player).getActionPack();

    BlockGenerator blockGenerator = BlockGenerator.getInstance();
    assert blockGenerator != null;
    String nextBlockName = blockGenerator.getNextBlockName();

    if (nextBlockName == null) {
      playerAccess.setBuildFloor(false);
      // TODO: somehow tell the player that the next block was null.
      return;
    }

    Inventory inventory = player.getInventory();
    int hotbarSlot = -1;

    for (int slot = 0; slot < 9; slot++) {
      ItemStack itemStack = inventory.getStack(slot);
      if (itemStack.getItem().toString().contains(nextBlockName.toLowerCase())) {
        hotbarSlot = slot;
        break;
      }
    }

    if (hotbarSlot != -1) {
      actionPack.setSlot(hotbarSlot + 1);

      // The following doesn't seem to be needed but leaving here until it works on the server.
      // player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(hotbarSlot));
    } else {
      playerAccess.setBuildFloor(false);
      // TODO: somehow tell the player that they didn't have the correct block.
    }
    player.closeHandledScreen();
  }
}

