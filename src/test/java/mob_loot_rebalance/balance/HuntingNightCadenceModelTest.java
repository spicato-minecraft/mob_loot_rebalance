package mob_loot_rebalance.balance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HuntingNightCadenceModelTest {

	@Test
	void expectedBooksUsesSpawnChanceAndThemedBooksPerSlot() {
		assertEquals(1.0166666666666666, HuntingNightCadenceModel.EXPECTED_BOOKS_PER_BOOK_SLOT, 0.0001);
		assertEquals(0.3355, HuntingNightCadenceModel.EXPECTED_BOOKS_PER_ELITE, 0.0001);
		assertEquals(1.0065, HuntingNightCadenceModel.expectedBooks(100, 0.03), 0.0001);
		assertEquals(1.342, HuntingNightCadenceModel.expectedBooks(100, 0.04), 0.0001);
		assertEquals(2.013, HuntingNightCadenceModel.expectedBooks(100, 0.06), 0.0001);
	}

	@Test
	void spawnChanceForTargetBooksInvertsExpectedFormula() {
		assertEquals(0.03, HuntingNightCadenceModel.spawnChanceForExpectedBooks(1.0065, 100), 0.0001);
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
		assertEquals(4.697, expected, 0.0001);
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
		assertTrue(result.meanBooksPerNight() > 1.20 && result.meanBooksPerNight() < 1.50);
		assertTrue(result.fractionNightsWithAtLeastOneBook() > 0.55);
	}

	@Test
	void monteCarloSingleNightRespectsBounds() {
		java.util.Random random = new java.util.Random(42L);
		int books = HuntingNightCadenceModel.simulateSingleNight(50, 0.5, random);
		assertTrue(books >= 0 && books <= 50 * 2);
	}
}
