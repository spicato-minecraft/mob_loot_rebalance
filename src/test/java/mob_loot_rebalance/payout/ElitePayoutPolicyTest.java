package mob_loot_rebalance.payout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElitePayoutPolicyTest {

	@Test
	void allowsPlayerKilledEliteWhenPillarActive() {
		assertTrue(ElitePayoutPolicy.isPlayerKilledElite(true, true, true, true));
	}

	@Test
	void deniesWhenElitesPillarDisabled() {
		assertFalse(ElitePayoutPolicy.isPlayerKilledElite(false, true, true, true));
	}

	@Test
	void deniesNonPlayerKill() {
		assertFalse(ElitePayoutPolicy.isPlayerKilledElite(true, true, true, false));
	}

	@Test
	void deniesNonEliteMob() {
		assertFalse(ElitePayoutPolicy.isPlayerKilledElite(true, false, true, true));
	}

	@Test
	void deniesIneligibleMobType() {
		assertFalse(ElitePayoutPolicy.isPlayerKilledElite(true, true, false, true));
	}
}
