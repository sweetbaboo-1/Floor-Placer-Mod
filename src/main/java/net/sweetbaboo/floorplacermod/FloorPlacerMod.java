package net.sweetbaboo.floorplacermod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import net.sweetbaboo.floorplacermod.command.SchematicFileArgumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FloorPlacerMod implements ModInitializer {
    public static final String MOD_ID = "floor-placer-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ArgumentTypeRegistry.registerArgumentType(new Identifier(MOD_ID, "schematic_type"), SchematicFileArgumentType.class, ConstantArgumentSerializer.of(SchematicFileArgumentType::new));
    }
}