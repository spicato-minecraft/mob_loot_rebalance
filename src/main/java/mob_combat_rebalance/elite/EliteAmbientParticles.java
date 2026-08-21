package mob_combat_rebalance.elite;

import mob_combat_rebalance.Mob_combat_rebalance;
import mob_combat_rebalance.config.ElitesConfig;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sparse ambient vanilla particles on elite mobs (AC-5 visual cue).
 */
public final class EliteAmbientParticles {

	private static final Logger LOGGER = LogManager.getLogger(Mob_combat_rebalance.MOD_ID + ".elite.particles");
	private static final Identifier DEFAULT_PARTICLE = Identifier.parse("minecraft:dust");
	private static final Identifier DUST_PARTICLE = Identifier.parse("minecraft:dust");

	private EliteAmbientParticles() {
	}

	public static void emit(Mob mob, ServerLevel level, ElitesConfig config) {
		if (!shouldEmit(config)) {
			return;
		}

		ParticleOptions particle = resolveOptions(config);
		RandomSource random = level.random;
		double width = mob.getBbWidth();
		double x = mob.getX() + (random.nextDouble() - 0.5) * width;
		double y = mob.getY() + random.nextDouble() * mob.getBbHeight() * 0.75 + 0.1;
		double z = mob.getZ() + (random.nextDouble() - 0.5) * width;
		level.sendParticles(particle, true, false, x, y, z, 1, 0.0, 0.05, 0.0, 0.0);
	}

	public static boolean shouldEmit(ElitesConfig config) {
		return config.particle_interval_ticks > 0 && config.ambient_particle != null && !config.ambient_particle.isBlank();
	}

	static ParticleOptions resolveOptions(ElitesConfig config) {
		Identifier id = parseParticleId(config.ambient_particle);
		if (DUST_PARTICLE.equals(id)) {
			return dustOptions(config);
		}

		ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.getValue(id);
		if (particleType instanceof SimpleParticleType simple) {
			return simple;
		}

		LOGGER.warn("Unsupported elite ambient particle {}; falling back to red dust", id);
		return dustOptions(config);
	}

	static DustParticleOptions dustOptions(ElitesConfig config) {
		return new DustParticleOptions(config.particle_dust_color, config.particle_dust_scale);
	}

	private static Identifier parseParticleId(String configuredParticle) {
		if (configuredParticle == null || configuredParticle.isBlank()) {
			return DEFAULT_PARTICLE;
		}
		return Identifier.parse(configuredParticle.trim());
	}
}
