package mob_combat_rebalance.elite;

import net.minecraft.world.entity.EntityType;

import java.util.Set;

/**
 * v1 elite-eligible hostile mob types (AC-4).
 */
public final class EliteMobTypes {

	private static final Set<EntityType<?>> ELIGIBLE = Set.of(
			EntityType.ZOMBIE,
			EntityType.HUSK,
			EntityType.DROWNED,
			EntityType.ZOMBIE_VILLAGER,
			EntityType.SKELETON,
			EntityType.STRAY,
			EntityType.BOGGED,
			EntityType.PARCHED,
			EntityType.CREEPER,
			EntityType.SPIDER,
			EntityType.CAVE_SPIDER,
			EntityType.WITCH
	);

	private EliteMobTypes() {
	}

	public static boolean isEligible(EntityType<?> type) {
		return ELIGIBLE.contains(type);
	}
}
