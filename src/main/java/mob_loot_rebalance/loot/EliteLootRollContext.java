package mob_loot_rebalance.loot;

import mob_loot_rebalance.killmethod.KillMethod;

/**
 * Per-roll routing state for elite loot generation.
 *
 * @param killMethod melee / ranged / fallback for core weapon and theme conditions
 */
public record EliteLootRollContext(
		KillMethod killMethod,
		boolean allowInvisibilityPotion
) {

	private static final ThreadLocal<EliteLootRollContext> CURRENT = new ThreadLocal<>();

	public static EliteLootRollContext current() {
		return CURRENT.get();
	}

	public static void run(EliteLootRollContext context, Runnable action) {
		EliteLootRollContext previous = CURRENT.get();
		CURRENT.set(context);
		try {
			action.run();
		} finally {
			if (previous == null) {
				CURRENT.remove();
			} else {
				CURRENT.set(previous);
			}
		}
	}
}
