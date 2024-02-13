package net.sweetbaboo.floorplacermod;

import access.ServerPlayerEntityAccess;
import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class BlockSelector {

  public static void selectNextBlock(ServerPlayerEntity player) {
    ServerPlayerEntityAccess playerAccess = (ServerPlayerEntityAccess) player;
    EntityPlayerActionPack actionPack = ((ServerPlayerInterface) player).getActionPack();

    BlockGenerator blockGenerator = BlockGenerator.getInstance();
    assert blockGenerator != null;
    String nextBlockName = blockGenerator.getNextBlockName();

    if (nextBlockName == null) {
      playerAccess.setBuildFloor(false);
      actionPack.stopAll();
      // TODO: somehow tell the player that the next block was null.
      return;
    }

    Inventory inventory = player.getInventory();
    int hotbarSlot = -1;

    for (int slot = 0; slot < 36; slot++) {
      ItemStack itemStack = inventory.getStack(slot);
      if (itemStack.getItem().toString().toLowerCase().contains(nextBlockName.toLowerCase())) {
        hotbarSlot = slot;
        break;
      }
    }

    if (hotbarSlot != -1) {
      player.getInventory().swapSlotWithHotbar(hotbarSlot);

      // The following doesn't seem to be needed but leaving here until it works on the server.
      // player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(hotbarSlot));
    } else {
      playerAccess.setBuildFloor(false);
      // TODO: somehow tell the player that they didn't have the correct block.
    }
  }
}
