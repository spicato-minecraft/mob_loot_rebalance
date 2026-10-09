package mob_loot_rebalance.elite;

/**
 * Client presentation gate: elite motes only while the local player is
 * looking through a spyglass. Creative / spectator skip the gate for UAT.
 */
public final class SpyglassParticleGate {

	private SpyglassParticleGate() {
	}

	public static boolean shouldShowToViewer(boolean scoping, boolean creative, boolean spectator) {
		return scoping || creative || spectator;
	}

	public static boolean shouldPulse(boolean viewerSees, boolean raisedThisTick, int mobTickCount, int interval) {
		if (!viewerSees || interval <= 0) {
			return false;
		}
		return raisedThisTick || mobTickCount % interval == 0;
	}
}
