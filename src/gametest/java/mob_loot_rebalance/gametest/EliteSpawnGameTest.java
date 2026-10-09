package mob_loot_rebalance.gametest;

import mob_loot_rebalance.config.ModConfig;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;

public class EliteSpawnGameTest {

	@GameTest(maxTicks = 100)
	public void spawnerSpawnCanBeElite(GameTestHelper context) {
		EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);
		context.setBlock(new BlockPos(1, 1, 1), Blocks.SPAWNER);

		Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityType.ZOMBIE,
				EntitySpawnReason.SPAWNER,
				new BlockPos(1, 1, 1)
		);
		EliteSpawnGameTestHelper.assertElite(zombie, true);
		context.succeed();
	}

	@GameTest(maxTicks = 100)
	public void naturalSpawnCanBeElite(GameTestHelper context) {
		EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);

		Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityType.ZOMBIE,
				EntitySpawnReason.NATURAL,
				new BlockPos(1, 1, 1)
		);
		EliteSpawnGameTestHelper.assertElite(zombie, true);
		context.succeed();
	}

	@GameTest(maxTicks = 100)
	public void structureSpawnCanBeElite(GameTestHelper context) {
		EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);

		Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityType.ZOMBIE,
				EntitySpawnReason.STRUCTURE,
				new BlockPos(1, 1, 1)
		);
		EliteSpawnGameTestHelper.assertElite(zombie, true);
		context.succeed();
	}

	@GameTest(maxTicks = 100)
	public void eventSpawnNeverElite(GameTestHelper context) {
		EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);

		Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityType.ZOMBIE,
				EntitySpawnReason.EVENT,
				new BlockPos(1, 1, 1)
		);
		EliteSpawnGameTestHelper.assertElite(zombie, false);
		context.succeed();
	}

	@GameTest(maxTicks = 100)
	public void spawnerSpawnDeniedWhenTrialChanceZero(GameTestHelper context) {
		ModConfig config = EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
		config.elites.spawn_chance = 1.0;
		config.elites.spawner_trial_spawn_chance = 0.0;
		EliteSpawnGameTestHelper.buildFloor(context, 2);

		Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityType.ZOMBIE,
				EntitySpawnReason.SPAWNER,
				new BlockPos(1, 1, 1)
		);
		EliteSpawnGameTestHelper.assertElite(zombie, false);
		context.succeed();
	}

	@GameTest(maxTicks = 100)
	public void phantomNaturalSpawnCanBeElite(GameTestHelper context) {
		EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);

		Mob phantom = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityType.PHANTOM,
				EntitySpawnReason.NATURAL,
				new BlockPos(1, 1, 1)
		);
		EliteSpawnGameTestHelper.assertElite(phantom, true);
		context.succeed();
	}

	@GameTest(maxTicks = 100)
	public void pillagerPatrolSpawnCanBeElite(GameTestHelper context) {
		EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);

		Mob pillager = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityType.PILLAGER,
				EntitySpawnReason.PATROL,
				new BlockPos(1, 1, 1)
		);
		EliteSpawnGameTestHelper.assertElite(pillager, true);
		context.succeed();
	}

	@GameTest(maxTicks = 100)
	public void babyMobNeverElite(GameTestHelper context) {
		EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);

		Mob baby = EliteSpawnGameTestHelper.spawnBabyMob(
				context,
				EntityType.ZOMBIE,
				EntitySpawnReason.NATURAL,
				new BlockPos(1, 1, 1)
		);
		EliteSpawnGameTestHelper.assertElite(baby, false);
		context.succeed();
	}

	@GameTest(maxTicks = 100)
	public void eliteHasNoGlowWhenUatGlowOff(GameTestHelper context) {
		ModConfig config = EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
		config.elites.uat_glow = false;
		ModConfig.setForTest(config);
		EliteSpawnGameTestHelper.buildFloor(context, 2);

		Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityType.ZOMBIE,
				EntitySpawnReason.NATURAL,
				new BlockPos(1, 1, 1)
		);
		EliteSpawnGameTestHelper.assertElite(zombie, true);
		if (zombie.hasEffect(MobEffects.GLOWING) || zombie.hasGlowingTag()) {
			throw EliteSpawnGameTestHelper.fail(
					"uat_glow=false but elite has glowing effect=" + zombie.hasEffect(MobEffects.GLOWING)
							+ " tag=" + zombie.hasGlowingTag()
			);
		}
		context.succeed();
	}
}
