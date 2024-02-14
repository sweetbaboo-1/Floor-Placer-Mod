package net.sweetbaboo.floorplacermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.sandrohc.schematic4j.schematic.Schematic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockGenerator {
  private static final String SAVE_STATE_DIRECTORY_PATH="resources\\floorplacerState\\";
  private static final String SAVE_STATE_FILENAME="blockGeneratorState.json";

  private static BlockGenerator instance;

  @Expose
  private int index;
  @Expose
  private List<String> blockOrderList;
  @Expose
  private String filename;

  private Schematic tile;
  private int tilesX, tilesY, cols, rows;

  private BlockGenerator(String filename, int rowsToBuild, int columnsToBuild) {
    this.filename=filename;
    this.tile=LitematicaLoader.loadLitematicaFile(filename);

    this.tilesX = columnsToBuild;
    this.tilesY = rowsToBuild;

    assert tile != null;
    this.cols = tile.width();
    this.rows = tile.length();
    this.index = 0;
    generateBlockOrder();
  }

  public static BlockGenerator getInstance(String filename, int rowsToBuild, int columnsToBuild) {
    if (instance == null) {
      instance=new BlockGenerator(filename, rowsToBuild, columnsToBuild);
    }
    return instance;
  }

  public boolean saveState() {
    GsonBuilder gsonBuilder=new GsonBuilder().setPrettyPrinting();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation(); // Only fields with @Expose will be serialized
    Gson gson=gsonBuilder.create();

    File directory = new File(SAVE_STATE_DIRECTORY_PATH);
    if (!directory.exists()) {
      if (!directory.mkdirs()) {
        System.err.println("Failed to create directory: " + SAVE_STATE_DIRECTORY_PATH);
        return false;
      }
    }

    try (FileWriter writer=new FileWriter(SAVE_STATE_DIRECTORY_PATH + SAVE_STATE_FILENAME)) {
      gson.toJson(this, writer);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean loadState() {
    Gson gson=new Gson();
    try (FileReader reader=new FileReader(SAVE_STATE_DIRECTORY_PATH + SAVE_STATE_FILENAME)) {
      BlockGenerator loadedState=gson.fromJson(reader, BlockGenerator.class);
      this.blockOrderList = loadedState.blockOrderList;
      this.index = loadedState.index;
      this.filename = loadedState.filename;
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public String getTileName() {
    return this.tile.name();
  }

  public static BlockGenerator getInstance() {
    if (instance == null) {
      instance=new BlockGenerator();
    }
    return instance;
  }

  private BlockGenerator() {
  }

  public String getNextBlockName() {
    if (index >= blockOrderList.size()) {
      return null;
    }
    String name = blockOrderList.get(index).substring("minecraft:".length());
    index++;
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
    tile=null;
    filename=null;
    index = 0;
    blockOrderList = null;
    tilesX = 0;
    tilesY = 0;
    cols = 0;
    rows = 0;
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
