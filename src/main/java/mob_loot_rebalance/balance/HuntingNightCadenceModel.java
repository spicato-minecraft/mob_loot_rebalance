package mob_loot_rebalance.balance;

/**
 * Analytical model for AC-12 hunting-night book cadence.
 * <p>
 * Book <em>slot</em> rate is {@link #BOOK_SLOT_RATE} (23 / 100 on the elite table).
 * A slot rolls core once (armor or weapon or rare Mending) and theme once:
 * core {@code 2/3} on a melee/ranged kill with equal empty/armor/weapon weight,
 * theme 0.35 → {@link #EXPECTED_BOOKS_PER_BOOK_SLOT} ≈ 1.0167. Cap is two books.
 * <p>
 * {@code expectedBooks ≈ eligibleKills × spawnChance × BOOK_SLOT_RATE × EXPECTED_BOOKS_PER_BOOK_SLOT}
 */
public final class HuntingNightCadenceModel {

	public static final double BOOK_SLOT_RATE = 0.23;
	/** Planning default: melee/ranged kill, empty:armor:weapon share core weight. */
	public static final double P_CORE = 2.0 / 3.0;
	public static final double P_THEME = 0.35;
	public static final double EXPECTED_BOOKS_PER_BOOK_SLOT = P_CORE + P_THEME;
	public static final double EXPECTED_BOOKS_PER_ELITE = BOOK_SLOT_RATE * EXPECTED_BOOKS_PER_BOOK_SLOT;
	public static final double TARGET_BOOKS_MIN = 1.0;
	public static final double TARGET_BOOKS_MAX = 2.0;
	public static final double TARGET_BOOKS_MID = 1.5;

	private HuntingNightCadenceModel() {
	}

	public static double expectedBooks(int eligibleKills, double spawnChance) {
		if (eligibleKills <= 0 || spawnChance <= 0.0) {
			return 0.0;
		}
		return eligibleKills * spawnChance * EXPECTED_BOOKS_PER_ELITE;
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
		return expectedBooks / (eligibleKills * EXPECTED_BOOKS_PER_ELITE);
	}

	public static boolean isInTargetBand(double expectedBooks) {
		return expectedBooks >= TARGET_BOOKS_MIN && expectedBooks <= TARGET_BOOKS_MAX;
	}

	/**
	 * Monte Carlo of book drops: binomial elites × Bernoulli book slot × independent inner book rolls.
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

	/**
	 * One night under melee/ranged defaults (one core roll + one theme roll).
	 */
	static int simulateSingleNight(int eligibleKills, double spawnChance, java.util.Random random) {
		int books = 0;
		for (int kill = 0; kill < eligibleKills; kill++) {
			if (random.nextDouble() >= spawnChance) {
				continue;
			}
			if (random.nextDouble() >= BOOK_SLOT_RATE) {
				continue;
			}
			if (random.nextDouble() < P_CORE) {
				books++;
			}
			if (random.nextDouble() < P_THEME) {
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
