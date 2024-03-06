package net.sweetbaboo.floorplacermod.commands.backup;

import net.minecraft.server.MinecraftServer;
import net.sweetbaboo.floorplacermod.BlockGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RestoreHelper {

  public static List<String> getAvailableBackups(MinecraftServer server) {
    File root=BlockGenerator.SAVE_STATE_PATH;
    List<String> backupNames=new ArrayList<>();

    if (!root.exists() || !root.isDirectory()) {
      return new ArrayList<>();
    }

    File[] files=root.listFiles();

    if (files == null) {
      return new ArrayList<>();
    }

    for (File file : files) {
      if (file.isFile()) {
        backupNames.add(file.getName());
      }
    }
    return backupNames;
  }
}
