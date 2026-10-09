package mob_loot_rebalance.elite;

import net.minecraft.client.player.LocalPlayer;

/**
 * Tracks spyglass raise across elite {@code aiStep} calls in the same client tick
 * so every visible elite can pulse once when the overlay comes up.
 */
public final class SpyglassParticleFrame {

	private static int lastPlayerTick = Integer.MIN_VALUE;
	private static boolean wasShowing;
	private static boolean showing;
	private static boolean raisedThisTick;

	private SpyglassParticleFrame() {
	}

	public static void capture(LocalPlayer player) {
		int tick = player.tickCount;
		if (tick == lastPlayerTick) {
			return;
		}
		lastPlayerTick = tick;
		showing = SpyglassParticleGate.shouldShowToViewer(
				player.isScoping(),
				player.isCreative(),
				player.isSpectator()
		);
		raisedThisTick = showing && !wasShowing;
		wasShowing = showing;
	}

	public static boolean showing() {
		return showing;
	}

	public static boolean raisedThisTick() {
		return raisedThisTick;
	}
}
