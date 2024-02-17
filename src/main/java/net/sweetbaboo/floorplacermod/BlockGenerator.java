package net.sweetbaboo.floorplacermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.sandrohc.schematic4j.schematic.Schematic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockGenerator {
  private static final String MOD_ID = "floor-placer-mod";
  private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  private static final String SAVE_STATE_DIRECTORY_PATH="resources" + File.separator + "floorplacerState" + File.separator;
  private static final String SAVE_STATE_FILENAME="blockGeneratorState.json";

  private static BlockGenerator instance;

  @Expose
  private int index;
  @Expose
  private List<String> blockOrderList;
  @Expose
  private String filename;
  @Expose
  private int tilesX;
  @Expose
  private int tilesY;
  @Expose
  private int cols;
  @Expose
  private int rows;

  private Schematic tile;

  private BlockGenerator() {
    blockOrderList = new ArrayList<>();
    filename = "";
  }

  public boolean init(String filename, int rowsToBuild, int columnsToBuild, ServerCommandSource source) {
    this.filename=filename;
    this.tile=LitematicaLoader.loadLitematicaFile(filename, source);

    if (this.tile == null) {
      LOGGER.error("BlockGenerator.init failed to load the schematic " + filename);
      source.sendFeedback(() -> Text.of("BlockGenerator.init failed to load the schematic " + filename), false);
      this.filename = null;
      return false;
    }

    this.tilesX=columnsToBuild;
    this.tilesY=rowsToBuild;

    this.cols=tile.width();
    this.rows=tile.length();
    this.index=0;
    generateBlockOrder();
    return true;
  }

  public boolean saveState(ServerCommandSource source) {

    if (tile == null) {
      source.sendFeedback(() -> Text.of("Nothing to save..."), false);
      return true;
    }

    GsonBuilder gsonBuilder=new GsonBuilder().setPrettyPrinting();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation(); // Only fields with @Expose will be serialized
    Gson gson=gsonBuilder.create();

    File directory=new File(SAVE_STATE_DIRECTORY_PATH);
    if (!directory.exists()) {
      if (!directory.mkdirs()) {
        LOGGER.error("Failed to create directory: " + SAVE_STATE_DIRECTORY_PATH);
        source.sendFeedback(() -> Text.of("Could not create the directory"), false);
        return false;
      }
    }

    try (FileWriter writer=new FileWriter(SAVE_STATE_DIRECTORY_PATH + SAVE_STATE_FILENAME)) {
      gson.toJson(this, writer);
      source.sendFeedback(() -> Text.of("Saved " + tile.name()), false);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      source.sendFeedback(() -> Text.of("Error serializing BlockGenerator"), false);
      return false;
    }
  }

  public boolean loadState(ServerCommandSource source) {
    File file = new File(SAVE_STATE_DIRECTORY_PATH + SAVE_STATE_FILENAME);
    if (!file.exists()) {
      source.sendFeedback(() -> Text.of("Nothing to load..."), false);
      return false;
    }

    Gson gson = new Gson();
    try (FileReader reader = new FileReader(file)) {
      BlockGenerator loadedState = gson.fromJson(reader, BlockGenerator.class);
      this.blockOrderList = loadedState.blockOrderList;
      this.index = loadedState.index;
      this.filename = loadedState.filename;
      this.tile = LitematicaLoader.loadLitematicaFile(this.filename, source);
      this.tilesX = loadedState.tilesX;
      this.tilesY = loadedState.tilesY;
      this.cols = loadedState.cols;
      this.rows = loadedState.rows;
      source.sendFeedback(() -> Text.of("State loaded successfully"), false);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      source.sendFeedback(() -> Text.of("Error loading save state"), false);
      return false;
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      source.sendFeedback(() -> Text.of("Error parsing JSON"), false);
      return false;
    }
  }

  public String getTileName() {
    if (this.tile == null) {
      return "null";
    }
    return this.tile.name();
  }

  public static BlockGenerator getInstance() {
    if (instance == null) {
      instance=new BlockGenerator();
    }
    return instance;
  }

  public String getNextBlockName() {
    if (blockOrderList == null) {
      return null;
    }
    if (index >= blockOrderList.size()) {
      return null;
    }
    String name=blockOrderList.get(index).substring("minecraft:".length());
    index++;
    return name;
  }

  public void generateBlockOrder() {
    blockOrderList=new ArrayList<>();
    for (int x=0; x < cols * tilesY; x++) {
      for (int y=rows * tilesX - 1; y > 0; y--) {
        blockOrderList.add(tile.block(x % cols, 0, y % rows).block);
      }
    }
    for (int x=cols * tilesX - 1; x >= 0; x--) {
      blockOrderList.add(tile.block(0, 0, x % cols).block);
    }
  }

  public void reset() {
    instance=null;
  }

  // these getters are all needed for the serialization and deserialization for gson.
  public Schematic getTile() {
    return tile;
  }

  public String getFilename() {
    return filename;
  }

  public int getIndex() {
    return index;
  }

  public List<String> getBlockOrderList() {
    return blockOrderList;
  }

  public int getTilesX() {
    return tilesX;
  }

  public int getTilesY() {
    return tilesY;
  }

  public int getCols() {
    return cols;
  }

  public int getRows() {
    return rows;
  }
}
