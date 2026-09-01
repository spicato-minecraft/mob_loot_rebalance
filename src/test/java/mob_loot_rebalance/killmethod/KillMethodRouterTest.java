package mob_loot_rebalance.killmethod;

import mob_loot_rebalance.MinecraftTestBootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KillMethodRouterTest {

	@BeforeAll
	static void bootstrapMinecraft() {
		MinecraftTestBootstrap.init();
	}

	@Test
	void environmentalKillWithRecentSwordHitUsesMeleeHistory() {
		PlayerDamageHistory history = new PlayerDamageHistory();
		history.record(80, KillMethod.MELEE, 100);

		KillMethod method = KillMethodRouter.resolveKillMethod(
				100,
				100,
				history,
				Optional.empty()
		);

		assertEquals(KillMethod.MELEE, method);
	}

	@Test
	void environmentalKillWithoutPlayerDamageUsesFallback() {
		PlayerDamageHistory history = new PlayerDamageHistory();

		KillMethod method = KillMethodRouter.resolveKillMethod(
				100,
				100,
				history,
				Optional.empty()
		);

		assertEquals(KillMethod.FALLBACK, method);
	}

	@Test
	void killingBlowTakesPriorityOverOlderHistory() {
		PlayerDamageHistory history = new PlayerDamageHistory();
		history.record(50, KillMethod.MELEE, 100);

		KillMethod method = KillMethodRouter.resolveKillMethod(
				100,
				100,
				history,
				Optional.of(KillMethod.RANGED)
		);

		assertEquals(KillMethod.RANGED, method);
	}

	@Test
	void expiredHistoryOutsideLookbackFallsBack() {
		PlayerDamageHistory history = new PlayerDamageHistory();
		history.record(0, KillMethod.MELEE, 100);

		KillMethod method = KillMethodRouter.resolveKillMethod(
				200,
				100,
				history,
				Optional.empty()
		);

		assertEquals(KillMethod.FALLBACK, method);
	}

	@Test
	void playerDamageHistoryTracksWindow() {
		PlayerDamageHistory history = new PlayerDamageHistory();
		history.record(95, KillMethod.RANGED, 100);
		history.record(40, KillMethod.MELEE, 100);

		assertEquals(Optional.of(KillMethod.RANGED), history.mostRecentInWindow(100, 100));
		assertEquals(Optional.empty(), history.mostRecentInWindow(200, 100));
	}
}
