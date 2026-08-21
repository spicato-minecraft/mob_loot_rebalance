package mob_combat_rebalance.killmethod;

import mob_combat_rebalance.MinecraftTestBootstrap;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	void fallbackReturnsFallbackPoolOnly() {
		Identifier pool = KillMethodRouter.poolFor(KillMethod.FALLBACK, RandomSource.create(0));
		assertEquals(EliteBookPools.FALLBACK, pool);
	}

	@Test
	void meleeWeightedPoolsStayInScope() {
		Set<Identifier> seen = new HashSet<>();
		RandomSource random = RandomSource.create(42);
		for (int i = 0; i < 200; i++) {
			seen.add(KillMethodRouter.poolFor(KillMethod.MELEE, random));
		}

		assertEquals(
				Set.of(EliteBookPools.ARMOR, EliteBookPools.MELEE, EliteBookPools.MENDING),
				seen
		);
		assertFalse(seen.contains(EliteBookPools.RANGED));
		assertFalse(seen.contains(EliteBookPools.FALLBACK));
	}

	@Test
	void rangedWeightedPoolsPreferRangedOverArmor() {
		int ranged = 0;
		int armor = 0;
		RandomSource random = RandomSource.create(7);
		for (int i = 0; i < 600; i++) {
			Identifier pool = KillMethodRouter.poolFor(KillMethod.RANGED, random);
			if (pool.equals(EliteBookPools.RANGED)) {
				ranged++;
			} else if (pool.equals(EliteBookPools.ARMOR)) {
				armor++;
			}
		}

		assertTrue(ranged > armor, "Expected ~2:1 ranged:armor weighting");
	}

	@Test
	void routerNeverReturnsOutOfScopePoolIds() {
		Set<Identifier> allowed = Set.of(
				EliteBookPools.ARMOR,
				EliteBookPools.MELEE,
				EliteBookPools.RANGED,
				EliteBookPools.MENDING,
				EliteBookPools.FALLBACK
		);

		RandomSource random = RandomSource.create(99);
		for (KillMethod method : EnumSet.allOf(KillMethod.class)) {
			for (int i = 0; i < 50; i++) {
				assertTrue(allowed.contains(KillMethodRouter.poolFor(method, random)));
			}
		}
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
