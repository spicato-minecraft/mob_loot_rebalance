package mob_loot_rebalance.killmethod;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Weapon-signature theme books: melee vs ranged. Armor cores stay ungated.
 */
public final class ThemeEnchantmentKillMethod {

	public static final Set<String> MELEE = Set.of(
			"minecraft:smite",
			"minecraft:bane_of_arthropods",
			"minecraft:knockback",
			"minecraft:fire_aspect",
			"minecraft:looting",
			"minecraft:sweeping_edge",
			"minecraft:density",
			"minecraft:breach",
			"minecraft:lunge",
			"minecraft:impaling"
	);

	public static final Set<String> RANGED = Set.of(
			"minecraft:punch",
			"minecraft:flame",
			"minecraft:infinity",
			"minecraft:piercing",
			"minecraft:quick_charge",
			"minecraft:multishot",
			"minecraft:loyalty",
			"minecraft:channeling",
			"minecraft:riptide"
	);

	private static final Map<String, KillMethod> BY_ID;

	static {
		java.util.HashMap<String, KillMethod> map = new java.util.HashMap<>();
		for (String id : MELEE) {
			map.put(id, KillMethod.MELEE);
		}
		for (String id : RANGED) {
			map.put(id, KillMethod.RANGED);
		}
		BY_ID = Map.copyOf(map);
	}

	private ThemeEnchantmentKillMethod() {
	}

	public static Optional<KillMethod> forEnchantmentId(String id) {
		return Optional.ofNullable(BY_ID.get(id));
	}
}
