package mob_loot_rebalance.loot;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.payout.ElitePayoutPolicy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Gates the elite-table generate on player-killed elites (AC-10, AC-11, AC-20).
 */
public final class EliteLootDropPolicy {

	private EliteLootDropPolicy() {
	}

	public static boolean shouldDropEliteLoot(LivingEntity entity, DamageSource damageSource, ModConfig config) {
		return shouldDropEliteLoot(config.isEliteLootPillarActive(), ElitePayoutPolicy.isPlayerKilledElite(entity, config));
	}

	static boolean shouldDropEliteLoot(boolean lootPillarActive, boolean playerKilledElite) {
		return lootPillarActive && playerKilledElite;
	}
}
