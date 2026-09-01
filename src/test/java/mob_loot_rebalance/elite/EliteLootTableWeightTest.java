package mob_loot_rebalance.elite;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validates default elite mob loot tables ship with weights summing to 100.
 */
class EliteLootTableWeightTest {

	private static final int EXPECTED_WEIGHT_TOTAL = 100;

	private static final List<String> MOB_TABLES = List.of(
			"zombie",
			"husk",
			"drowned",
			"zombie_villager",
			"skeleton",
			"stray",
			"bogged",
			"parched",
			"creeper",
			"spider",
			"cave_spider",
			"witch",
			"phantom",
			"enderman",
			"slime",
			"silverfish",
			"endermite",
			"guardian",
			"elder_guardian",
			"shulker",
			"ghast",
			"blaze",
			"magma_cube",
			"wither_skeleton",
			"piglin",
			"piglin_brute",
			"hoglin",
			"zoglin",
			"zombified_piglin",
			"pillager",
			"vindicator",
			"evoker",
			"vex",
			"ravager",
			"breeze"
	);

	@Test
	void mobTableWeightsSumTo100() {
		for (String mobId : MOB_TABLES) {
			JsonObject table = loadLootTable("data/mob_loot_rebalance/loot_table/elite/" + mobId + ".json");
			JsonArray pools = table.getAsJsonArray("pools");
			assertNotNull(pools, mobId + " must define pools");
			assertEquals(1, pools.size(), mobId + " elite table uses a single rolls:1 pool");

			JsonObject pool = pools.get(0).getAsJsonObject();
			assertEquals(1, pool.get("rolls").getAsInt(), mobId + " pool must roll exactly once");

			JsonArray entries = pool.getAsJsonArray("entries");
			assertNotNull(entries, mobId + " pool must define entries");
			assertFalse(entries.isEmpty(), mobId + " pool must not be empty");

			int totalWeight = 0;
			for (JsonElement entryElement : entries) {
				JsonObject entry = entryElement.getAsJsonObject();
				int weight = entry.has("weight") ? entry.get("weight").getAsInt() : 1;
				assertTrue(weight > 0, mobId + " entry weights must be positive");
				totalWeight += weight;
			}

			assertEquals(EXPECTED_WEIGHT_TOTAL, totalWeight, mobId + " elite table weights must sum to 100");
		}
	}

	private static JsonObject loadLootTable(String resourcePath) {
		InputStream stream = EliteLootTableWeightTest.class.getClassLoader().getResourceAsStream(resourcePath);
		assertNotNull(stream, "Missing loot table resource: " + resourcePath);
		try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
			return JsonParser.parseReader(reader).getAsJsonObject();
		} catch (Exception e) {
			throw new AssertionError("Failed to parse loot table: " + resourcePath, e);
		}
	}
}
