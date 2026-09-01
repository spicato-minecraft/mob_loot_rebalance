package mob_loot_rebalance.elite;

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
			EntitySpawnReason.CHUNK_GENERATION,
			EntitySpawnReason.STRUCTURE,
			EntitySpawnReason.SPAWNER,
			EntitySpawnReason.TRIAL_SPAWNER,
			EntitySpawnReason.PATROL
	);

	private static final Set<EntitySpawnReason> EXPLICITLY_DENIED = Set.of(
			EntitySpawnReason.TRIGGERED,
			EntitySpawnReason.REINFORCEMENT,
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
					reason + " is denied for elite roll"
			);
		}
	}

	@Test
	void eventConversionAndReinforcementAreDenied() {
		assertFalse(EliteSpawnPolicy.isEligibleForEliteRoll(EntitySpawnReason.EVENT));
		assertFalse(EliteSpawnPolicy.isEligibleForEliteRoll(EntitySpawnReason.CONVERSION));
		assertFalse(EliteSpawnPolicy.isEligibleForEliteRoll(EntitySpawnReason.REINFORCEMENT));
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
		assertEquals(6, ALLOWED.size(), "allows NATURAL, CHUNK_GENERATION, STRUCTURE, SPAWNER, TRIAL_SPAWNER, PATROL");
		assertTrue(denied.containsAll(EXPLICITLY_DENIED));
	}
}
