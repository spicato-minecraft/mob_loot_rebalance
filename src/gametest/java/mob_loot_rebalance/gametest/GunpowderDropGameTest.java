package mob_loot_rebalance.gametest;

import mob_loot_rebalance.loot.EliteLootTables;
import mob_loot_rebalance.loot.GunpowderDrops;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;

public class GunpowderDropGameTest {

	private static final int GENERATES = 250;
	private static final int VANILLA_DOUBLED_MAX = 4;
	private static final BlockPos SPAWN_POS = new BlockPos(1, 1, 1);

	@GameTest(maxTicks = 200)
	public void creeperVanillaGunpowderIsDoubled(GameTestHelper context) {
		LootRoll roll = begin(context, EntityTypes.CREEPER);
		int max = maxCount(roll, GunpowderDrops.CREEPER_TABLE, Items.GUNPOWDER);
		assertDoubledVanillaGunpowder("minecraft:entities/creeper", max);
		context.succeed();
	}

	@GameTest(maxTicks = 200)
	public void ghastVanillaGunpowderIsDoubledWithoutChangingTears(GameTestHelper context) {
		LootRoll roll = begin(context, EntityTypes.GHAST);
		int maxGunpowder = maxCount(roll, GunpowderDrops.GHAST_TABLE, Items.GUNPOWDER);
		int maxTears = maxCount(roll, GunpowderDrops.GHAST_TABLE, Items.GHAST_TEAR);
		assertDoubledVanillaGunpowder("minecraft:entities/ghast", maxGunpowder);
		if (maxTears > 1) {
			throw EliteSpawnGameTestHelper.fail(
					"Ghast tears must stay vanilla (max 1 without Looting); observed " + maxTears
			);
		}
		context.succeed();
	}

	@GameTest(maxTicks = 200)
	public void eliteCreeperJackpotDropsSixteenToThirtyTwoGunpowder(GameTestHelper context) {
		LootRoll roll = begin(context, EntityTypes.CREEPER);
		boolean foundJackpot = false;
		int max = 0;
		for (int attempt = 0; attempt < GENERATES; attempt++) {
			int count = countItem(generate(roll, EliteLootTables.forEntityType(EntityTypes.CREEPER)), Items.GUNPOWDER);
			if (count > max) {
				max = count;
			}
			if (count >= 16) {
				foundJackpot = true;
				if (count > 32) {
					throw EliteSpawnGameTestHelper.fail("Elite creeper jackpot gunpowder was " + count + "; cap is 32");
				}
			}
		}
		if (!foundJackpot) {
			throw EliteSpawnGameTestHelper.fail(
					"Expected elite creeper unique gunpowder 16–32; max observed " + max
			);
		}
		context.succeed();
	}

	@GameTest(maxTicks = 200)
	public void eliteWitchIngredientGunpowderIsFourToEight(GameTestHelper context) {
		LootRoll roll = begin(context, EntityTypes.WITCH);
		Identifier ingredients = Identifier.fromNamespaceAndPath("mob_loot_rebalance", "elite/pools/witch_ingredients");
		boolean foundGunpowder = false;
		for (int attempt = 0; attempt < GENERATES; attempt++) {
			List<ItemStack> generated = generate(roll, ingredients);
			int gunpowder = countItem(generated, Items.GUNPOWDER);
			if (gunpowder > 0) {
				foundGunpowder = true;
				if (gunpowder < 4 || gunpowder > 8) {
					throw EliteSpawnGameTestHelper.fail(
							"Witch ingredient gunpowder should be 4–8; observed " + gunpowder
					);
				}
			}
			int glowstone = countItem(generated, Items.GLOWSTONE_DUST);
			if (glowstone > 0 && (glowstone < 2 || glowstone > 4)) {
				throw EliteSpawnGameTestHelper.fail(
						"Non-gunpowder witch ingredients should stay 2–4; glowstone was " + glowstone
				);
			}
		}
		if (!foundGunpowder) {
			throw EliteSpawnGameTestHelper.fail("Witch ingredients never rolled gunpowder");
		}
		context.succeed();
	}

	private static void assertDoubledVanillaGunpowder(String tableId, int max) {
		if (max < 3) {
			throw EliteSpawnGameTestHelper.fail(
					"Expected doubled " + tableId + " gunpowder (vanilla max 2); observed max " + max
			);
		}
		if (max > VANILLA_DOUBLED_MAX) {
			throw EliteSpawnGameTestHelper.fail(
					tableId + " gunpowder exceeded 2× vanilla max; observed " + max
			);
		}
	}

	private static int maxCount(LootRoll roll, Identifier tableId, Item item) {
		int max = 0;
		for (int attempt = 0; attempt < GENERATES; attempt++) {
			int count = countItem(generate(roll, tableId), item);
			if (count > max) {
				max = count;
			}
		}
		return max;
	}

	private static List<ItemStack> generate(LootRoll roll, Identifier tableId) {
		ResourceKey<LootTable> tableKey = ResourceKey.create(Registries.LOOT_TABLE, tableId);
		List<ItemStack> generated = new ArrayList<>();
		roll.mob.dropFromLootTable(roll.level, roll.source, false, tableKey, generated::add);
		return generated;
	}

	private static int countItem(List<ItemStack> generated, Item item) {
		int total = 0;
		for (ItemStack stack : generated) {
			if (stack.is(item)) {
				total += stack.getCount();
			}
		}
		return total;
	}

	private static LootRoll begin(GameTestHelper context, EntityType<? extends Mob> type) {
		EliteSpawnGameTestHelper.buildFloor(context, 2);
		Mob mob = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				type,
				EntitySpawnReason.NATURAL,
				SPAWN_POS
		);
		ServerPlayer player = EliteSpawnGameTestHelper.placeMockPlayerNear(context, SPAWN_POS);
		ServerLevel level = context.getLevel();
		mob.setLastHurtByPlayer(player, (int) level.getGameTime());
		return new LootRoll(mob, level, level.damageSources().playerAttack(player));
	}

	private record LootRoll(Mob mob, ServerLevel level, DamageSource source) {
	}
}
