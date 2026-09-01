package mob_loot_rebalance.payout;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.elite.EliteMobTypes;
import mob_loot_rebalance.elite.EliteState;
import mob_loot_rebalance.xp.XpUpliftPolicy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * Shared gate for player-killed elite payouts (AC-9, AC-10, AC-22).
 */
public final class ElitePayoutPolicy {

	private ElitePayoutPolicy() {
	}

	public static boolean isPlayerKilledElite(LivingEntity entity, ModConfig config) {
		return isPlayerKilledElite(
				config.isElitesPillarActive(),
				entity instanceof Mob mob && EliteState.isElite(mob),
				EliteMobTypes.isEligible(entity.getType()),
				XpUpliftPolicy.isPlayerKill(entity)
		);
	}

	static boolean isPlayerKilledElite(
			boolean elitesPillarActive,
			boolean eliteMob,
			boolean eligibleType,
			boolean playerKill
	) {
		return elitesPillarActive && eliteMob && eligibleType && playerKill;
	}
}
