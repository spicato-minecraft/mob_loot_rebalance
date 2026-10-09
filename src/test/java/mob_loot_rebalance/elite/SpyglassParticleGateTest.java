package mob_loot_rebalance.elite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpyglassParticleGateTest {

	@Test
	void survivalNeedsSpyglass() {
		assertFalse(SpyglassParticleGate.shouldShowToViewer(false, false, false));
		assertTrue(SpyglassParticleGate.shouldShowToViewer(true, false, false));
	}

	@Test
	void creativeAndSpectatorSkipSpyglass() {
		assertTrue(SpyglassParticleGate.shouldShowToViewer(false, true, false));
		assertTrue(SpyglassParticleGate.shouldShowToViewer(false, false, true));
	}

	@Test
	void pulseOnIntervalOrWhenRaisingSpyglass() {
		assertFalse(SpyglassParticleGate.shouldPulse(false, true, 60, 60));
		assertTrue(SpyglassParticleGate.shouldPulse(true, false, 60, 60));
		assertFalse(SpyglassParticleGate.shouldPulse(true, false, 61, 60));
		assertTrue(SpyglassParticleGate.shouldPulse(true, true, 61, 60));
		assertFalse(SpyglassParticleGate.shouldPulse(true, false, 60, 0));
	}
}
