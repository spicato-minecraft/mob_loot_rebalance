package mob_combat_rebalance;

import mob_combat_rebalance.config.ModConfig;
import mob_combat_rebalance.elite.EliteAttachments;
import mob_combat_rebalance.loot.RoutedEnchantedBookFunction;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mob_combat_rebalance implements ModInitializer {
	public static final String MOD_ID = "mob_combat_rebalance";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Eager-init attachment registration before any spawn rolls.
		EliteAttachments.IS_ELITE.getClass();
		RoutedEnchantedBookFunction.register();

		ModConfig config = ModConfig.load();
		ModConfig.set(config);
		LOGGER.info(
				"Mob Combat Rebalance initialized (xp={}, elites={}, elite_loot={})",
				config.isXpPillarActive(),
				config.isElitesPillarActive(),
				config.isEliteLootPillarActive()
		);
	}
}
