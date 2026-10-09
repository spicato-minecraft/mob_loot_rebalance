package mob_loot_rebalance.loot;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EliteLootDropPolicyTest {

	@Test
	void allowsPlayerKilledEliteWhenLootPillarActive() {
		assertTrue(EliteLootDropPolicy.shouldDropEliteLoot(true, true));
	}

	@Test
	void deniesWhenLootPillarDisabled() {
		assertFalse(EliteLootDropPolicy.shouldDropEliteLoot(false, true));
	}

	@Test
	void deniesNonPlayerKilledElite() {
		assertFalse(EliteLootDropPolicy.shouldDropEliteLoot(true, false));
	}

	@Test
	void mapsAllEliteTables() {
		assertEquals(
				"mob_loot_rebalance:elite/zombie",
				EliteLootTables.forEntityType(EntityTypes.ZOMBIE).toString()
		);
		assertEquals(
				"mob_loot_rebalance:elite/witch",
				EliteLootTables.forEntityType(EntityTypes.WITCH).toString()
		);
	}
}
