package mob_loot_rebalance.loot;

import mob_loot_rebalance.Mob_loot_rebalance;
import net.minecraft.resources.Identifier;

/**
 * Locked datapack layout for themed elite loot (matrix SOT).
 *
 * <p>Outer {@code elite/&lt;mob&gt;.json}: one pool, {@code rolls: 1}, weights sum 100 —
 * consolation 15, books 23, potions 40, unique 22.
 *
 * <p>Book slot nested table {@code elite/books/&lt;mob&gt;.json}: two independent pools
 * (each {@code rolls: 1}):
 * <ol>
 *   <li>core — one pick among empty, armor books (Protection + signatures), Sharpness (melee),
 *       Power (ranged), and rare Mending. At most one core book.</li>
 *   <li>theme — weapon signatures vs empty ({@code p_theme} 0.35), each signature gated
 *       with {@code kill_method}; empty-only if the matrix cell is —</li>
 * </ol>
 *
 * <p>A book slot therefore drops at most two books: one core (armor or weapon or Mending)
 * plus one theme.
 *
 * <p>Potion slot {@code elite/potions/&lt;mob&gt;.json}: two independent pools — Healing vs empty
 * ({@code p_potion_core} 0.55) and theme vs empty ({@code p_potion_theme} 0.40).
 *
 * <p>Nested {@code p_*} empty/item rates stay encoded as integer datapack weights.
 * Outer slot mix is live-tunable via {@code elite_loot.*_weight} config knobs.
 */
public final class EliteLootLayout {

	public static final int OUTER_WEIGHT_TOTAL = 100;
	public static final int CONSOLATION_WEIGHT = 15;
	public static final int BOOK_SLOT_WEIGHT = 23;
	public static final int POTION_SLOT_WEIGHT = 40;
	public static final int UNIQUE_WEIGHT = 22;

	/**
	 * Planning default: empty, armor family, and matching weapon family share weight
	 * ({@code 2/3} chance of a core book on a melee or ranged kill).
	 */
	public static final double P_CORE = 2.0 / 3.0;
	public static final double P_THEME = 0.35;
	public static final double P_POTION_CORE = 0.55;
	public static final double P_POTION_THEME = 0.40;

	private EliteLootLayout() {
	}

	public static Identifier outerTable(String mobPath) {
		return Identifier.fromNamespaceAndPath(Mob_loot_rebalance.MOD_ID, "elite/" + mobPath);
	}

	public static Identifier booksTable(String mobPath) {
		return Identifier.fromNamespaceAndPath(Mob_loot_rebalance.MOD_ID, "elite/books/" + mobPath);
	}

	public static Identifier potionsTable(String mobPath) {
		return Identifier.fromNamespaceAndPath(Mob_loot_rebalance.MOD_ID, "elite/potions/" + mobPath);
	}
}
