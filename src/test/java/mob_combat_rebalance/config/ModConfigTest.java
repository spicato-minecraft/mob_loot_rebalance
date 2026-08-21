package mob_combat_rebalance.config;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModConfigTest {

	@BeforeEach
	void resetInstance() {
		ModConfig.set(null);
	}

	@Test
	void defaultsMatchV1Spec() {
		ModConfig config = ModConfig.defaults();

		assertTrue(config.xp.enabled);
		assertEquals(1.35, config.xp.multiplier, 0.0001);
		assertTrue(config.elites.enabled);
		assertEquals(0.07, config.elites.spawn_chance, 0.0001);
		assertEquals("minecraft:dust", config.elites.ambient_particle);
		assertEquals(0xFFFF0000, config.elites.particle_dust_color);
		assertEquals(0.4F, config.elites.particle_dust_scale, 0.0001F);
		assertEquals(30, config.elites.particle_interval_ticks);
		assertFalse(config.elites.uat_glow);
		assertTrue(config.elite_loot.enabled);
		assertEquals(100, config.kill_method.lookback_ticks);
		assertFalse(config.combat_potion.allow_invisibility);
		assertEquals("minecraft:block.amethyst_block.chime", config.feedback.elite_kill_sound);
	}

	@Test
	void pillarTogglesAreIndependent() {
		ModConfig config = ModConfig.defaults();

		config.xp.enabled = false;
		assertFalse(config.isXpPillarActive());
		assertTrue(config.isElitesPillarActive());
		assertTrue(config.isEliteLootPillarActive());

		config.xp.enabled = true;
		config.elites.enabled = false;
		assertFalse(config.isElitesPillarActive());

		config.elites.enabled = true;
		config.elites.spawn_chance = 0.0;
		assertFalse(config.isElitesPillarActive(), "0% spawn chance disables elite pillar");

		config.elites.spawn_chance = 0.07;
		config.elite_loot.enabled = false;
		assertFalse(config.isEliteLootPillarActive());
	}

	@Test
	void setForTestOverridesRuntimeConfig() {
		ModConfig testConfig = ModConfig.defaults();
		testConfig.xp.multiplier = 2.0;

		ModConfig.setForTest(testConfig);
		assertSame(testConfig, ModConfig.get());
		assertEquals(2.0, ModConfig.get().xp.multiplier, 0.0001);
	}

	@Test
	void normalizeClampsInvalidValues() {
		ModConfig config = ModConfig.defaults();
		config.xp.multiplier = -1.0;
		config.elites.spawn_chance = 2.5;
		config.kill_method.lookback_ticks = -10;
		config.feedback.elite_kill_sound = "  ";

		ModConfig.normalize(config);

		assertEquals(0.0, config.xp.multiplier, 0.0001);
		assertEquals(1.0, config.elites.spawn_chance, 0.0001);
		assertEquals(0, config.kill_method.lookback_ticks);
		assertEquals("minecraft:block.amethyst_block.chime", config.feedback.elite_kill_sound);
	}
}
