package net.sweetbaboo.floorplacermod.mixin;

import access.ServerPlayerEntityAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.sweetbaboo.floorplacermod.BlockSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets="carpet/helpers/EntityPlayerActionPack$ActionType$1")
public class CarpetEntityPlayerActionPackMixin {

  @Redirect(method="execute",
          at=@At(value="INVOKE",
                  target="Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;")
  )

  // for some reason this gets called 2x per use. I don't know why, but if it ain't broke, don't fix it.
  private ActionResult redirectInteractBlock(ServerPlayerInteractionManager interactionManager, ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHit) {
    ServerPlayerEntityAccess playerAccess = (ServerPlayerEntityAccess) player;
    if (!playerAccess.isBuildingFloor()) return ActionResult.FAIL;

    // we don't support offhand
    Hand mainHand = Hand.MAIN_HAND;
    ItemStack originalStack = player.getStackInHand(mainHand);

    // this prevents the player from running out of blocks as it will only place a block that it has 2 or more of.
    if (originalStack.getCount() < 2) {
      player.getCommandSource().sendFeedback(() -> Text.of("Didn't have enough " + originalStack), false);
      return ActionResult.FAIL;
    }

    ActionResult result = player.interactionManager.interactBlock(player, player.getServerWorld(), originalStack, hand, blockHit);

    // only select the next block when once the previous block was placed.
    if (result.isAccepted()) {
      BlockSelector.selectNextBlock(player, player.getCommandSource());
      return ActionResult.PASS;
    }
    return ActionResult.FAIL;
  }
}

