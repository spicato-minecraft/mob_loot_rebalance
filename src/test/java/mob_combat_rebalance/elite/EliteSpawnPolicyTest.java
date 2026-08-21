package mob_combat_rebalance.elite;

import net.minecraft.world.entity.EntitySpawnReason;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EliteSpawnPolicyTest {

	private static final Set<EntitySpawnReason> ALLOWED = Set.of(
			EntitySpawnReason.NATURAL,
			EntitySpawnReason.CHUNK_GENERATION
	);

	private static final Set<EntitySpawnReason> EXPLICITLY_DENIED = Set.of(
			EntitySpawnReason.STRUCTURE,
			EntitySpawnReason.TRIGGERED,
			EntitySpawnReason.SPAWNER,
			EntitySpawnReason.TRIAL_SPAWNER,
			EntitySpawnReason.REINFORCEMENT,
			EntitySpawnReason.PATROL,
			EntitySpawnReason.CONVERSION,
			EntitySpawnReason.EVENT
	);

	@Test
	void classifiesEverySpawnReason() {
		for (EntitySpawnReason reason : EntitySpawnReason.values()) {
			boolean eligible = EliteSpawnPolicy.isEligibleForEliteRoll(reason);
			if (ALLOWED.contains(reason)) {
				assertTrue(eligible, reason + " should allow elite roll");
			} else {
				assertFalse(eligible, reason + " should deny elite roll");
			}
		}
	}

	@Test
	void explicitlyDeniedReasonsAreBlocked() {
		for (EntitySpawnReason reason : EXPLICITLY_DENIED) {
			assertFalse(
					EliteSpawnPolicy.isEligibleForEliteRoll(reason),
					reason + " is explicitly denied in v1 locked rules"
			);
		}
	}

	@Test
	void vanillaIsSpawnerReasonsAreBlocked() {
		for (EntitySpawnReason reason : EntitySpawnReason.values()) {
			if (EntitySpawnReason.isSpawner(reason)) {
				assertFalse(
						EliteSpawnPolicy.isEligibleForEliteRoll(reason),
						reason + " is a spawner reason and must deny elite roll"
				);
			}
		}
	}

	@Test
	void spawnerAndTrialSpawnerUseVanillaIsSpawnerHelper() {
		assertTrue(EntitySpawnReason.isSpawner(EntitySpawnReason.SPAWNER));
		assertTrue(EntitySpawnReason.isSpawner(EntitySpawnReason.TRIAL_SPAWNER));
		assertFalse(EntitySpawnReason.isSpawner(EntitySpawnReason.NATURAL));
	}

	@Test
	void allSpawnReasonValuesAreAccountedFor() {
		Set<EntitySpawnReason> all = Arrays.stream(EntitySpawnReason.values()).collect(Collectors.toSet());
		Set<EntitySpawnReason> denied = all.stream()
				.filter(reason -> !ALLOWED.contains(reason))
				.collect(Collectors.toSet());

		assertEquals(all.size(), ALLOWED.size() + denied.size());
		assertEquals(2, ALLOWED.size(), "v1 allows exactly NATURAL and CHUNK_GENERATION");
		assertTrue(denied.containsAll(EXPLICITLY_DENIED));
	}
}
