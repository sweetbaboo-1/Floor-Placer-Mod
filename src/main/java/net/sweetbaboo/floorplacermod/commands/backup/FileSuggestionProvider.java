package net.sweetbaboo.floorplacermod.commands.backup;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public final class FileSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
  private static final FileSuggestionProvider INSTANCE = new FileSuggestionProvider();

  public static FileSuggestionProvider Instance() { return INSTANCE; }

  @Override
  public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
    var files = RestoreHelper.getAvailableBackups(ctx.getSource().getServer());
    return CommandSource.suggestMatching(files, builder);
  }
}
