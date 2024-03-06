package net.sweetbaboo.floorplacermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.sandrohc.schematic4j.schematic.Schematic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockGenerator {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static final File SAVE_STATE_PATH = FabricLoader.getInstance().getConfigDir().resolve("floor-placer-mod").toFile();
  private static BlockGenerator instance;
  private int index;
  private List<String> blockOrderList;
  private transient int tilesX;
  private transient int tilesY;
  private transient int cols;
  private transient int rows;
  private transient Schematic tile;

  private BlockGenerator() {
    blockOrderList = new ArrayList<>();
  }

  public static BlockGenerator getInstance() {
    if (instance == null) {
      instance = new BlockGenerator();
    }
    return instance;
  }

  public boolean init(String filename, int rowsToBuild, int columnsToBuild, ServerCommandSource source) {
    this.tile = LitematicaLoader.loadLitematicaFile(filename + ".litematic", source);

    if (this.tile == null) {
      FloorPlacerMod.LOGGER.error("BlockGenerator.init failed to load the schematic %s".formatted(filename));
      source.sendError(Text.of("BlockGenerator.init failed to load the schematic %s".formatted(filename)));
      return false;
    }

    this.tilesX = columnsToBuild;
    this.tilesY = rowsToBuild;

    this.cols = tile.width();
    this.rows = tile.length();
    this.index = 0;
    generateBlockOrder();
    return true;
  }

  public boolean saveState(ServerCommandSource source, String filename) {
    if (!SAVE_STATE_PATH.exists()) {
      if (!SAVE_STATE_PATH.mkdirs()) {
        FloorPlacerMod.LOGGER.error("Failed to create directory: %s".formatted(SAVE_STATE_PATH));
        source.sendFeedback(() -> Text.of("Could not create the directory"), false);
        return false;
      }
    }

    // TODO: double check that this is the correct file path.
    try (FileWriter writer = new FileWriter(SAVE_STATE_PATH + File.separator + filename)) {
      GSON.toJson(this, writer);
      source.sendFeedback(() -> Text.of("Saved"), false);
      return true;
    } catch (IOException e) {
      FloorPlacerMod.LOGGER.error("Error serializing BlockGenerator", e);
      source.sendError(Text.of("Error serializing BlockGenerator"));
      return false;
    }
  }

  public boolean loadState(ServerCommandSource source, String filename) {
    if (!SAVE_STATE_PATH.exists()) {
      source.sendError(Text.of("Nothing to load..."));
      return false;
    }

    try (FileReader reader = new FileReader(SAVE_STATE_PATH + File.separator + filename)) {
      BlockGenerator loadedState = GSON.fromJson(reader, BlockGenerator.class);
      this.blockOrderList = loadedState.blockOrderList;
      this.index = loadedState.index;
      BlockSelector.selectNextBlock(source.getPlayer(), source, false);
      source.sendFeedback(() -> Text.of("State loaded successfully"), false);
      return true;
    } catch (IOException e) {
      FloorPlacerMod.LOGGER.error("Error loading save state", e);
      source.sendError(Text.of("Error loading save state"));
      return false;
    } catch (JsonSyntaxException e) {
      FloorPlacerMod.LOGGER.error("Error parsing JSON", e);
      source.sendError(Text.of("Error parsing JSON"));
      return false;
    }
  }

  public String getNextBlockName(boolean shouldIncrement) {
    if (blockOrderList == null) {
      return null;
    }
    if (index >= blockOrderList.size()) {
      return null;
    }
    String name = blockOrderList.get(index).substring("minecraft:".length());
    if (shouldIncrement) {
      index++;
    }
    return name;
  }

  public void generateBlockOrder() {
    blockOrderList = new ArrayList<>();
    for (int x = 0; x < cols * tilesY; x++) {
      for (int y = rows * tilesX - 1; y > 0; y--) {
        blockOrderList.add(tile.block(x % cols, 0, y % rows).block);
      }
    }
    for (int x = cols * tilesX - 1; x >= 0; x--) {
      blockOrderList.add(tile.block(0, 0, x % cols).block);
    }
  }

  public void reset() {
    instance = null;
  }

  public boolean setIndex(int index) {
    if (index > blockOrderList.size() - 1) {
      return false;
    }
    this.index = index;
    return true;
  }

  public int getIndex() {
    return index;
  }

  public boolean deleteBackup(ServerCommandSource source, String fileName) {
    if (!SAVE_STATE_PATH.exists()) {
      source.sendError(Text.of("Nothing to load..."));
      return true;
    }

    File file = new File(SAVE_STATE_PATH + File.separator + fileName);
    if (!file.delete()) {
      source.sendError(Text.of("Failed to delete file"));
      return false;
    }
    return true;
  }
}
