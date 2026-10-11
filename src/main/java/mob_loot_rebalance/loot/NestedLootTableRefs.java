package mob_loot_rebalance.loot;

import net.minecraft.core.HolderSet;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * 26.2 stored one reference-or-inline table. 26.3 stores a holder set.
 * A single keyed holder is the old reference side; inline and multi-holder sets have no one id.
 */
public final class NestedLootTableRefs {

	private NestedLootTableRefs() {
	}

	public static Identifier referenceId(HolderSet<LootTable> tables) {
		if (tables.size() != 1) {
			return null;
		}
		return tables.get(0).unwrapKey().map(ResourceKey::identifier).orElse(null);
	}
}
