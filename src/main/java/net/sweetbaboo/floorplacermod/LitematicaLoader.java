package net.sweetbaboo.floorplacermod;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.sandrohc.schematic4j.SchematicLoader;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class LitematicaLoader {
  private static final String SYNCMATICA_FOLDER="syncmatics" + File.separator;
  private static final String MOD_ID = "floor-placer-mod";
  private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static Schematic loadLitematicaFile(String filename, ServerCommandSource source) {
    final String filePath=SYNCMATICA_FOLDER + filename;
    Schematic schematic;
    try {
      schematic=SchematicLoader.load(filePath);
      source.sendFeedback(() -> Text.of("Loaded schematic"), false);
      return schematic;
    } catch (ParsingException | IOException e) {
      LOGGER.error("Failed to load schematic: " + filename);
      source.sendFeedback(() -> Text.of("Failed to load schematic"), false);

      e.printStackTrace();
    }
    return null;
  }
}

