package mob_loot_rebalance.loot;

import mob_loot_rebalance.killmethod.KillMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KillMethodLootConditionTest {

	@Test
	void meleeConditionMatchesOnlyMeleeKills() {
		KillMethodLootCondition condition = new KillMethodLootCondition(KillMethod.MELEE);
		assertTrue(matches(condition, KillMethod.MELEE));
		assertFalse(matches(condition, KillMethod.RANGED));
		assertFalse(matches(condition, KillMethod.FALLBACK));
	}

	@Test
	void rangedConditionMatchesOnlyRangedKills() {
		KillMethodLootCondition condition = new KillMethodLootCondition(KillMethod.RANGED);
		assertTrue(matches(condition, KillMethod.RANGED));
		assertFalse(matches(condition, KillMethod.MELEE));
		assertFalse(matches(condition, KillMethod.FALLBACK));
	}

	private static boolean matches(KillMethodLootCondition condition, KillMethod method) {
		boolean[] result = {false};
		EliteLootRollContext.run(
				new EliteLootRollContext(method, false),
				() -> result[0] = condition.test(null)
		);
		return result[0];
	}
}
