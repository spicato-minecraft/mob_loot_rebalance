package mob_loot_rebalance.elite;

import mob_loot_rebalance.Mob_loot_rebalance;
import mob_loot_rebalance.config.ElitesConfig;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sparse ambient vanilla particles on elite mobs (AC-5 visual cue).
 * Spawned on the client only (see spyglass gate).
 */
public final class EliteAmbientParticles {

	private static final Logger LOGGER = LogManager.getLogger(Mob_loot_rebalance.MOD_ID + ".elite.particles");
	private static final Identifier DEFAULT_PARTICLE = Identifier.parse("minecraft:dust");
	private static final Identifier DUST_PARTICLE = Identifier.parse("minecraft:dust");

	private EliteAmbientParticles() {
	}

	public static void emit(Mob mob, Level level, ElitesConfig config) {
		if (!shouldEmit(config) || !level.isClientSide()) {
			return;
		}

		ParticleOptions particle = resolveOptions(config);
		RandomSource random = mob.getRandom();
		double width = mob.getBbWidth();
		double x = mob.getX() + (random.nextDouble() - 0.5) * width;
		double y = mob.getY() + random.nextDouble() * mob.getBbHeight() * 0.75 + 0.1;
		double z = mob.getZ() + (random.nextDouble() - 0.5) * width;
		level.addParticle(particle, true, true, x, y, z, 0.0, 0.05, 0.0);
		if (pairsFireflyGlow(config)) {
			level.addParticle(ParticleTypes.FIREFLY, true, true, x, y, z, 0.0, 0.02, 0.0);
		}
	}

	public static boolean shouldEmit(ElitesConfig config) {
		return config.particle_interval_ticks > 0 && config.ambient_particle != null && !config.ambient_particle.isBlank();
	}

	static boolean pairsFireflyGlow(ElitesConfig config) {
		return DUST_PARTICLE.equals(parseParticleId(config.ambient_particle));
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

		LOGGER.warn("Unsupported elite ambient particle {}; falling back to silvery dust", id);
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
