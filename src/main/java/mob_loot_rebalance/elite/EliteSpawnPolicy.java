package mob_loot_rebalance.elite;

import net.minecraft.world.entity.EntitySpawnReason;

/**
 * Classifies whether a newly spawned mob may roll for elite status at spawn time.
 * Allowed reasons: natural, chunk generation, structure, spawner, trial spawner, and patrol.
 * Conversion, reinforcement, raid EVENT waves, triggered, and all other reasons deny.
 * Boss exclusion is type-gated via {@link EliteMobTypes}, not spawn reason.
 */
public final class EliteSpawnPolicy {

	private EliteSpawnPolicy() {
	}

	public static boolean isEligibleForEliteRoll(EntitySpawnReason reason) {
		return reason == EntitySpawnReason.NATURAL
				|| reason == EntitySpawnReason.CHUNK_GENERATION
				|| reason == EntitySpawnReason.STRUCTURE
				|| reason == EntitySpawnReason.SPAWNER
				|| reason == EntitySpawnReason.TRIAL_SPAWNER
				|| reason == EntitySpawnReason.PATROL;
	}
}
