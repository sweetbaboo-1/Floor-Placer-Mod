package net.sweetbaboo.floorplacermod;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FloorPlacerMod implements ModInitializer {
	public static final String MOD_ID = "floorplacermod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing PeartCraft's floorBuilder mod");
	}
}