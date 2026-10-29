package mob_loot_rebalance.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mob_loot_rebalance.MinecraftTestBootstrap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GunpowderDropsTest {

	@BeforeAll
	static void boot() {
		MinecraftTestBootstrap.init();
	}

	@Test
	void doublesGunpowderAndLeavesOtherItems() {
		List<ItemStack> drops = new ArrayList<>();
		drops.add(new ItemStack(Items.GUNPOWDER, 2));
		drops.add(new ItemStack(Items.GHAST_TEAR, 1));
		drops.add(new ItemStack(Items.MUSIC_DISC_13, 1));

		GunpowderDrops.multiplyGunpowder(drops);

		assertEquals(4, drops.get(0).getCount());
		assertEquals(1, drops.get(1).getCount());
		assertTrue(drops.get(1).is(Items.GHAST_TEAR));
		assertEquals(1, drops.get(2).getCount());
	}

	@Test
	void appliesOnlyToVanillaCreeperAndGhastTables() {
		assertTrue(GunpowderDrops.appliesTo(GunpowderDrops.CREEPER_TABLE));
		assertTrue(GunpowderDrops.appliesTo(GunpowderDrops.GHAST_TABLE));
		assertFalse(GunpowderDrops.appliesTo(Identifier.parse("minecraft:entities/witch")));
		assertFalse(GunpowderDrops.appliesTo(Identifier.parse("mob_loot_rebalance:elite/creeper")));
		assertFalse(GunpowderDrops.appliesTo(null));
	}

	@Test
	void eliteCreeperJackpotIsSixteenToThirtyTwo() {
		JsonObject gunpowder = uniqueItem("data/mob_loot_rebalance/loot_table/elite/creeper.json", "minecraft:gunpowder");
		assertCountRange(gunpowder, 16, 32);
	}

	@Test
	void eliteWitchIngredientGunpowderIsFourToEight() {
		JsonObject table = loadLootTable("data/mob_loot_rebalance/loot_table/elite/pools/witch_ingredients.json");
		JsonArray entries = table.getAsJsonArray("pools").get(0).getAsJsonObject().getAsJsonArray("entries");
		boolean foundGunpowder = false;
		for (var element : entries) {
			JsonObject entry = element.getAsJsonObject();
			String name = entry.get("name").getAsString();
			int expectedMin = "minecraft:gunpowder".equals(name) ? 4 : 2;
			int expectedMax = "minecraft:gunpowder".equals(name) ? 8 : 4;
			assertCountRange(entry, expectedMin, expectedMax);
			if ("minecraft:gunpowder".equals(name)) {
				foundGunpowder = true;
			}
		}
		assertTrue(foundGunpowder, "witch ingredients must still drop gunpowder");
	}

	private static JsonObject uniqueItem(String resourcePath, String itemId) {
		JsonObject table = loadLootTable(resourcePath);
		JsonArray entries = table.getAsJsonArray("pools").get(0).getAsJsonObject().getAsJsonArray("entries");
		for (var element : entries) {
			JsonObject entry = element.getAsJsonObject();
			if (itemId.equals(entry.has("name") ? entry.get("name").getAsString() : "")) {
				return entry;
			}
		}
		throw new AssertionError("Missing unique item " + itemId + " in " + resourcePath);
	}

	private static void assertCountRange(JsonObject entry, int min, int max) {
		JsonObject count = entry.getAsJsonArray("modifier").get(0).getAsJsonObject().getAsJsonObject("count");
		assertEquals(min, count.get("min").getAsInt(), entry.get("name").getAsString() + " min");
		assertEquals(max, count.get("max").getAsInt(), entry.get("name").getAsString() + " max");
	}

	private static JsonObject loadLootTable(String resourcePath) {
		InputStream stream = GunpowderDropsTest.class.getClassLoader().getResourceAsStream(resourcePath);
		assertNotNull(stream, "Missing loot table resource: " + resourcePath);
		try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
			return JsonParser.parseReader(reader).getAsJsonObject();
		} catch (Exception e) {
			throw new AssertionError("Failed to parse loot table: " + resourcePath, e);
		}
	}
}
