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
  private static final File SAVE_STATE_PATH = FabricLoader.getInstance().getConfigDir().resolve("floor-placer-mod/blockGeneratorState.json").toFile();
  private static BlockGenerator instance;
  private int index;
  private List<String> blockOrderList;
  private String filename;
  private int tilesY;
  private int tilesX;
  private int cols;
  private int rows;
  private transient Schematic tile;

  private BlockGenerator() {
    blockOrderList = new ArrayList<>();
    filename = "";
  }

  public static BlockGenerator getInstance() {
    if (instance == null) {
      instance = new BlockGenerator();
    }
    return instance;
  }

  public boolean init(String filename, int rowsToBuild, int columnsToBuild, ServerCommandSource source) {
    this.filename = filename;
    this.tile = LitematicaLoader.loadLitematicaFile(filename, source);

    if (this.tile == null) {
      FloorPlacerMod.LOGGER.error("BlockGenerator.init failed to load the schematic %s".formatted(filename));
      source.sendError(Text.of("BlockGenerator.init failed to load the schematic %s".formatted(filename)));
      this.filename = null;
      return false;
    }

    this.tilesY= columnsToBuild;
    this.tilesX= rowsToBuild;

    this.cols = tile.width();
    this.rows = tile.length();
    this.index = 0;
    generateBlockOrder();
    return true;
  }

  public boolean saveState(ServerCommandSource source) {

    if (tile == null) {
      source.sendFeedback(() -> Text.of("Nothing to save..."), false);
      return true;
    }

    File directory = SAVE_STATE_PATH.getParentFile();
    if (!directory.exists()) {
      if (!directory.mkdirs()) {
        FloorPlacerMod.LOGGER.error("Failed to create directory: %s".formatted(directory));
        source.sendFeedback(() -> Text.of("Could not create the directory"), false);
        return false;
      }
    }

    try (FileWriter writer = new FileWriter(SAVE_STATE_PATH)) {
      GSON.toJson(this, writer);
      source.sendFeedback(() -> Text.of("Saved " + tile.name()), false);
      return true;
    } catch (IOException e) {
      FloorPlacerMod.LOGGER.error("Error serializing BlockGenerator", e);
      source.sendError(Text.of("Error serializing BlockGenerator"));
      return false;
    }
  }

  public boolean loadState(ServerCommandSource source) {
    if (!SAVE_STATE_PATH.exists()) {
      source.sendError(Text.of("Nothing to load..."));
      return false;
    }

    try (FileReader reader = new FileReader(SAVE_STATE_PATH)) {
      BlockGenerator loadedState = GSON.fromJson(reader, BlockGenerator.class);
      this.blockOrderList = loadedState.blockOrderList;
      this.index = loadedState.index;
      this.filename = loadedState.filename;
      this.tile = LitematicaLoader.loadLitematicaFile(this.filename, source);
      this.tilesY= loadedState.tilesY;
      this.tilesX= loadedState.tilesX;
      this.cols = loadedState.cols;
      this.rows = loadedState.rows;
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

  public String getTileName() {
    if (this.tile == null) {
      return "null";
    }
    return this.tile.name();
  }

  public String getNextBlockName() {
    if (blockOrderList == null) {
      return null;
    }
    if (index >= blockOrderList.size()) {
      return null;
    }
    String name = blockOrderList.get(index).substring("minecraft:".length());
    index++;
    return name;
  }

  public void generateBlockOrder() {
    blockOrderList = new ArrayList<>();
    for (int x=0; x < cols * tilesX; x++) {
      for (int y=rows * tilesY - 1; y > 0; y--) {
        blockOrderList.add(tile.block(x % cols, 0, y % rows).block);
      }
    }
    for (int x=cols * tilesY - 1; x >= 0; x--) {
      blockOrderList.add(tile.block(0, 0, x % cols).block);
    }
  }

  public void reset() {
    instance = null;
  }
}
