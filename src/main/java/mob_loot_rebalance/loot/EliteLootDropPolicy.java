package mob_loot_rebalance.loot;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.elite.BroadEliteLootTypes;
import mob_loot_rebalance.elite.EliteMobTypes;
import mob_loot_rebalance.payout.ElitePayoutPolicy;
import mob_loot_rebalance.xp.XpUpliftPolicy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Gates the elite-table generate on player-killed elites (AC-10, AC-11, AC-20).
 * {@link BroadEliteLootTypes} use a per-kill roll instead of the elite spawn stamp.
 */
public final class EliteLootDropPolicy {

	private EliteLootDropPolicy() {
	}

	public static boolean shouldDropEliteLoot(LivingEntity entity, DamageSource damageSource, ModConfig config) {
		if (!config.isEliteLootPillarActive()) {
			return false;
		}
		if (!XpUpliftPolicy.isPlayerKill(entity)) {
			return false;
		}
		if (!EliteMobTypes.isEligible(entity.getType())) {
			return false;
		}
		if (BroadEliteLootTypes.isBroadLoot(entity.getType())) {
			return BroadEliteLootTypes.passesRoll(entity.getRandom(), config.elites.spawn_chance);
		}
		return shouldDropEliteLoot(true, ElitePayoutPolicy.isPlayerKilledElite(entity, config));
	}

	static boolean shouldDropEliteLoot(boolean lootPillarActive, boolean playerKilledElite) {
		return lootPillarActive && playerKilledElite;
	}
}
