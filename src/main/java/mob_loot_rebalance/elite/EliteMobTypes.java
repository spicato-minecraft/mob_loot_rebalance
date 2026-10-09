package mob_loot_rebalance.elite;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import java.util.Set;

/**
 * Elite-eligible hostile mob types. Bosses (Wither, Ender Dragon, Warden) and bees are excluded.
 */
public final class EliteMobTypes {

	static final Set<EntityType<?>> ELIGIBLE = Set.of(
			EntityTypes.ZOMBIE,
			EntityTypes.HUSK,
			EntityTypes.DROWNED,
			EntityTypes.ZOMBIE_VILLAGER,
			EntityTypes.SKELETON,
			EntityTypes.STRAY,
			EntityTypes.BOGGED,
			EntityTypes.PARCHED,
			EntityTypes.CREEPER,
			EntityTypes.SPIDER,
			EntityTypes.CAVE_SPIDER,
			EntityTypes.WITCH,
			EntityTypes.PHANTOM,
			EntityTypes.ENDERMAN,
			EntityTypes.SLIME,
			EntityTypes.SILVERFISH,
			EntityTypes.ENDERMITE,
			EntityTypes.GUARDIAN,
			EntityTypes.ELDER_GUARDIAN,
			EntityTypes.SHULKER,
			EntityTypes.GHAST,
			EntityTypes.BLAZE,
			EntityTypes.MAGMA_CUBE,
			EntityTypes.WITHER_SKELETON,
			EntityTypes.PIGLIN,
			EntityTypes.PIGLIN_BRUTE,
			EntityTypes.HOGLIN,
			EntityTypes.ZOGLIN,
			EntityTypes.ZOMBIFIED_PIGLIN,
			EntityTypes.PILLAGER,
			EntityTypes.VINDICATOR,
			EntityTypes.EVOKER,
			EntityTypes.VEX,
			EntityTypes.RAVAGER,
			EntityTypes.BREEZE
	);

	private EliteMobTypes() {
	}

	public static boolean isEligible(EntityType<?> type) {
		return ELIGIBLE.contains(type);
	}
}
