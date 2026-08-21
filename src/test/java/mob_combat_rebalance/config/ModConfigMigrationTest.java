package mob_combat_rebalance.config;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModConfigMigrationTest {

	@Test
	void applyDefaultsForMissingKeys_fillsAllSectionsFromEmptyFile() throws Exception {
		JsonObject root = new JsonObject();

		ModConfig config = new ModConfig();
		boolean addedKeys = invokeMigration(root, config);

		assertTrue(addedKeys);
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
	void applyDefaultsForMissingKeys_preservesProvidedNestedKeys() throws Exception {
		JsonObject root = new JsonObject();
		JsonObject xp = new JsonObject();
		xp.addProperty("enabled", false);
		xp.addProperty("multiplier", 2.0);
		root.add("xp", xp);

		JsonObject elites = new JsonObject();
		elites.addProperty("spawn_chance", 0.5);
		root.add("elites", elites);

		ModConfig config = new ModConfig();
		config.xp.enabled = false;
		config.xp.multiplier = 2.0;
		config.elites.enabled = false;
		config.elites.spawn_chance = 0.5;

		boolean addedKeys = invokeMigration(root, config);

		assertTrue(addedKeys);
		assertFalse(config.xp.enabled);
		assertEquals(2.0, config.xp.multiplier, 0.0001);
		assertTrue(config.elites.enabled, "missing elites.enabled merges default true");
		assertEquals(0.5, config.elites.spawn_chance, 0.0001);
	}

	private static boolean invokeMigration(JsonObject root, ModConfig config) throws Exception {
		Method migrate = ModConfig.class.getDeclaredMethod("applyDefaultsForMissingKeys", JsonObject.class, ModConfig.class);
		migrate.setAccessible(true);
		return (boolean) migrate.invoke(null, root, config);
	}
}
