package net.sweetbaboo.floorplacermod.commands.backup;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.sweetbaboo.floorplacermod.BlockGenerator;
import org.jetbrains.annotations.Nullable;

public class StartBackupCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("start")
            .then(CommandManager.argument("comment", StringArgumentType.string())
                    .executes(ctx -> execute(ctx.getSource(), StringArgumentType.getString(ctx, "comment")))
            ).executes(ctx -> execute(ctx.getSource(), null));
  }

  private static int execute(ServerCommandSource source, @Nullable String comment) {
    if (comment == null) {
      comment = "Index=" + BlockGenerator.getInstance().getIndex();
    } else {
      comment = comment.replace(' ', '_');
    }
    BlockGenerator.getInstance().saveState(source, comment);
    return 1;
  }
}
