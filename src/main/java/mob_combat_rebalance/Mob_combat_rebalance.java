package mob_combat_rebalance;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mob_combat_rebalance implements ModInitializer {
	public static final String MOD_ID = "mob_combat_rebalance";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Mob Combat Rebalance initialized");
	}
}
