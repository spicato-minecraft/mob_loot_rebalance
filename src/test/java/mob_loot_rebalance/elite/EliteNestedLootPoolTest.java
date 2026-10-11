package mob_loot_rebalance.elite;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mob_loot_rebalance.killmethod.KillMethod;
import mob_loot_rebalance.killmethod.ThemeEnchantmentKillMethod;
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
 * Nested elite book/potion tables: two book pools (core + theme), two potion pools.
 */
class EliteNestedLootPoolTest {

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
	void bookEntriesUseFixedEnchantBookLootFunction() {
		for (String mobId : MOB_TABLES) {
			String resourcePath = "data/mob_loot_rebalance/loot_table/elite/books/" + mobId + ".json";
			JsonObject table = loadLootTable(resourcePath);
			assertFalse(
					table.toString().contains("set_enchantments"),
					mobId + " must not use set_enchantments on enchanted_book"
			);
			assertFalse(
					table.toString().contains("set_components"),
					mobId + " must use mob_loot_rebalance:fixed_enchant_book (same stack shape as structure loot)"
			);
			assertTrue(
					table.toString().contains("mob_loot_rebalance:fixed_enchant_book"),
					mobId + " book tables must apply fixed_enchant_book"
			);
		}
	}

	@Test
	void bookTablesHaveCoreAndThemePools() {
		for (String mobId : MOB_TABLES) {
			String resourcePath = "data/mob_loot_rebalance/loot_table/elite/books/" + mobId + ".json";
			JsonObject table = loadLootTable(resourcePath);
			JsonArray pools = table.getAsJsonArray("pools");
			assertNotNull(pools, mobId + " books must define pools");
			assertEquals(2, pools.size(), mobId + " book table must have exactly 2 pools (core + theme)");

			JsonArray coreEntries = pools.get(0).getAsJsonObject().getAsJsonArray("entries");
			assertTrue(containsEmpty(coreEntries), mobId + " core pool must contain minecraft:empty");
			assertTrue(tableContainsEnchant(coreEntries, "minecraft:mending"), mobId + " core must include Mending");
			assertEquals("melee", killMethodForEnchant(coreEntries, "minecraft:sharpness"), mobId + " Sharpness");
			assertEquals("ranged", killMethodForEnchant(coreEntries, "minecraft:power"), mobId + " Power");
			assertFalse(
					table.toString().contains("mob_loot_rebalance:routed_offense_book"),
					mobId + " must not use routed_offense_book"
			);
			assertFalse(
					table.toString().contains("minecraft:unbreaking"),
					mobId + " books must not contain Unbreaking"
			);

			assertThemeBooksAreKillMethodGated(mobId, pools.get(1).getAsJsonObject().getAsJsonArray("entries"));
		}
	}

	private static void assertThemeBooksAreKillMethodGated(String mobId, JsonArray themeEntries) {
		assertNotNull(themeEntries);
		for (var element : themeEntries) {
			JsonObject entry = element.getAsJsonObject();
			if (!"minecraft:item".equals(entry.get("type").getAsString())) {
				continue;
			}
			String enchantId = firstSetEnchantmentId(entry);
			if (enchantId == null) {
				continue;
			}
			KillMethod expected = ThemeEnchantmentKillMethod.forEnchantmentId(enchantId).orElse(null);
			assertNotNull(expected, mobId + " theme pool has unclassified enchant " + enchantId);
			JsonObject condition = entry.getAsJsonObject("condition");
			assertNotNull(condition, mobId + " " + enchantId + " must have kill_method condition");
			assertEquals(
					"mob_loot_rebalance:kill_method",
					condition.get("type").getAsString(),
					mobId + " " + enchantId
			);
			assertEquals(
					expected.name().toLowerCase(),
					condition.get("method").getAsString(),
					mobId + " " + enchantId + " kill method"
			);
		}
	}

	private static boolean tableContainsEnchant(JsonArray entries, String enchantId) {
		for (var element : entries) {
			if (enchantId.equals(firstSetEnchantmentId(element.getAsJsonObject()))) {
				return true;
			}
		}
		return false;
	}

	private static String killMethodForEnchant(JsonArray entries, String enchantId) {
		for (var element : entries) {
			JsonObject entry = element.getAsJsonObject();
			if (!enchantId.equals(firstSetEnchantmentId(entry))) {
				continue;
			}
			JsonObject condition = entry.getAsJsonObject("condition");
			assertNotNull(condition, enchantId + " must have kill_method condition");
			return condition.get("method").getAsString();
		}
		return null;
	}

	private static String firstSetEnchantmentId(JsonObject entry) {
		if (!entry.has("modifier")) {
			return null;
		}
		JsonArray functions = entry.getAsJsonArray("modifier");
		for (var element : functions) {
			JsonObject function = element.getAsJsonObject();
			if (!"mob_loot_rebalance:fixed_enchant_book".equals(function.get("type").getAsString())) {
				continue;
			}
			if (!function.has("enchantment")) {
				continue;
			}
			return function.get("enchantment").getAsString();
		}
		return null;
	}

	@Test
	void potionTablesHaveTwoPools() {
		for (String mobId : MOB_TABLES) {
			String resourcePath = "data/mob_loot_rebalance/loot_table/elite/potions/" + mobId + ".json";
			JsonObject table = loadLootTable(resourcePath);
			JsonArray pools = table.getAsJsonArray("pools");
			assertNotNull(pools, mobId + " potions must define pools");
			assertEquals(2, pools.size(), mobId + " potion table must have exactly 2 pools");
		}
	}

	private static boolean containsEmpty(JsonArray entries) {
		assertNotNull(entries);
		for (var element : entries) {
			JsonObject entry = element.getAsJsonObject();
			if ("minecraft:empty".equals(entry.get("type").getAsString())) {
				return true;
			}
		}
		return false;
	}

	private static JsonObject loadLootTable(String resourcePath) {
		InputStream stream = EliteNestedLootPoolTest.class.getClassLoader().getResourceAsStream(resourcePath);
		assertNotNull(stream, "Missing loot table resource: " + resourcePath);
		try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
			return JsonParser.parseReader(reader).getAsJsonObject();
		} catch (Exception e) {
			throw new AssertionError("Failed to parse loot table: " + resourcePath, e);
		}
	}
}
