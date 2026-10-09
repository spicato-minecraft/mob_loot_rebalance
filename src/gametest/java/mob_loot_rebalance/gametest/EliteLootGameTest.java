package mob_loot_rebalance.gametest;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.killmethod.KillMethod;
import mob_loot_rebalance.loot.EliteLootLayout;
import mob_loot_rebalance.loot.EliteLootRollContext;
import mob_loot_rebalance.loot.EliteLootTables;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class EliteLootGameTest {

	@GameTest(maxTicks = 200)
	public void playerKilledEliteGeneratesEliteTableLoot(GameTestHelper context) {
		applyEliteLootTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);
		BlockPos spawnPos = new BlockPos(1, 1, 1);
		AABB searchBox = new AABB(context.absolutePos(spawnPos)).inflate(4.0);

		context.succeedWhen(() -> {
			clearItemDrops(context, searchBox);
			Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
					context,
					EntityTypes.ZOMBIE,
					EntitySpawnReason.NATURAL,
					spawnPos
			);
			EliteSpawnGameTestHelper.assertElite(zombie, true);

			ServerPlayer player = EliteSpawnGameTestHelper.placeMockPlayerNear(context, spawnPos);
			ServerLevel level = context.getLevel();
			DamageSource source = level.damageSources().playerAttack(player);
			zombie.setLastHurtByPlayer(player, (int) level.getGameTime());
			if (!zombie.hurtServer(level, source, 1000.0F)) {
				throw EliteSpawnGameTestHelper.fail("Failed to kill elite zombie with player damage");
			}

			boolean foundEliteTableLoot = level.getEntitiesOfClass(ItemEntity.class, searchBox)
					.stream()
					.map(ItemEntity::getItem)
					.anyMatch(stack -> !stack.isEmpty() && (
							stack.is(Items.GOLDEN_APPLE)
									|| stack.is(Items.ENCHANTED_BOOK)
									|| stack.is(Items.SPLASH_POTION)
									|| stack.is(Items.LINGERING_POTION)
					));
			if (!foundEliteTableLoot) {
				throw EliteSpawnGameTestHelper.fail(
						"Expected elite-table loot from " + EliteLootTables.forEntityType(EntityTypes.ZOMBIE)
				);
			}
		});
	}

	@GameTest(maxTicks = 200)
	public void meleeBookSlotCanDropMultipleBooks(GameTestHelper context) {
		applyEliteLootTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);
		BlockPos spawnPos = new BlockPos(1, 1, 1);

		Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityTypes.ZOMBIE,
				EntitySpawnReason.NATURAL,
				spawnPos
		);
		EliteSpawnGameTestHelper.assertElite(zombie, true);

		ServerPlayer player = EliteSpawnGameTestHelper.placeMockPlayerNear(context, spawnPos);
		ServerLevel level = context.getLevel();
		DamageSource source = level.damageSources().playerAttack(player);
		zombie.setLastHurtByPlayer(player, (int) level.getGameTime());

		ResourceKey<LootTable> booksKey = ResourceKey.create(
				Registries.LOOT_TABLE,
				EliteLootLayout.booksTable("zombie")
		);
		EliteLootRollContext rollContext = new EliteLootRollContext(KillMethod.MELEE, false);

		boolean foundPair = false;
		for (int attempt = 0; attempt < 200 && !foundPair; attempt++) {
			List<ItemStack> generated = new ArrayList<>();
			EliteLootRollContext.run(rollContext, () ->
					zombie.dropFromLootTable(level, source, false, booksKey, generated::add)
			);
			int books = 0;
			for (ItemStack stack : generated) {
				if (stack.is(Items.ENCHANTED_BOOK)) {
					assertEnchantedBookHasStoredEnchantment(stack);
					books += Math.max(1, stack.getCount());
				}
			}
			if (books >= 2) {
				foundPair = true;
			}
		}

		if (!foundPair) {
			throw EliteSpawnGameTestHelper.fail(
					"Expected at least one generate of " + EliteLootLayout.booksTable("zombie")
							+ " to drop 2+ enchanted books"
			);
		}
		context.succeed();
	}

	@GameTest(maxTicks = 200)
	public void bookSlotNeverDropsMoreThanTwoBooks(GameTestHelper context) {
		applyEliteLootTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);
		BlockPos spawnPos = new BlockPos(1, 1, 1);

		Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityTypes.ZOMBIE,
				EntitySpawnReason.NATURAL,
				spawnPos
		);
		EliteSpawnGameTestHelper.assertElite(zombie, true);

		ServerPlayer player = EliteSpawnGameTestHelper.placeMockPlayerNear(context, spawnPos);
		ServerLevel level = context.getLevel();
		DamageSource source = level.damageSources().playerAttack(player);
		zombie.setLastHurtByPlayer(player, (int) level.getGameTime());

		ResourceKey<LootTable> booksKey = ResourceKey.create(
				Registries.LOOT_TABLE,
				EliteLootLayout.booksTable("zombie")
		);
		EliteLootRollContext rollContext = new EliteLootRollContext(KillMethod.MELEE, false);

		for (int attempt = 0; attempt < 200; attempt++) {
			List<ItemStack> generated = new ArrayList<>();
			EliteLootRollContext.run(rollContext, () ->
					zombie.dropFromLootTable(level, source, false, booksKey, generated::add)
			);
			int books = 0;
			boolean protection = false;
			boolean sharpness = false;
			for (ItemStack stack : generated) {
				if (!stack.is(Items.ENCHANTED_BOOK)) {
					continue;
				}
				assertEnchantedBookHasStoredEnchantment(stack);
				books += Math.max(1, stack.getCount());
				if (bookHasEnchantment(stack, "minecraft:protection")) {
					protection = true;
				}
				if (bookHasEnchantment(stack, "minecraft:sharpness")) {
					sharpness = true;
				}
			}
			if (books > 2) {
				throw EliteSpawnGameTestHelper.fail("Book slot dropped " + books + " books; cap is 2");
			}
			if (protection && sharpness) {
				throw EliteSpawnGameTestHelper.fail("Core table dropped both Protection and Sharpness");
			}
		}
		context.succeed();
	}

	@GameTest(maxTicks = 200)
	public void creeperMeleeBookSlotDoesNotDropPunch(GameTestHelper context) {
		assertCreeperPunchPresence(context, KillMethod.MELEE, false);
	}

	@GameTest(maxTicks = 200)
	public void creeperRangedBookSlotCanDropPunch(GameTestHelper context) {
		assertCreeperPunchPresence(context, KillMethod.RANGED, true);
	}

	private static void assertCreeperPunchPresence(
			GameTestHelper context,
			KillMethod killMethod,
			boolean expectPunch
	) {
		applyEliteLootTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);
		BlockPos spawnPos = new BlockPos(1, 1, 1);

		Mob creeper = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityTypes.CREEPER,
				EntitySpawnReason.NATURAL,
				spawnPos
		);
		EliteSpawnGameTestHelper.assertElite(creeper, true);

		ServerPlayer player = EliteSpawnGameTestHelper.placeMockPlayerNear(context, spawnPos);
		ServerLevel level = context.getLevel();
		DamageSource source = level.damageSources().playerAttack(player);
		creeper.setLastHurtByPlayer(player, (int) level.getGameTime());

		ResourceKey<LootTable> booksKey = ResourceKey.create(
				Registries.LOOT_TABLE,
				EliteLootLayout.booksTable("creeper")
		);
		EliteLootRollContext rollContext = new EliteLootRollContext(killMethod, false);

		boolean foundPunch = false;
		for (int attempt = 0; attempt < 250; attempt++) {
			List<ItemStack> generated = new ArrayList<>();
			EliteLootRollContext.run(rollContext, () ->
					creeper.dropFromLootTable(level, source, false, booksKey, generated::add)
			);
			for (ItemStack stack : generated) {
				if (bookHasEnchantment(stack, "minecraft:punch")) {
					foundPunch = true;
					break;
				}
			}
			if (foundPunch) {
				break;
			}
		}

		if (expectPunch && !foundPunch) {
			throw EliteSpawnGameTestHelper.fail("Expected Punch from creeper books on a ranged kill");
		}
		if (!expectPunch && foundPunch) {
			throw EliteSpawnGameTestHelper.fail("Creeper melee kills must not drop Punch books");
		}
		context.succeed();
	}

	private static void assertEnchantedBookHasStoredEnchantment(ItemStack stack) {
		ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
		if (stored.isEmpty()) {
			throw EliteSpawnGameTestHelper.fail("Elite book drop had no stored enchantments (plain enchanted book)");
		}
		var customData = stack.get(DataComponents.CUSTOM_DATA);
		if (customData == null
				|| !customData.copyTag().getBoolean(mob_loot_rebalance.loot.EliteEnchantedBooks.LOOT_BOOK_CUSTOM_DATA_KEY).orElse(false)) {
			throw EliteSpawnGameTestHelper.fail("Elite book drop missing loot-book custom_data (not a normal rebalance book stack)");
		}
	}

	private static boolean bookHasEnchantment(ItemStack stack, String enchantmentId) {
		if (!stack.is(Items.ENCHANTED_BOOK)) {
			return false;
		}
		ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
		ItemEnchantments applied = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		return hasEnchantmentId(stored, enchantmentId) || hasEnchantmentId(applied, enchantmentId);
	}

	private static boolean hasEnchantmentId(ItemEnchantments enchantments, String enchantmentId) {
		for (var entry : enchantments.entrySet()) {
			String id = entry.getKey().unwrapKey().orElseThrow().identifier().toString();
			if (enchantmentId.equals(id)) {
				return true;
			}
		}
		return false;
	}

	private static void clearItemDrops(GameTestHelper context, AABB searchBox) {
		context.getLevel().getEntitiesOfClass(ItemEntity.class, searchBox).forEach(item -> item.discard());
	}

	private static void applyEliteLootTestConfig() {
		ModConfig config = ModConfig.defaults();
		config.elites.enabled = true;
		config.elites.spawn_chance = 1.0;
		config.elites.spawner_trial_spawn_chance = 1.0;
		config.elite_loot.enabled = true;
		ModConfig.setForTest(config);
	}
}
