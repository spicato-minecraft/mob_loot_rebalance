package mob_loot_rebalance.balance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HuntingNightCadenceModelTest {

	@Test
	void expectedBooksUsesSpawnChanceAndThemedBooksPerSlot() {
		assertEquals(1.0166666666666666, HuntingNightCadenceModel.EXPECTED_BOOKS_PER_BOOK_SLOT, 0.0001);
		assertEquals(0.23383333333333334, HuntingNightCadenceModel.EXPECTED_BOOKS_PER_ELITE, 0.0001);
		assertEquals(0.7015, HuntingNightCadenceModel.expectedBooks(100, 0.03), 0.0001);
		assertEquals(0.935333, HuntingNightCadenceModel.expectedBooks(100, 0.04), 0.0001);
		assertEquals(1.403, HuntingNightCadenceModel.expectedBooks(100, 0.06), 0.0001);
	}

	@Test
	void spawnChanceForTargetBooksInvertsExpectedFormula() {
		assertEquals(0.03, HuntingNightCadenceModel.spawnChanceForExpectedBooks(0.7015, 100), 0.0001);
		assertEquals(
				1.5 / (100 * HuntingNightCadenceModel.EXPECTED_BOOKS_PER_ELITE),
				HuntingNightCadenceModel.spawnChanceForExpectedBooks(1.5, 100),
				0.0001
		);
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
	void fourteenPercentDefaultOvershootsTargetBandAtTypicalDedicatedKillCounts() {
		double expected = HuntingNightCadenceModel.expectedBooks(100, 0.14);
		assertEquals(3.273667, expected, 0.0001);
		assertFalse(HuntingNightCadenceModel.isInTargetBand(expected));
	}

	@Test
	void monteCarloAtFourPercentCentersNearExpectedBooks() {
		HuntingNightCadenceModel.SimulationResult result = HuntingNightCadenceModel.simulateTrialNights(
				100,
				0.04,
				5_000,
				0xAC12L
		);
		assertEquals(5_000, result.trialNights());
		assertTrue(result.meanBooksPerNight() > 0.80 && result.meanBooksPerNight() < 1.10);
		assertTrue(result.fractionNightsWithAtLeastOneBook() > 0.45);
	}

	@Test
	void monteCarloSingleNightRespectsBounds() {
		java.util.Random random = new java.util.Random(42L);
		int books = HuntingNightCadenceModel.simulateSingleNight(50, 0.5, random);
		assertTrue(books >= 0 && books <= 50 * 2);
	}
}
