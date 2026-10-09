package mob_loot_rebalance.elite;

/**
 * Elite ambient glow uses vanilla {@link net.minecraft.core.particles.ParticleTypes#FIREFLY} on 26.1+.
 * Shorter custom lifetimes are not available without a client factory; emit interval stays sparse.
 */
public final class EliteParticleTypes {

	/** Target mote lifetime when using a modded short-lived firefly (reference for tuning interval). */
	public static final int ELITE_FIREFLY_LIFETIME_TICKS = 40;

	private EliteParticleTypes() {
	}
}
