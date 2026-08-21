package mob_combat_rebalance.gametest;

import mob_combat_rebalance.config.ModConfig;
import mob_combat_rebalance.loot.EliteLootTables;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

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
					EntityType.ZOMBIE,
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
						"Expected elite-table loot from " + EliteLootTables.forEntityType(EntityType.ZOMBIE)
				);
			}
		});
	}

	private static void clearItemDrops(GameTestHelper context, AABB searchBox) {
		context.getLevel().getEntitiesOfClass(ItemEntity.class, searchBox).forEach(item -> item.discard());
	}

	private static void applyEliteLootTestConfig() {
		ModConfig config = ModConfig.defaults();
		config.elites.enabled = true;
		config.elites.spawn_chance = 1.0;
		config.elite_loot.enabled = true;
		ModConfig.setForTest(config);
	}
}
