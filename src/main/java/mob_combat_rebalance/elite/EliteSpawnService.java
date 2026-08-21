package mob_combat_rebalance.elite;

import mob_combat_rebalance.config.ModConfig;
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
		if (!EliteMobTypes.isEligible(mob.getType())) {
			return;
		}
		if (mob.isBaby()) {
			return;
		}
		if (level.random.nextDouble() >= ModConfig.get().elites.spawn_chance) {
			return;
		}

		EliteState.markElite(mob);
		EliteModifiers.apply(mob, level);
	}
}
