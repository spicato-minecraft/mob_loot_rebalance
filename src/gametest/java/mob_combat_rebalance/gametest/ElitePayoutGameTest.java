package mob_combat_rebalance.gametest;

import mob_combat_rebalance.config.ModConfig;
import mob_combat_rebalance.xp.EliteBonusXpPolicy;
import mob_combat_rebalance.xp.XpUpliftPolicy;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

public class ElitePayoutGameTest {

	@GameTest(maxTicks = 200)
	public void playerKilledEliteGrantsBonusXp(GameTestHelper context) {
		applyPayoutTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);
		BlockPos spawnPos = new BlockPos(1, 1, 1);
		AABB searchBox = new AABB(context.absolutePos(spawnPos)).inflate(4.0);

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

		int vanillaReward = 5;
		int upliftedReward = XpUpliftPolicy.scaleReward(vanillaReward, ModConfig.get().xp.multiplier);
		int expectedMinimum = upliftedReward + EliteBonusXpPolicy.scaleBonus(upliftedReward, EliteBonusXpPolicy.BONUS_MULTIPLIER);

		context.succeedWhen(() -> {
			int droppedXp = sumExperienceOrbs(level, searchBox);
			if (droppedXp < expectedMinimum) {
				throw EliteSpawnGameTestHelper.fail(
						"Expected at least " + expectedMinimum + " XP from elite payout but found " + droppedXp
				);
			}
		});
	}

	@GameTest(maxTicks = 200)
	public void nonPlayerKillEliteDeniesPayouts(GameTestHelper context) {
		applyPayoutTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);
		BlockPos spawnPos = new BlockPos(1, 1, 1);
		AABB searchBox = new AABB(context.absolutePos(spawnPos)).inflate(4.0);

		context.succeedWhen(() -> {
			clearDrops(context, searchBox);
			Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
					context,
					EntityType.ZOMBIE,
					EntitySpawnReason.NATURAL,
					spawnPos
			);
			EliteSpawnGameTestHelper.assertElite(zombie, true);

			ServerLevel level = context.getLevel();
			DamageSource source = level.damageSources().lava();
			if (!zombie.hurtServer(level, source, 1000.0F)) {
				throw EliteSpawnGameTestHelper.fail("Failed to kill elite zombie with lava damage");
			}

			int droppedXp = sumExperienceOrbs(level, searchBox);
			if (droppedXp > 5) {
				throw EliteSpawnGameTestHelper.fail(
						"Non-player elite kill should not grant uplift or bonus XP, but found " + droppedXp
				);
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
			if (foundEliteTableLoot) {
				throw EliteSpawnGameTestHelper.fail("Non-player elite kill should not generate elite-table loot");
			}
		});
	}

	private static int sumExperienceOrbs(ServerLevel level, AABB searchBox) {
		return level.getEntitiesOfClass(ExperienceOrb.class, searchBox)
				.stream()
				.mapToInt(ExperienceOrb::getValue)
				.sum();
	}

	private static void clearDrops(GameTestHelper context, AABB searchBox) {
		ServerLevel level = context.getLevel();
		level.getEntitiesOfClass(ItemEntity.class, searchBox).forEach(item -> item.discard());
		level.getEntitiesOfClass(ExperienceOrb.class, searchBox).forEach(orb -> orb.discard());
	}

	private static void applyPayoutTestConfig() {
		ModConfig config = ModConfig.defaults();
		config.elites.enabled = true;
		config.elites.spawn_chance = 1.0;
		config.elite_loot.enabled = true;
		config.xp.enabled = true;
		config.xp.multiplier = 1.35;
		ModConfig.setForTest(config);
	}
}
