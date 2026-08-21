package mob_combat_rebalance.loot;

import mob_combat_rebalance.Mob_combat_rebalance;
import mob_combat_rebalance.elite.EliteMobTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

/**
 * Default elite loot table ids for v1-eligible hostiles.
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
			Map.entry(EntityType.WITCH, table("witch"))
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
		return Identifier.fromNamespaceAndPath(Mob_combat_rebalance.MOD_ID, "elite/" + path);
	}
}
