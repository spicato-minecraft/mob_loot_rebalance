package mob_loot_rebalance.elite;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EliteCombatHandshakeTest {

	@AfterEach
	void resetHandshake() {
		EliteCombatHandshake.overrideCombatModLoadedForTest(null);
		EliteCombatHandshake.overrideSizeModLoadedForTest(null);
	}

	@Test
	void lootOnlyAppliesScaleAndCombatStats() {
		EliteCombatHandshake.overrideCombatModLoadedForTest(() -> false);
		EliteCombatHandshake.overrideSizeModLoadedForTest(() -> false);
		assertTrue(EliteCombatHandshake.shouldApplyFallbackScale());
		assertTrue(EliteCombatHandshake.shouldApplyFallbackCombatStats());
		assertTrue(EliteCombatHandshake.shouldApplyFallbackPresentation());
	}

	@Test
	void sizeJarMutesScaleOnly() {
		EliteCombatHandshake.overrideCombatModLoadedForTest(() -> false);
		EliteCombatHandshake.overrideSizeModLoadedForTest(() -> true);
		assertFalse(EliteCombatHandshake.shouldApplyFallbackScale());
		assertTrue(EliteCombatHandshake.shouldApplyFallbackCombatStats());
		assertTrue(EliteCombatHandshake.shouldApplyFallbackPresentation());
	}

	@Test
	void combatJarMutesFullFallback() {
		EliteCombatHandshake.overrideCombatModLoadedForTest(() -> true);
		EliteCombatHandshake.overrideSizeModLoadedForTest(() -> false);
		assertFalse(EliteCombatHandshake.shouldApplyFallbackScale());
		assertFalse(EliteCombatHandshake.shouldApplyFallbackCombatStats());
		assertFalse(EliteCombatHandshake.shouldApplyFallbackPresentation());
	}

	@Test
	void combatAndSizeMuteFullFallback() {
		EliteCombatHandshake.overrideCombatModLoadedForTest(() -> true);
		EliteCombatHandshake.overrideSizeModLoadedForTest(() -> true);
		assertFalse(EliteCombatHandshake.shouldApplyFallbackScale());
		assertFalse(EliteCombatHandshake.shouldApplyFallbackCombatStats());
		assertFalse(EliteCombatHandshake.shouldApplyFallbackPresentation());
	}
}
