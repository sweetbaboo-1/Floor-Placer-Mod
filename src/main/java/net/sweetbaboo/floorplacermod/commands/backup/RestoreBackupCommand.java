package net.sweetbaboo.floorplacermod.commands.backup;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.sweetbaboo.floorplacermod.BlockGenerator;
import net.sweetbaboo.floorplacermod.BlockSelector;
import net.sweetbaboo.floorplacermod.access.ServerPlayerEntityAccess;

public class RestoreBackupCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("restore")
            .then(CommandManager.argument("file", StringArgumentType.string())
                    .suggests(FileSuggestionProvider.Instance())
                    .executes(ctx -> execute(
                            StringArgumentType.getString(ctx, "file"),
                            ctx.getSource()
                    ))
            );
  }

  private static int execute(String file, ServerCommandSource source) {
    BlockGenerator.getInstance().loadState(source, file);
    ServerPlayerEntity player = source.getPlayer();
    if (player != null) {
      ((ServerPlayerEntityAccess) player).setBuildFloor(true);
      BlockSelector.selectNextBlock(player, source, false);
      source.sendFeedback(() -> Text.of("Correct block selected"), false);
    } else {
      source.sendError(Text.of("Player is null"));
    }
    return 1;
  }
}
