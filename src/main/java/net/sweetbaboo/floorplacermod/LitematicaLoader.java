package net.sweetbaboo.floorplacermod;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.sandrohc.schematic4j.SchematicLoader;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class LitematicaLoader {
  private static final Path SYNCMATICA_FOLDER= FabricLoader.getInstance().getGameDir().resolve("syncmatics");

  @Nullable
  public static Schematic loadLitematicaFile(String filename, ServerCommandSource source) {
    Schematic schematic;
    try {
      schematic=SchematicLoader.load(SYNCMATICA_FOLDER.resolve(filename));
      source.sendFeedback(() -> Text.of("Loaded schematic"), false);
      return schematic;
    } catch (ParsingException | IOException e) {
      FloorPlacerMod.LOGGER.error("Failed to load schematic: %s".formatted(filename), e);
      source.sendError(Text.of("Failed to load schematic"));
    }
    return null;
  }
}

