package net.sweetbaboo.floorplacermod;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

// Prevents MinecraftClient calls on a dedicated server.
public class SyncmaticaConfigAccess {
    private static final Path PLACEMENTS = Path.of("syncmatica/placements.json");

    @Nullable
    public static File getPlacementsConfig() {
        FabricLoader fabric = FabricLoader.getInstance();

        if (fabric.getEnvironmentType() == EnvType.SERVER)
            return fabric.getConfigDir().resolve(PLACEMENTS).toFile();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.isConnectedToLocalServer())
            return Objects.requireNonNull(mc.getServer()).getSavePath(WorldSavePath.ROOT).resolve(PLACEMENTS).toFile();
        return null;
    }
}