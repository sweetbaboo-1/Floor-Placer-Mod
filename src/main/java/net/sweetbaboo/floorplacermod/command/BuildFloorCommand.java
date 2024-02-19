package net.sweetbaboo.floorplacermod.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.sweetbaboo.floorplacermod.BlockGenerator;
import net.sweetbaboo.floorplacermod.BlockSelector;
import net.sweetbaboo.floorplacermod.access.ServerPlayerEntityAccess;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BuildFloorCommand {
  public static ArgumentBuilder<ServerCommandSource, ?> create() {
    return literal("buildFloor")
        .then(literal("schematic")
            .then(argument("file", new SchematicFileArgumentType())
                .then(argument("width", IntegerArgumentType.integer(1))
                    .then(argument("length", IntegerArgumentType.integer(1))
                        .executes(BuildFloorCommand::loadSchematic)))))
        .then(literal("save")
            .executes(BuildFloorCommand::saveState))
        .then(literal("load")
            .executes(BuildFloorCommand::loadState))
        .then(literal("stop")
            .executes(BuildFloorCommand::stop));
  }

  private static int saveState(CommandContext<ServerCommandSource> context) {
    ServerPlayerEntity player = getContextPlayer(context);
    if (player == null) return -1;
    ((ServerPlayerEntityAccess) player).setBuildFloor(false);
    BlockGenerator blockGenerator = BlockGenerator.getInstance();
    return blockGenerator.saveState(context.getSource()) ? 1 : 0;
  }

  private static int loadState(CommandContext<ServerCommandSource> context) {
    ServerPlayerEntity player = getContextPlayer(context);
    if (player == null) return -1;

    ((ServerPlayerEntityAccess) player).setBuildFloor(true);
    BlockGenerator blockGenerator = BlockGenerator.getInstance();
    return blockGenerator.loadState(context.getSource()) ? 1 : 0;
  }

  private static int stop(CommandContext<ServerCommandSource> context) {
    ServerPlayerEntity player = getContextPlayer(context);
    if (player == null) return -1;

    ((ServerPlayerEntityAccess) player).setBuildFloor(false);
    BlockGenerator blockGenerator = BlockGenerator.getInstance();
    blockGenerator.reset();
    context.getSource().sendFeedback(() -> Text.of("Stopped"), false);
    return 1;
  }

  private static int loadSchematic(CommandContext<ServerCommandSource> context) {
    ServerPlayerEntity player = getContextPlayer(context);
    if (player == null) return -1;

    String schematic = context.getArgument("file", String.class);
    int floorWidth = IntegerArgumentType.getInteger(context, "width");
    int floorLength = IntegerArgumentType.getInteger(context, "length");

    BlockGenerator blockGenerator = BlockGenerator.getInstance();

    if (!blockGenerator.init(schematic, floorWidth, floorLength, context.getSource())) {
      context.getSource().sendError(Text.of(String.format("BlockGenerator.init failed\nfilename: %s\nwidth: %d\nlength: %d", schematic, floorWidth, floorLength)));
      return -1;
    } else {
      context.getSource().sendFeedback(() -> Text.of("Started %s building floor.".formatted(player.getDisplayName().getString())), false);
    }
    ((ServerPlayerEntityAccess) player).setBuildFloor(true);
    return BlockSelector.selectNextBlock(player, context.getSource());
  }

  @Nullable
  private static ServerPlayerEntity getContextPlayer(CommandContext<ServerCommandSource> context) {
    String name = StringArgumentType.getString(context, "player");
    ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(name);
    if (player == null) context.getSource().sendError(Text.of("Specified player doesn't exist"));
    return player;
  }
}