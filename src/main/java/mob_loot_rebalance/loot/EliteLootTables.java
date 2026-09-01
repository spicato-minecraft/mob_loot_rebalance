package mob_loot_rebalance.loot;

import mob_loot_rebalance.Mob_loot_rebalance;
import mob_loot_rebalance.elite.EliteMobTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

/**
 * Default elite loot table ids for every {@link EliteMobTypes} elite (35 matrix types).
 */
public final class EliteLootTables {

	private static final Map<EntityType<?>, Identifier> BY_TYPE = Map.ofEntries(
			Map.entry(EntityType.ZOMBIE, table("zombie")),
			Map.entry(EntityType.HUSK, table("husk")),
			Map.entry(EntityType.DROWNED, table("drowned")),
			Map.entry(EntityType.ZOMBIE_VILLAGER, table("zombie_villager")),
			Map.entry(EntityType.SKELETON, table("skeleton")),
			Map.entry(EntityType.STRAY, table("stray")),
			Map.entry(EntityType.BOGGED, table("bogged")),
			Map.entry(EntityType.PARCHED, table("parched")),
			Map.entry(EntityType.CREEPER, table("creeper")),
			Map.entry(EntityType.SPIDER, table("spider")),
			Map.entry(EntityType.CAVE_SPIDER, table("cave_spider")),
			Map.entry(EntityType.WITCH, table("witch")),
			Map.entry(EntityType.PHANTOM, table("phantom")),
			Map.entry(EntityType.ENDERMAN, table("enderman")),
			Map.entry(EntityType.SLIME, table("slime")),
			Map.entry(EntityType.SILVERFISH, table("silverfish")),
			Map.entry(EntityType.ENDERMITE, table("endermite")),
			Map.entry(EntityType.GUARDIAN, table("guardian")),
			Map.entry(EntityType.ELDER_GUARDIAN, table("elder_guardian")),
			Map.entry(EntityType.SHULKER, table("shulker")),
			Map.entry(EntityType.GHAST, table("ghast")),
			Map.entry(EntityType.BLAZE, table("blaze")),
			Map.entry(EntityType.MAGMA_CUBE, table("magma_cube")),
			Map.entry(EntityType.WITHER_SKELETON, table("wither_skeleton")),
			Map.entry(EntityType.PIGLIN, table("piglin")),
			Map.entry(EntityType.PIGLIN_BRUTE, table("piglin_brute")),
			Map.entry(EntityType.HOGLIN, table("hoglin")),
			Map.entry(EntityType.ZOGLIN, table("zoglin")),
			Map.entry(EntityType.ZOMBIFIED_PIGLIN, table("zombified_piglin")),
			Map.entry(EntityType.PILLAGER, table("pillager")),
			Map.entry(EntityType.VINDICATOR, table("vindicator")),
			Map.entry(EntityType.EVOKER, table("evoker")),
			Map.entry(EntityType.VEX, table("vex")),
			Map.entry(EntityType.RAVAGER, table("ravager")),
			Map.entry(EntityType.BREEZE, table("breeze"))
	);

	private EliteLootTables() {
	}

	public static Identifier forEntityType(EntityType<?> type) {
		Identifier table = BY_TYPE.get(type);
		if (table == null || !EliteMobTypes.isEligible(type)) {
			throw new IllegalArgumentException("No elite loot table for " + type);
		}
		return table;
	}

	private static Identifier table(String path) {
		return Identifier.fromNamespaceAndPath(Mob_loot_rebalance.MOD_ID, "elite/" + path);
	}
}
