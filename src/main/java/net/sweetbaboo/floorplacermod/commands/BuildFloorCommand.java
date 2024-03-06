package net.sweetbaboo.floorplacermod.commands;

import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.sweetbaboo.floorplacermod.BlockGenerator;
import net.sweetbaboo.floorplacermod.BlockSelector;
import net.sweetbaboo.floorplacermod.FloorPlacerMod;
import net.sweetbaboo.floorplacermod.SyncmaticaConfigAccess;
import net.sweetbaboo.floorplacermod.access.ServerPlayerEntityAccess;
import net.sweetbaboo.floorplacermod.commands.backup.BackupCommand;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BuildFloorCommand {

  private static final Gson GSON = new Gson();
  private static Map<String, String> syncmaticaPlacements;

  public static ArgumentBuilder<ServerCommandSource, ?> create() {
    return literal("buildFloor")
        .then(literal("schematic")
            .then(argument("file", StringArgumentType.word())
                    .suggests(BuildFloorCommand::suggestSchematics)
                .then(argument("width", IntegerArgumentType.integer(1))
                    .then(argument("length", IntegerArgumentType.integer(1))
                        .executes(BuildFloorCommand::loadSchematic)))))
        .then(BackupCommand.register())
        .then(literal("abort")
            .executes(BuildFloorCommand::stop))
        .then(literal("selectNextBlock")
            .executes(BuildFloorCommand::selectNextBlock))
        .then(literal("setIndex")
            .then(argument("index", IntegerArgumentType.integer(0)))
                .executes(BuildFloorCommand::setIndex));
  }

  private static int listBackups(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
    return 0;
  }

  private static int startBackup(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
    return 0;
  }

  private static int setIndex(CommandContext<ServerCommandSource> context) {
    BlockGenerator blockGenerator = BlockGenerator.getInstance();
    if (blockGenerator == null) {
      context.getSource().sendError(Text.of("BlockGenerator is null"));
      return 0;
    }
    int index = IntegerArgumentType.getInteger(context, "index");
    if (!blockGenerator.setIndex(index)) {
      context.getSource().sendError(Text.of("Index is too large"));
      return 0;
    }
    context.getSource().sendFeedback(() -> Text.of(String.format("Index set to %d", index)), false);
    return 1;
  }

  private static int selectNextBlock(CommandContext<ServerCommandSource> context) {
    ServerPlayerEntity player = getContextPlayer(context);
    return BlockSelector.selectNextBlock(player, context.getSource(), false);
  }

  private static CompletableFuture<Suggestions> suggestSchematics(CommandContext<ServerCommandSource> serverCommandSourceCommandContext, SuggestionsBuilder suggestionsBuilder) {
    return CommandSource.suggestMatching(getSchematics().keySet(), suggestionsBuilder);
  }

  private static Map<String, String> getSchematics() {
    File conf = SyncmaticaConfigAccess.getPlacementsConfig();
    if (conf == null || !conf.exists() || !conf.canRead())
      return Collections.emptyMap();
    try (FileReader reader = new FileReader(conf)) {
      Map<String, String> result = new HashMap<>();
      syncmaticaPlacements = result;
      JsonObject json = GSON.fromJson(reader, JsonObject.class);
      JsonArray placements = json.getAsJsonArray("placements");

      for (JsonElement element : placements) {
        JsonObject placement = element.getAsJsonObject();
        String hash = placement.get("hash").getAsString();
        String fileName = placement.get("file_name").getAsString();
        result.put(fileName, hash);
      }
      return result;
    } catch (Exception e) {
      FloorPlacerMod.LOGGER.error("Failed to load synced schematics", e);
      syncmaticaPlacements = Collections.emptyMap();
      return Collections.emptyMap();
    }
  }

  private static int stop(CommandContext<ServerCommandSource> context) {
    ServerPlayerEntity player = getContextPlayer(context);
    if (player == null) return -1;
    EntityPlayerActionPack actionPack=((ServerPlayerInterface) player).getActionPack();
    actionPack.stopAll();
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
    schematic = syncmaticaPlacements.get(schematic);
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
    return BlockSelector.selectNextBlock(player, context.getSource(), true);
  }

  @Nullable
  private static ServerPlayerEntity getContextPlayer(CommandContext<ServerCommandSource> context) {
    String name = StringArgumentType.getString(context, "player");
    ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(name);
    if (player == null) context.getSource().sendError(Text.of("Specified player doesn't exist"));
    return player;
  }
}