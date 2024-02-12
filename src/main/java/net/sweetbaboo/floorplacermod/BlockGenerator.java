package net.sweetbaboo.floorplacermod;

import net.sandrohc.schematic4j.schematic.Schematic;

public class BlockGenerator {
  private static BlockGenerator instance;

  private Schematic tile;
  private int rowsToBuild;
  private int columnsToBuild;

  private int floorColumn = 0;
  private int tileColumn = 0;
  private int floorRow = 0;
  private int tileRow = 0;

  private BlockGenerator(Schematic tile, int rowsToBuild, int columnsToBuild) {
    this.tile = tile;
    this.rowsToBuild = rowsToBuild;
    this.columnsToBuild = columnsToBuild;
  }

  public static BlockGenerator getInstance(Schematic tile, int rowsToBuild, int columnsToBuild) {
    if (instance == null) {
      instance = new BlockGenerator(tile, rowsToBuild, columnsToBuild);
    }
    return instance;
  }

  public static BlockGenerator getInstance() {
    if (instance == null) {
      return new BlockGenerator();
    }
    return instance;
  }

  private BlockGenerator() {
    reset();
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
    instance = null;
  }
}
