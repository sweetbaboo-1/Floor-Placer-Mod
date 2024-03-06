package net.sweetbaboo.floorplacermod.commands.backup;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.sweetbaboo.floorplacermod.BlockGenerator;

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
    return 1;
  }
}
