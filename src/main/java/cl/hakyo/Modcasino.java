package cl.hakyo;

import cl.hakyo.registry.ModBlockEntities;
import cl.hakyo.registry.ModBlocks;
import cl.hakyo.registry.ModCreativeTabs;
import cl.hakyo.registry.ModEntities;
import cl.hakyo.registry.ModItems;
import cl.hakyo.registry.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Modcasino implements ModInitializer {
	public static final String MOD_ID = "modcasino";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModSounds.register();
		ModItems.register();
		ModBlocks.register();
		ModEntities.register();
		ModBlockEntities.register();
		ModCreativeTabs.register();
		LOGGER.info("Modcasino initialized");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
