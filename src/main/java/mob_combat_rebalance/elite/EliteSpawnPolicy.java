package mob_combat_rebalance.elite;

import net.minecraft.world.entity.EntitySpawnReason;

/**
 * Classifies whether a newly spawned mob may roll for elite status at spawn time.
 * v1 locked rules: only {@link EntitySpawnReason#NATURAL} and
 * {@link EntitySpawnReason#CHUNK_GENERATION} are eligible; all other reasons deny.
 */
public final class EliteSpawnPolicy {

	private EliteSpawnPolicy() {
	}

	public static boolean isEligibleForEliteRoll(EntitySpawnReason reason) {
		return reason == EntitySpawnReason.NATURAL
				|| reason == EntitySpawnReason.CHUNK_GENERATION;
	}
}
