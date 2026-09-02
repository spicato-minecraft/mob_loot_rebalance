package mob_loot_rebalance.elite;

import net.fabricmc.loader.api.FabricLoader;

import java.util.function.BooleanSupplier;

/**
 * Contract with combat and size JARs. This JAR always owns elite identity
 * (spawn roll, persistent flag, loot, XP, kill chime).
 *
 * <p>Fallback combat presentation (dust, 1.5× stats, creeper blast) applies only
 * when Mob Combat Rebalance is absent. Fallback 1.3× scale applies only when
 * neither MCR nor Mob Size Diversity is loaded.
 */
public final class EliteCombatHandshake {

	public static final String COMBAT_MOD_ID = "mob_combat_rebalance";
	public static final String SIZE_MOD_ID = "mob_size_diversity";

	private static BooleanSupplier combatModLoaded = EliteCombatHandshake::detectCombatMod;
	private static BooleanSupplier sizeModLoaded = EliteCombatHandshake::detectSizeMod;

	private EliteCombatHandshake() {
	}

	/** Dust, 1.5× stats, knockback, creeper blast — muted only by MCR. */
	public static boolean shouldApplyFallbackCombatStats() {
		return !combatModLoaded.getAsBoolean();
	}

	/** Binary 1.3× {@code SCALE} — muted by MCR or the size JAR (anti-stack). */
	public static boolean shouldApplyFallbackScale() {
		return !combatModLoaded.getAsBoolean() && !sizeModLoaded.getAsBoolean();
	}

	/**
	 * Full loot-only presentation including scale. Prefer the split methods.
	 * Kept for callers that still mean “MCR is absent” (particles follow combat stats).
	 */
	public static boolean shouldApplyFallbackPresentation() {
		return shouldApplyFallbackCombatStats();
	}

	public static void overrideCombatModLoadedForTest(BooleanSupplier override) {
		combatModLoaded = override != null ? override : EliteCombatHandshake::detectCombatMod;
	}

	public static void overrideSizeModLoadedForTest(BooleanSupplier override) {
		sizeModLoaded = override != null ? override : EliteCombatHandshake::detectSizeMod;
	}

	private static boolean detectCombatMod() {
		return FabricLoader.getInstance().isModLoaded(COMBAT_MOD_ID);
	}

	private static boolean detectSizeMod() {
		return FabricLoader.getInstance().isModLoaded(SIZE_MOD_ID);
	}
}
