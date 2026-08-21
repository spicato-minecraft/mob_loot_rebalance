package mob_combat_rebalance.balance;

/**
 * Analytical model for AC-12 hunting-night book cadence.
 * <p>
 * Expected books per dedicated night ≈ {@code eligibleKills × spawnChance × bookRollRate}.
 * Book roll rate is the elite-table {@code enchanting_book_roll} weight (25 / 100).
 */
public final class HuntingNightCadenceModel {

	public static final double BOOK_ROLL_RATE = 0.25;
	public static final double TARGET_BOOKS_MIN = 1.0;
	public static final double TARGET_BOOKS_MAX = 2.0;
	public static final double TARGET_BOOKS_MID = 1.5;

	private HuntingNightCadenceModel() {
	}

	public static double expectedBooks(int eligibleKills, double spawnChance) {
		if (eligibleKills <= 0 || spawnChance <= 0.0) {
			return 0.0;
		}
		return eligibleKills * spawnChance * BOOK_ROLL_RATE;
	}

	public static double expectedElites(int eligibleKills, double spawnChance) {
		if (eligibleKills <= 0 || spawnChance <= 0.0) {
			return 0.0;
		}
		return eligibleKills * spawnChance;
	}

	public static double spawnChanceForExpectedBooks(double expectedBooks, int eligibleKills) {
		if (eligibleKills <= 0 || expectedBooks <= 0.0) {
			return 0.0;
		}
		return expectedBooks / (eligibleKills * BOOK_ROLL_RATE);
	}

	public static boolean isInTargetBand(double expectedBooks) {
		return expectedBooks >= TARGET_BOOKS_MIN && expectedBooks <= TARGET_BOOKS_MAX;
	}

	/**
	 * Monte Carlo simulation of book drops across many trial nights (binomial elites × Bernoulli book roll).
	 */
	public static SimulationResult simulateTrialNights(
			int eligibleKillsPerNight,
			double spawnChance,
			int trialNights,
			long seed
	) {
		if (trialNights <= 0) {
			return new SimulationResult(0, 0, 0, 0, 0, 0, 0.0, 0.0);
		}

		java.util.Random random = new java.util.Random(seed);
		int totalBooks = 0;
		int nightsWithAtLeastOneBook = 0;
		int nightsWithAtLeastTwoBooks = 0;
		int minBooks = Integer.MAX_VALUE;
		int maxBooks = 0;

		for (int night = 0; night < trialNights; night++) {
			int books = simulateSingleNight(eligibleKillsPerNight, spawnChance, random);
			totalBooks += books;
			minBooks = Math.min(minBooks, books);
			maxBooks = Math.max(maxBooks, books);
			if (books >= 1) {
				nightsWithAtLeastOneBook++;
			}
			if (books >= 2) {
				nightsWithAtLeastTwoBooks++;
			}
		}

		return new SimulationResult(
				trialNights,
				totalBooks,
				nightsWithAtLeastOneBook,
				nightsWithAtLeastTwoBooks,
				minBooks == Integer.MAX_VALUE ? 0 : minBooks,
				maxBooks,
				(double) totalBooks / trialNights,
				(double) nightsWithAtLeastOneBook / trialNights
		);
	}

	static int simulateSingleNight(int eligibleKills, double spawnChance, java.util.Random random) {
		int books = 0;
		for (int kill = 0; kill < eligibleKills; kill++) {
			if (random.nextDouble() >= spawnChance) {
				continue;
			}
			if (random.nextDouble() < BOOK_ROLL_RATE) {
				books++;
			}
		}
		return books;
	}

	public record SimulationResult(
			int trialNights,
			int totalBooks,
			int nightsWithAtLeastOneBook,
			int nightsWithAtLeastTwoBooks,
			int minBooksPerNight,
			int maxBooksPerNight,
			double meanBooksPerNight,
			double fractionNightsWithAtLeastOneBook
	) {
	}
}
