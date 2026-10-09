package mob_loot_rebalance.loot;

import mob_loot_rebalance.config.EliteLootConfig;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import mob_loot_rebalance.MinecraftTestBootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OuterSlotWeightsTest {

	@BeforeAll
	static void boot() {
		MinecraftTestBootstrap.init();
	}

	@Test
	void classifyNestedTables() {
		assertEquals(
				OuterSlot.CONSOLATION,
				OuterSlotWeights.classifyNestedTable(Identifier.parse("minecraft:entities/zombie"))
		);
		assertEquals(
				OuterSlot.BOOKS,
				OuterSlotWeights.classifyNestedTable(Identifier.parse("mob_loot_rebalance:elite/books/zombie"))
		);
		assertEquals(
				OuterSlot.POTIONS,
				OuterSlotWeights.classifyNestedTable(Identifier.parse("mob_loot_rebalance:elite/potions/zombie"))
		);
		assertEquals(
				OuterSlot.UNIQUE,
				OuterSlotWeights.classifyNestedTable(Identifier.parse("mob_loot_rebalance:elite/pools/witch_ingredients"))
		);
	}

	@Test
	void remapScalesFromShippedLayout() {
		EliteLootConfig config = new EliteLootConfig();
		config.consolation_weight = 5;
		config.books_weight = 40;
		config.potions_weight = 45;
		config.unique_weight = 10;

		assertEquals(5, OuterSlotWeights.remap(OuterSlot.CONSOLATION, 15, config));
		assertEquals(40, OuterSlotWeights.remap(OuterSlot.BOOKS, 23, config));
		assertEquals(45, OuterSlotWeights.remap(OuterSlot.POTIONS, 40, config));
		assertEquals(4, OuterSlotWeights.remap(OuterSlot.UNIQUE, 9, config));
	}

	@Test
	void outerEliteTableExcludesNestedPaths() {
		assertTrue(OuterSlotWeights.isOuterEliteTable(Identifier.parse("mob_loot_rebalance:elite/zombie")));
		assertFalse(OuterSlotWeights.isOuterEliteTable(Identifier.parse("mob_loot_rebalance:elite/books/zombie")));
		assertFalse(OuterSlotWeights.isOuterEliteTable(Identifier.parse("minecraft:entities/zombie")));
	}

	@Test
	void bookSlotRateTracksLiveWeights() {
		EliteLootConfig config = new EliteLootConfig();
		assertEquals(0.23, OuterSlotWeights.bookSlotRate(config), 0.0001);
		config.consolation_weight = 0;
		config.books_weight = 50;
		config.potions_weight = 50;
		config.unique_weight = 0;
		assertEquals(0.5, OuterSlotWeights.bookSlotRate(config), 0.0001);
	}
}
