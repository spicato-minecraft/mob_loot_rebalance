package mob_loot_rebalance.config;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ElitesConfigJsonTest {

	@Test
	void uatGlowFalseInFileIsNotTreatedAsTrue() {
		String json = """
				{
				  "elites": {
				    "enabled": true,
				    "spawn_chance": 0.14,
				    "particle_interval_ticks": 8,
				    "uat_glow": false
				  }
				}
				""";
		ModConfig config = new Gson().fromJson(json, ModConfig.class);
		assertFalse(config.elites.uat_glow);
		assertEquals(8, config.elites.particle_interval_ticks);
	}
}
