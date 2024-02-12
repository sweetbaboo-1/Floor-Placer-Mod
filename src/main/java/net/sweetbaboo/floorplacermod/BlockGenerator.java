package net.sweetbaboo.floorplacermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.sandrohc.schematic4j.schematic.Schematic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BlockGenerator {
  private static final String FILE_PATH = "resources\\floorplacerState\\blockGeneratorState.json";

  private static BlockGenerator instance;

  private Schematic tile;

  @Expose
  private String filename;
  @Expose
  private int rowsToBuild;
  @Expose
  private int columnsToBuild;
  @Expose
  private int floorColumn;
  @Expose
  private int tileColumn;
  @Expose
  private int floorRow;
  @Expose
  private int tileRow;

  private BlockGenerator(String filename, int rowsToBuild, int columnsToBuild) {
    this.filename = filename;
    this.tile = LitematicaLoader.loadLitematicaFile(filename);
    this.rowsToBuild = rowsToBuild;
    this.columnsToBuild = columnsToBuild;
  }

  public static BlockGenerator getInstance(String filename, int rowsToBuild, int columnsToBuild) {
    if (instance == null) {
      instance = new BlockGenerator(filename, rowsToBuild, columnsToBuild);
    }
    return instance;
  }

  public boolean saveState() {
    GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation(); // Only fields with @Expose will be serialized
    Gson gson = gsonBuilder.create();

    try (FileWriter writer = new FileWriter(FILE_PATH)) {
      gson.toJson(this, writer);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean loadState() {
    Gson gson = new Gson();
    try (FileReader reader = new FileReader(FILE_PATH)) {
      BlockGenerator loadedState = gson.fromJson(reader, BlockGenerator.class);
      this.filename = loadedState.filename;
      this.rowsToBuild = loadedState.rowsToBuild;
      this.columnsToBuild = loadedState.columnsToBuild;
      this.floorColumn = loadedState.floorColumn;
      this.tileColumn = loadedState.tileColumn;
      this.floorRow = loadedState.floorRow;
      this.tileRow = loadedState.tileRow;
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
      instance = new BlockGenerator();
      instance.loadState();
    }
    return instance;
  }

  private BlockGenerator() {
  }

  /**
   * TODO: right now the way that the floor placer is set up the top row of blocks is used as the stream. This is undesired.
   *
   * @return
   */
  public String getNextBlockName() {
    if (floorColumn < columnsToBuild && tileColumn < tile.width()
            && floorRow < rowsToBuild && tileRow < tile.length()) {
      String nextBlock = tile.block(tileRow, 0, tileColumn).block;

      tileRow++;
      if (tileRow >= tile.length()) {
        tileRow = 0;
        floorRow++;
        if (floorRow >= rowsToBuild) {
          floorRow = 0;
          tileColumn++;
          if (tileColumn >= tile.width()) {
            tileColumn = 0;
            floorColumn++;
          }
        }
      }

      return nextBlock.substring("minecraft:".length());
    } else {
      return null;
    }
  }

  public void reset() {
    tile = null;
    filename = null;
    rowsToBuild = 0;
    columnsToBuild = 0;
    floorColumn = 0;
    tileColumn = 0;
    floorRow = 0;
    tileRow = 0;
  }

  public Schematic getTile() {
    return tile;
  }

  public String getFilename() {
    return filename;
  }

  public int getRowsToBuild() {
    return rowsToBuild;
  }

  public int getColumnsToBuild() {
    return columnsToBuild;
  }

  public int getFloorColumn() {
    return floorColumn;
  }

  public int getTileColumn() {
    return tileColumn;
  }

  public int getFloorRow() {
    return floorRow;
  }

  public int getTileRow() {
    return tileRow;
  }
}
