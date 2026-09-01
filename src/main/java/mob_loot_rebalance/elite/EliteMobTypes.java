package mob_loot_rebalance.elite;

import net.minecraft.world.entity.EntityType;

import java.util.Set;

/**
 * Elite-eligible hostile mob types. Bosses (Wither, Ender Dragon, Warden) and bees are excluded.
 */
public final class EliteMobTypes {

	static final Set<EntityType<?>> ELIGIBLE = Set.of(
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
			EntityType.WITCH,
			EntityType.PHANTOM,
			EntityType.ENDERMAN,
			EntityType.SLIME,
			EntityType.SILVERFISH,
			EntityType.ENDERMITE,
			EntityType.GUARDIAN,
			EntityType.ELDER_GUARDIAN,
			EntityType.SHULKER,
			EntityType.GHAST,
			EntityType.BLAZE,
			EntityType.MAGMA_CUBE,
			EntityType.WITHER_SKELETON,
			EntityType.PIGLIN,
			EntityType.PIGLIN_BRUTE,
			EntityType.HOGLIN,
			EntityType.ZOGLIN,
			EntityType.ZOMBIFIED_PIGLIN,
			EntityType.PILLAGER,
			EntityType.VINDICATOR,
			EntityType.EVOKER,
			EntityType.VEX,
			EntityType.RAVAGER,
			EntityType.BREEZE
	);

	private EliteMobTypes() {
	}

	public static boolean isEligible(EntityType<?> type) {
		return ELIGIBLE.contains(type);
	}
}
