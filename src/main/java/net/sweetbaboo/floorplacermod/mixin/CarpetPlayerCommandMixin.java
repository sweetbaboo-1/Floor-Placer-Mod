package net.sweetbaboo.floorplacermod.mixin;

import access.ServerPlayerEntityAccess;
import carpet.commands.PlayerCommand;
import com.google.gson.*;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.sweetbaboo.floorplacermod.BlockGenerator;
import net.sweetbaboo.floorplacermod.BlockSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@Mixin(PlayerCommand.class)
public abstract class CarpetPlayerCommandMixin {
  private static final String MOD_ID="floor-placer-mod";
  private static final Logger LOGGER=LoggerFactory.getLogger(MOD_ID);
  private static List<String> fileNames;
  private static List<String> schematicNames;

  private static final String SYNCMATICA_PLACEMENT_JSON_FILEPATH="config" + File.separator + "syncmatica" + File.separator + "placements.json";

  @SuppressWarnings("unchecked")
  @ModifyExpressionValue(method="register", at=@At(value="INVOKE", target="Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;then(Lcom/mojang/brigadier/builder/ArgumentBuilder;)Lcom/mojang/brigadier/builder/ArgumentBuilder;", ordinal=1), remap=false)
  private static ArgumentBuilder<ServerCommandSource, ?> insertBuildFloorLiteral(ArgumentBuilder<ServerCommandSource, ?> original) {
    return original.then(((LiteralArgumentBuilder<ServerCommandSource>) (Object) literal("buildFloor"))
            .then(CommandManager.argument("schematic", StringArgumentType.word())
                    .suggests((c, b) -> suggestStrings(b))
                    .then(CommandManager.argument("rows", IntegerArgumentType.integer())
                            .then(CommandManager.argument("columns", IntegerArgumentType.integer())
                                    .executes(context -> {
                                      var player=getPlayer(context);
                                      String schematicName=StringArgumentType.getString(context, "schematic");
                                      String fileName=fileNames.get(schematicName.indexOf(schematicName));
                                      int rows=IntegerArgumentType.getInteger(context, "rows");
                                      int columns=IntegerArgumentType.getInteger(context, "columns");

                                      if (rows <= 0) {
                                        context.getSource().sendFeedback(() -> Text.of("Rows must be >= 1"), false);
                                        return 1;
                                      }
                                      if (columns <= 0) {
                                        context.getSource().sendFeedback(() -> Text.of("Columns must be >= 1"), false);
                                        return 1;
                                      }

                                      BlockGenerator blockGenerator=BlockGenerator.getInstance();

                                      ((ServerPlayerEntityAccess) player).setBuildFloor(true);

                                      if (!blockGenerator.init(fileName, rows, columns, context.getSource())) {
                                        LOGGER.error(String.format("BlockGenerator.init failed\nfilename: %s\nrows: %d\ncols: %d", fileName, rows, columns));
                                        context.getSource().sendFeedback(() -> Text.of(String.format("BlockGenerator.init failed\nfilename: %s\nrows: %d\ncols: %d", fileName, rows, columns)), false);
                                        return 0;
                                      } else {
                                        context.getSource().sendFeedback(() -> Text.of("Started " + player.getDisplayName().getString() + " building floor."), false);
                                      }

                                      if (BlockSelector.selectNextBlock(player, context.getSource()) == 0) {
                                        return 0;
                                      }
                                      return 1;
                                    })
                            )
                    )
            )
            .then(((LiteralArgumentBuilder<ServerCommandSource>) (Object) literal("saveState"))
                    .executes(context -> {
                      var player=getPlayer(context);
                      ((ServerPlayerEntityAccess) player).setBuildFloor(false);
                      BlockGenerator blockGenerator=BlockGenerator.getInstance();

                      if (!blockGenerator.saveState(context.getSource())) return 0;
                      return 1;
                    })
            )
            .then(((LiteralArgumentBuilder<ServerCommandSource>) (Object) literal("loadState"))
                    .executes(context -> {
                      var player=getPlayer(context);
                      ((ServerPlayerEntityAccess) player).setBuildFloor(true);
                      BlockGenerator blockGenerator=BlockGenerator.getInstance();

                      if (!blockGenerator.loadState(context.getSource())) return 0;
                      return 1;
                    })
            )
            .then(((LiteralArgumentBuilder<ServerCommandSource>) (Object) literal("stop"))
                    .executes(context -> {
                      var player=getPlayer(context);
                      ((ServerPlayerEntityAccess) player).setBuildFloor(false);
                      BlockGenerator blockGenerator=BlockGenerator.getInstance();
                      blockGenerator.reset();
                      context.getSource().sendFeedback(() -> Text.of("Stopped"), false);
                      return 1;
                    })
            )
    );
  }

  // make the suggestions as to which schematics could be built
  private static CompletableFuture<Suggestions> suggestStrings(SuggestionsBuilder builder) {
    generateSuggestionList();
    String remaining=builder.getRemaining().toLowerCase();
    schematicNames.stream()
            .filter(suggestion -> suggestion.toLowerCase().startsWith(remaining))
            .forEach(builder::suggest);
    return builder.buildFuture();
  }

  private static void generateSuggestionList() {
    fileNames=new ArrayList<>();
    schematicNames=new ArrayList<>();

    Gson gson=new Gson();
    try (BufferedReader reader=new BufferedReader(new FileReader(SYNCMATICA_PLACEMENT_JSON_FILEPATH))) {
      JsonObject jsonObject=gson.fromJson(reader, JsonObject.class);
      JsonArray placements=jsonObject.getAsJsonArray("placements");

      for (JsonElement element : placements) {
        JsonObject placement=element.getAsJsonObject();
        String hash=placement.get("hash").getAsString();
        String fileName=placement.get("file_name").getAsString();

        fileNames.add(hash + ".litematic");
        schematicNames.add(fileName.replaceAll("[^a-zA-Z]", ""));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Unique
  private static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context) {
    String playerName=StringArgumentType.getString(context, "player");
    MinecraftServer server=context.getSource().getServer();
    return server.getPlayerManager().getPlayer(playerName);
  }
}
