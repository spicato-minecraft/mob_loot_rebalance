package mob_loot_rebalance.config;

import mob_loot_rebalance.loot.EliteLootLayout;

/**
 * Elite-table loot pillar plus playtest knobs for the outer slot mix.
 */
public final class EliteLootConfig {
	public boolean enabled = true;
	public int consolation_weight = EliteLootLayout.CONSOLATION_WEIGHT;
	public int books_weight = EliteLootLayout.BOOK_SLOT_WEIGHT;
	public int potions_weight = EliteLootLayout.POTION_SLOT_WEIGHT;
	public int unique_weight = EliteLootLayout.UNIQUE_WEIGHT;
}
