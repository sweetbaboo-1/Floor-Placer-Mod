package net.sweetbaboo.floorplacermod.mixin;

import access.ServerPlayerEntityAccess;
import carpet.commands.PlayerCommand;
import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
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
import net.sweetbaboo.floorplacermod.LitematicaLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@Mixin(PlayerCommand.class)
public abstract class CarpetPlayerCommandMixin {

  private static List<String> fileNames;
  private static final String SCHEMATIC_FOLDER="resources\\schematics\\";

  @SuppressWarnings("unchecked")
  @ModifyExpressionValue(method="register", at=@At(value="INVOKE", target="Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;then(Lcom/mojang/brigadier/builder/ArgumentBuilder;)Lcom/mojang/brigadier/builder/ArgumentBuilder;", ordinal=1), remap=false)
  private static ArgumentBuilder<ServerCommandSource, ?> insertBuildFloorLiteral(ArgumentBuilder<ServerCommandSource, ?> original) {
    return original.then(((LiteralArgumentBuilder<ServerCommandSource>) (Object) literal("buildFloor"))
            .then(CommandManager.argument("filename", StringArgumentType.word())
                    .suggests((c, b) -> suggestStrings(b))
                    .then(CommandManager.argument("rows", IntegerArgumentType.integer())
                            .then(CommandManager.argument("columns", IntegerArgumentType.integer())
                                    .executes(context -> {
                                      var player=getPlayer(context);
                                      String fileName=StringArgumentType.getString(context, "filename");
                                      int rows=IntegerArgumentType.getInteger(context, "rows");
                                      int columns=IntegerArgumentType.getInteger(context, "columns");
                                      ((ServerPlayerEntityAccess) player).setBuildFloor(true);
                                      context.getSource().sendFeedback(() -> Text.of("Started " + player.getDisplayName().getString() + " building floor."), false);
                                      BlockGenerator.getInstance(LitematicaLoader.loadLitematicaFile(fileName), rows, columns);
                                      return 1;
                                    })
                            )
                    )
            )
            .then(((LiteralArgumentBuilder<ServerCommandSource>) (Object) literal("stop"))
                    .executes(context -> {
                      var player=getPlayer(context);
                      ((ServerPlayerEntityAccess) player).setBuildFloor(false);
                      BlockGenerator.getInstance().reset();
                      context.getSource().sendFeedback(() -> Text.of("Stopped " + player.getDisplayName().getString() + " building floor"), false);
                      return 1;
                    }))
    );
  }

  //  int rows = IntegerArgumentType.getInteger(context, "rows");
//  int columns = IntegerArgumentType.getInteger(context, "columns");
  private static CompletableFuture<Suggestions> suggestStrings(SuggestionsBuilder builder) {
    generateSuggestionList(); // Ensure fileNames list is populated
    String remaining=builder.getRemaining().toLowerCase(); // Get the remaining text typed by the user
    // Iterate through the suggestion list and add matching suggestions to the builder
    fileNames.stream()
            .filter(suggestion -> suggestion.toLowerCase().startsWith(remaining))
            .forEach(builder::suggest);
    return builder.buildFuture(); // Build and return the suggestions
  }


  private static void generateSuggestionList() {
    fileNames=new ArrayList<>();
    File directory=new File(SCHEMATIC_FOLDER);
    File[] files=directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isFile()) {
          fileNames.add(file.getName());
        }
      }
    }
  }

  @Unique
  private static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context) {
    String playerName=StringArgumentType.getString(context, "player");
    MinecraftServer server=context.getSource().getServer();
    return server.getPlayerManager().getPlayer(playerName);
  }

}