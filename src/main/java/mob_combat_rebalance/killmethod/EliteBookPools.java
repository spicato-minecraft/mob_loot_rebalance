package mob_combat_rebalance.killmethod;

import mob_combat_rebalance.Mob_combat_rebalance;
import net.minecraft.resources.Identifier;

/**
 * Loot-table ids for kill-method sub-pools. Enchant lists live in JSON only; Java routes to these tables.
 */
public final class EliteBookPools {
	public static final Identifier ARMOR = pool("armor");
	public static final Identifier MELEE = pool("melee");
	public static final Identifier RANGED = pool("ranged");
	public static final Identifier MENDING = pool("mending");
	public static final Identifier FALLBACK = pool("fallback");

	private EliteBookPools() {
	}

	public static Identifier pool(String path) {
		return Identifier.fromNamespaceAndPath(Mob_combat_rebalance.MOD_ID, "elite/pools/" + path);
	}
}
