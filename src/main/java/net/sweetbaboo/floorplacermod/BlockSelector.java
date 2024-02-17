package net.sweetbaboo.floorplacermod;

import access.ServerPlayerEntityAccess;
import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockSelector {
  private static final String MOD_ID = "floor-placer-mod";
  private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static int selectNextBlock(ServerPlayerEntity player, ServerCommandSource source) {
    ServerPlayerEntityAccess playerAccess=(ServerPlayerEntityAccess) player;
    EntityPlayerActionPack actionPack=((ServerPlayerInterface) player).getActionPack();

    BlockGenerator blockGenerator=BlockGenerator.getInstance();

    if (blockGenerator == null) {
      LOGGER.error("BlockSelector.selectNextBlock: blockGenerator is null");
      source.sendFeedback(() -> Text.of("BlockSelector.selectNextBlock: blockGenerator is null"), false);
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
