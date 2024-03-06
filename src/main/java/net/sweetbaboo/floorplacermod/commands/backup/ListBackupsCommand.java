package net.sweetbaboo.floorplacermod.commands.backup;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.*;

public class ListBackupsCommand {

  public static LiteralArgumentBuilder<ServerCommandSource> register() {
    return CommandManager.literal("list")
            .executes(ctx -> { StringBuilder builder = new StringBuilder();
              var backups = RestoreHelper.getAvailableBackups(ctx.getSource().getServer());

              if(backups.size() == 0) {
                builder.append("There a no backups available.");
              } else if(backups.size() == 1) {
                builder.append("There is only one backup available: ");
                builder.append(backups.get(0));
              } else {
                backups.sort(null);
                Iterator<String> iterator = backups.iterator();
                builder.append("Available backups:\n");

                builder.append(iterator.next());

                while(iterator.hasNext()) {
                  builder.append(",\n");
                  builder.append(iterator.next());
                }
              }
              ctx.getSource().sendFeedback(() -> Text.of(builder.toString()), false);
              return 1;
            });
  }
}
