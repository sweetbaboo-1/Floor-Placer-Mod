package net.sweetbaboo.floorplacermod;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sandrohc.schematic4j.SchematicLoader;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;

public class LitematicaLoader {
  private static final String MOD_ID = "peartcraft-carpet-floor-placer";
  public static final Logger logger = LoggerFactory.getLogger(MOD_ID);

  public static Schematic loadLitematicaFile(String filename) {
    final String filePath = "resources\\schematics\\" + filename; // path to where syncmatica stores its schematics?
    Schematic schematic;
    try {
      schematic = SchematicLoader.load(filePath);
      logger.info("Successfully loaded " + schematic.name());
      return schematic;
    } catch (ParsingException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}

