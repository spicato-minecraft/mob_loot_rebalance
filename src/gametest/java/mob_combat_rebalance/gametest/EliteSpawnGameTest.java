package mob_combat_rebalance.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;

public class EliteSpawnGameTest {

	@GameTest(maxTicks = 100)
	public void spawnerSpawnNeverElite(GameTestHelper context) {
		EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
		EliteSpawnGameTestHelper.buildFloor(context, 2);
		context.setBlock(new BlockPos(1, 1, 1), Blocks.SPAWNER);

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
}
