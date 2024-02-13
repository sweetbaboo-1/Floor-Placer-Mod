package net.sweetbaboo.floorplacermod;

import net.sandrohc.schematic4j.SchematicLoader;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;

import java.io.IOException;

public class LitematicaLoader {
  private static final String SYNCMATICA_FOLDER="syncmatics\\";

  public static Schematic loadLitematicaFile(String filename) {
    final String filePath=SYNCMATICA_FOLDER + filename;
    Schematic schematic;
    try {
      schematic=SchematicLoader.load(filePath);
      return schematic;
    } catch (ParsingException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}

