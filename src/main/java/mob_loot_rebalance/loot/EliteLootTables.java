package mob_loot_rebalance.loot;

import mob_loot_rebalance.Mob_loot_rebalance;
import mob_loot_rebalance.elite.EliteMobTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import java.util.Map;

/**
 * Default elite loot table ids for every {@link EliteMobTypes} elite (35 matrix types).
 */
public final class EliteLootTables {

	private static final Map<EntityType<?>, Identifier> BY_TYPE = Map.ofEntries(
			Map.entry(EntityTypes.ZOMBIE, table("zombie")),
			Map.entry(EntityTypes.HUSK, table("husk")),
			Map.entry(EntityTypes.DROWNED, table("drowned")),
			Map.entry(EntityTypes.ZOMBIE_VILLAGER, table("zombie_villager")),
			Map.entry(EntityTypes.SKELETON, table("skeleton")),
			Map.entry(EntityTypes.STRAY, table("stray")),
			Map.entry(EntityTypes.BOGGED, table("bogged")),
			Map.entry(EntityTypes.PARCHED, table("parched")),
			Map.entry(EntityTypes.CREEPER, table("creeper")),
			Map.entry(EntityTypes.SPIDER, table("spider")),
			Map.entry(EntityTypes.CAVE_SPIDER, table("cave_spider")),
			Map.entry(EntityTypes.WITCH, table("witch")),
			Map.entry(EntityTypes.PHANTOM, table("phantom")),
			Map.entry(EntityTypes.ENDERMAN, table("enderman")),
			Map.entry(EntityTypes.SLIME, table("slime")),
			Map.entry(EntityTypes.SILVERFISH, table("silverfish")),
			Map.entry(EntityTypes.ENDERMITE, table("endermite")),
			Map.entry(EntityTypes.GUARDIAN, table("guardian")),
			Map.entry(EntityTypes.ELDER_GUARDIAN, table("elder_guardian")),
			Map.entry(EntityTypes.SHULKER, table("shulker")),
			Map.entry(EntityTypes.GHAST, table("ghast")),
			Map.entry(EntityTypes.BLAZE, table("blaze")),
			Map.entry(EntityTypes.MAGMA_CUBE, table("magma_cube")),
			Map.entry(EntityTypes.WITHER_SKELETON, table("wither_skeleton")),
			Map.entry(EntityTypes.PIGLIN, table("piglin")),
			Map.entry(EntityTypes.PIGLIN_BRUTE, table("piglin_brute")),
			Map.entry(EntityTypes.HOGLIN, table("hoglin")),
			Map.entry(EntityTypes.ZOGLIN, table("zoglin")),
			Map.entry(EntityTypes.ZOMBIFIED_PIGLIN, table("zombified_piglin")),
			Map.entry(EntityTypes.PILLAGER, table("pillager")),
			Map.entry(EntityTypes.VINDICATOR, table("vindicator")),
			Map.entry(EntityTypes.EVOKER, table("evoker")),
			Map.entry(EntityTypes.VEX, table("vex")),
			Map.entry(EntityTypes.RAVAGER, table("ravager")),
			Map.entry(EntityTypes.BREEZE, table("breeze"))
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
