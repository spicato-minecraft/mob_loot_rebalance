package mob_loot_rebalance;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.elite.EliteAttachments;
import mob_loot_rebalance.loot.KillMethodLootCondition;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mob_loot_rebalance implements ModInitializer {
	public static final String MOD_ID = "mob_loot_rebalance";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Eager-init attachment registration before any spawn rolls.
		EliteAttachments.IS_ELITE.getClass();
		KillMethodLootCondition.register();

		ModConfig config = ModConfig.load();
		ModConfig.set(config);
		LOGGER.info(
				"Mob Loot Rebalance initialized (xp={}, elites={}, elite_loot={})",
				config.isXpPillarActive(),
				config.isElitesPillarActive(),
				config.isEliteLootPillarActive()
		);
	}
}
