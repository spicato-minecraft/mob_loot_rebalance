package mob_combat_rebalance.balance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HuntingNightCadenceModelTest {

	@Test
	void expectedBooksUsesSpawnChanceAndBookRollRate() {
		assertEquals(0.75, HuntingNightCadenceModel.expectedBooks(100, 0.03), 0.0001);
		assertEquals(1.0, HuntingNightCadenceModel.expectedBooks(100, 0.04), 0.0001);
		assertEquals(1.5, HuntingNightCadenceModel.expectedBooks(100, 0.06), 0.0001);
	}

	@Test
	void spawnChanceForTargetBooksInvertsExpectedFormula() {
		assertEquals(0.04, HuntingNightCadenceModel.spawnChanceForExpectedBooks(1.0, 100), 0.0001);
		assertEquals(0.06, HuntingNightCadenceModel.spawnChanceForExpectedBooks(1.5, 100), 0.0001);
	}

	@Test
	void targetBandCoversOneToTwoBooks() {
		assertFalse(HuntingNightCadenceModel.isInTargetBand(0.75));
		assertTrue(HuntingNightCadenceModel.isInTargetBand(1.0));
		assertTrue(HuntingNightCadenceModel.isInTargetBand(1.5));
		assertTrue(HuntingNightCadenceModel.isInTargetBand(2.0));
		assertFalse(HuntingNightCadenceModel.isInTargetBand(2.1));
	}

	@Test
	void sevenPercentDefaultIsInTargetBandAtTypicalDedicatedKillCounts() {
		assertTrue(HuntingNightCadenceModel.isInTargetBand(
				HuntingNightCadenceModel.expectedBooks(100, 0.07)
		));
		assertEquals(1.75, HuntingNightCadenceModel.expectedBooks(100, 0.07), 0.0001);
	}

	@Test
	void monteCarloAtFourPercentCentersNearOneBookPerNight() {
		HuntingNightCadenceModel.SimulationResult result = HuntingNightCadenceModel.simulateTrialNights(
				100,
				0.04,
				5_000,
				0xAC12L
		);
		assertEquals(5_000, result.trialNights());
		assertTrue(result.meanBooksPerNight() > 0.85 && result.meanBooksPerNight() < 1.15);
		assertTrue(result.fractionNightsWithAtLeastOneBook() > 0.55);
	}

	@Test
	void monteCarloSingleNightRespectsBounds() {
		java.util.Random random = new java.util.Random(42L);
		int books = HuntingNightCadenceModel.simulateSingleNight(50, 0.5, random);
		assertTrue(books >= 0 && books <= 50);
	}
}
