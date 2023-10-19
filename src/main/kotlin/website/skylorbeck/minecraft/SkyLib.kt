package website.skylorbeck.minecraft

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory

object SkyLib : ModInitializer {
    private val logger = LoggerFactory.getLogger("skylib")

	override fun onInitialize() {
		if (FabricLoader.getInstance().isDevelopmentEnvironment)
		logger.info("Skylib Initialized")
	}
}