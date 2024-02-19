package net.sweetbaboo.floorplacermod;

import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.sweetbaboo.floorplacermod.access.ServerPlayerEntityAccess;

public class BlockSelector {
  public static int selectNextBlock(ServerPlayerEntity player, ServerCommandSource source) {
    ServerPlayerEntityAccess playerAccess=(ServerPlayerEntityAccess) player;
    EntityPlayerActionPack actionPack=((ServerPlayerInterface) player).getActionPack();

    BlockGenerator blockGenerator=BlockGenerator.getInstance();

    if (blockGenerator == null) {
      FloorPlacerMod.LOGGER.error("BlockSelector.selectNextBlock: blockGenerator is null");
      source.sendError(Text.of("BlockSelector.selectNextBlock: blockGenerator is null"));
      return 0;
    }

    String nextBlockName=blockGenerator.getNextBlockName();

    if (nextBlockName == null) {
      playerAccess.setBuildFloor(false);
      actionPack.stopAll();
      blockGenerator.reset();
      source.sendFeedback(() -> Text.of(String.format("Finished building %s", blockGenerator.getTileName())), false);
      return 1;
    }

    Inventory inventory=player.getInventory();
    int hotbarSlot=-1;

    for (int slot=0; slot < 36; slot++) {
      ItemStack itemStack=inventory.getStack(slot);
      if (itemStack.getItem().toString().equalsIgnoreCase(nextBlockName)) {
        hotbarSlot=slot;
        break;
      }
    }

    if (hotbarSlot != -1) {
      player.getInventory().swapSlotWithHotbar(hotbarSlot);
      return 1;
    }
    return 0;
  }
}
