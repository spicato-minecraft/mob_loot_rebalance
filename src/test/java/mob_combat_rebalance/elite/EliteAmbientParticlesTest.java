package mob_combat_rebalance.elite;

import mob_combat_rebalance.MinecraftTestBootstrap;
import mob_combat_rebalance.config.ElitesConfig;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EliteAmbientParticlesTest {

	@BeforeAll
	static void bootstrapMinecraft() {
		MinecraftTestBootstrap.init();
	}

	@Test
	void resolvesRedDustByDefault() {
		ElitesConfig config = new ElitesConfig();
		assertInstanceOf(DustParticleOptions.class, EliteAmbientParticles.resolveOptions(config));
		DustParticleOptions dust = (DustParticleOptions) EliteAmbientParticles.resolveOptions(config);
		assertEquals(1.0F, dust.getColor().x(), 0.01F);
		assertEquals(0.0F, dust.getColor().y(), 0.01F);
		assertEquals(0.0F, dust.getColor().z(), 0.01F);
	}

	@Test
	void resolvesSimpleParticlesWhenConfigured() {
		ElitesConfig config = new ElitesConfig();
		config.ambient_particle = "minecraft:soul";
		assertSame(ParticleTypes.SOUL, EliteAmbientParticles.resolveOptions(config));
	}

	@Test
	void fallsBackToRedDustForUnknownParticle() {
		ElitesConfig config = new ElitesConfig();
		config.ambient_particle = "minecraft:not_a_real_particle";
		assertInstanceOf(DustParticleOptions.class, EliteAmbientParticles.resolveOptions(config));
	}

	@Test
	void shouldEmitWhenConfigured() {
		ElitesConfig config = new ElitesConfig();
		assertTrue(EliteAmbientParticles.shouldEmit(config));

		config.particle_interval_ticks = 0;
		assertFalse(EliteAmbientParticles.shouldEmit(config));

		config.particle_interval_ticks = 30;
		config.ambient_particle = " ";
		assertFalse(EliteAmbientParticles.shouldEmit(config));
	}
}
