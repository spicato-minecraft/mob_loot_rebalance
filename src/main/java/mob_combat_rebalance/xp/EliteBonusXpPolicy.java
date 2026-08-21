package mob_combat_rebalance.xp;

import mob_combat_rebalance.config.ModConfig;
import mob_combat_rebalance.payout.ElitePayoutPolicy;
import net.minecraft.world.entity.LivingEntity;

/**
 * Elite kill bonus XP on top of the uplifted baseline (AC-9).
 */
public final class EliteBonusXpPolicy {

	public static final double BONUS_MULTIPLIER = 3.0;

	private EliteBonusXpPolicy() {
	}

	public static int resolveBonus(ModConfig config, LivingEntity entity, int upliftedReward) {
		if (!shouldGrantBonus(config, entity)) {
			return 0;
		}
		return scaleBonus(upliftedReward, BONUS_MULTIPLIER);
	}

	public static boolean shouldGrantBonus(ModConfig config, LivingEntity entity) {
		return ElitePayoutPolicy.isPlayerKilledElite(entity, config);
	}

	public static int scaleBonus(int upliftedReward, double bonusMultiplier) {
		if (bonusMultiplier == 0.0 || upliftedReward <= 0) {
			return 0;
		}
		return Math.max(0, (int) Math.round(upliftedReward * bonusMultiplier));
	}
}
