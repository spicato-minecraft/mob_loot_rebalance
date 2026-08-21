package mob_combat_rebalance.loot;

import net.minecraft.resources.Identifier;

/**
 * Per-roll routing state for elite loot generation (kill-method book pool, potion toggles).
 */
public record EliteLootRollContext(Identifier bookPool, boolean allowInvisibilityPotion) {

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
