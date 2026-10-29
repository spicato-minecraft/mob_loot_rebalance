package mob_loot_rebalance.elite;

import mob_loot_rebalance.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;

/**
 * Elite spawn roll at {@link Mob#finalizeSpawn} tail (AC-4).
 */
public final class EliteSpawnService {

	private EliteSpawnService() {
	}

	public static void tryRollElite(Mob mob, ServerLevel level, EntitySpawnReason reason) {
		if (!ModConfig.get().isElitesPillarActive()) {
			return;
		}
		if (!EliteSpawnPolicy.isEligibleForEliteRoll(reason)) {
			return;
		}
		if (!EliteMobTypes.isEliteSpawnEligible(mob.getType())) {
			return;
		}
		if (mob.isBaby()) {
			return;
		}
		double chance = EntitySpawnReason.isSpawner(reason)
				? ModConfig.get().elites.spawner_trial_spawn_chance
				: ModConfig.get().elites.spawn_chance;
		if (chance <= 0.0 || mob.getRandom().nextDouble() >= chance) {
			return;
		}

		EliteState.markElite(mob);
		EliteModifiers.apply(mob, level);
	}
}
