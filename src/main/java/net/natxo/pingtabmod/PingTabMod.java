package net.natxo.pingtabmod;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingTabMod implements ModInitializer {
	public static final String MOD_ID = "pingtabmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {


		LOGGER.info("PingTabMod Loaded!");
	}
}