package net.sweetbaboo.floorplacermod.commands.backup;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.sweetbaboo.floorplacermod.BlockGenerator;

public class DeleteCommand {

  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("delete")
            .then(CommandManager.argument("file", StringArgumentType.string())
                    .suggests(FileSuggestionProvider.Instance())
                    .executes(ctx -> execute(ctx.getSource(), StringArgumentType.getString(ctx, "file")))
            );
  }

  private static int execute(ServerCommandSource source, String fileName) {
    if (BlockGenerator.getInstance().deleteBackup(source, fileName)) {
      return 1;
    }
    return 0;
  }
}
