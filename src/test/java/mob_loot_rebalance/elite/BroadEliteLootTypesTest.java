package mob_loot_rebalance.elite;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BroadEliteLootTypesTest {

	@Test
	void ghastAndPiglinBruteAreBroadLootOnly() {
		assertTrue(BroadEliteLootTypes.isBroadLoot(EntityTypes.GHAST));
		assertTrue(BroadEliteLootTypes.isBroadLoot(EntityTypes.PIGLIN_BRUTE));
		assertFalse(BroadEliteLootTypes.isBroadLoot(EntityTypes.PIGLIN));
		assertFalse(BroadEliteLootTypes.isBroadLoot(EntityTypes.ZOMBIE));
	}

	@Test
	void broadTypesSkipEliteSpawnRoll() {
		assertFalse(EliteMobTypes.isEliteSpawnEligible(EntityTypes.GHAST));
		assertFalse(EliteMobTypes.isEliteSpawnEligible(EntityTypes.PIGLIN_BRUTE));
		assertTrue(EliteMobTypes.isEliteSpawnEligible(EntityTypes.PIGLIN));
	}

	@Test
	void rollChanceIsHalfSpawnChance() {
		assertEquals(0.07, BroadEliteLootTypes.rollChance(0.14), 1e-9);
	}

	@Test
	void passesRollAtZeroAndOne() {
		RandomSource random = RandomSource.create(0L);
		assertFalse(BroadEliteLootTypes.passesRoll(random, 0.0));
		assertTrue(BroadEliteLootTypes.passesRoll(random, 2.0));
	}
}
