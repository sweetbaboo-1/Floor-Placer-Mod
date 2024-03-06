package net.sweetbaboo.floorplacermod.commands.backup;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class BackupCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("backup")
            .then(StartBackupCommand.register())
            .then(RestoreBackupCommand.register())
            .then(ListBackupsCommand.register())
            .then(DeleteCommand.register());
  }
}
