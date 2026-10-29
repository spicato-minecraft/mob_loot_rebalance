package mob_loot_rebalance.elite;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import java.util.Set;

/**
 * Nether hostiles that skip the elite spawn roll and instead may generate their elite loot table
 * on any player kill at {@link #LOOT_RATE_FACTOR} × {@code elites.spawn_chance}.
 */
public final class BroadEliteLootTypes {

	/**
	 * Half the per-mob elite loot rate you would get from {@code spawn_chance} with guaranteed loot on elites.
	 */
	public static final double LOOT_RATE_FACTOR = 0.5;

	private static final Set<EntityType<?>> TYPES = Set.of(
			EntityTypes.GHAST,
			EntityTypes.PIGLIN_BRUTE
	);

	private BroadEliteLootTypes() {
	}

	public static boolean isBroadLoot(EntityType<?> type) {
		return TYPES.contains(type);
	}

	public static double rollChance(double spawnChance) {
		return spawnChance * LOOT_RATE_FACTOR;
	}

	public static boolean passesRoll(RandomSource random, double spawnChance) {
		double chance = rollChance(spawnChance);
		if (chance >= 1.0) {
			return true;
		}
		if (chance <= 0.0) {
			return false;
		}
		return random.nextDouble() < chance;
	}
}
