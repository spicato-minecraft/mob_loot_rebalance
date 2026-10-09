package mob_loot_rebalance.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ModConfigReloadTest {

	@AfterEach
	void resetInstance() {
		ModConfig.set(null);
	}

	@Test
	void reloadFromReplacesInMemoryWithoutRestart(@TempDir Path tempDir) {
		ModConfig first = ModConfig.defaults();
		first.elites.spawn_chance = 0.99;
		first.saveTo(tempDir.resolve("mob_loot_rebalance.json"));

		ModConfig loaded = ModConfig.reloadFrom(tempDir);
		assertEquals(0.99, loaded.elites.spawn_chance, 0.0001);
		assertEquals(0.99, ModConfig.get().elites.spawn_chance, 0.0001);

		ModConfig second = ModConfig.defaults();
		second.elites.spawn_chance = 0.01;
		second.saveTo(tempDir.resolve("mob_loot_rebalance.json"));

		ModConfig reloaded = ModConfig.reloadFrom(tempDir);
		assertNotSame(loaded, reloaded);
		assertEquals(0.01, ModConfig.get().elites.spawn_chance, 0.0001);
		assertEquals(0.01, reloaded.elites.spawn_chance, 0.0001);
	}

	@Test
	void reloadFromSeesDiskEvenIfGetCachedACopy(@TempDir Path tempDir) {
		ModConfig onDisk = ModConfig.defaults();
		onDisk.elites.spawn_chance = 0.5;
		onDisk.saveTo(tempDir.resolve("mob_loot_rebalance.json"));
		ModConfig.reloadFrom(tempDir);

		ModConfig drifted = ModConfig.defaults();
		drifted.elites.spawn_chance = 0.0;
		ModConfig.setForTest(drifted);
		assertEquals(0.0, ModConfig.get().elites.spawn_chance, 0.0001);

		ModConfig.reloadFrom(tempDir);
		assertEquals(0.5, ModConfig.get().elites.spawn_chance, 0.0001);
	}
}
