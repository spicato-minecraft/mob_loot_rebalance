package mob_combat_rebalance.gametest;

import mob_combat_rebalance.config.ModConfig;
import mob_combat_rebalance.elite.EliteState;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;

/**
 * Shared helpers for mob_combat_rebalance elite spawn GameTests.
 */
public final class EliteSpawnGameTestHelper {

	private EliteSpawnGameTestHelper() {
	}

	public static ModConfig applyEliteSpawnTestConfig() {
		ModConfig config = ModConfig.defaults();
		config.elites.enabled = true;
		config.elites.spawn_chance = 1.0;
		ModConfig.setForTest(config);
		return config;
	}

	public static void buildFloor(GameTestHelper context, int size) {
		for (int x = 0; x <= size; x++) {
			for (int z = 0; z <= size; z++) {
				context.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
			}
		}
	}

	public static Mob spawnAdultMob(GameTestHelper context, EntityType<? extends Mob> type, EntitySpawnReason reason, BlockPos pos) {
		Mob mob = context.spawn(type, pos.getX(), pos.getY(), pos.getZ(), reason);
		if (mob.isBaby()) {
			mob.setBaby(false);
		}
		return mob;
	}

	public static Mob spawnBabyMob(GameTestHelper context, EntityType<? extends Mob> type, EntitySpawnReason reason, BlockPos pos) {
		ServerLevel level = context.getLevel();
		Mob mob = type.create(level, EntitySpawnReason.STRUCTURE);
		if (mob == null) {
			throw fail("Failed to create mob " + type);
		}
		mob.setBaby(true);
		mob.setPersistenceRequired();
		Vec3 spawnPos = context.absoluteVec(Vec3.atBottomCenterOf(pos));
		mob.snapTo(spawnPos.x, spawnPos.y, spawnPos.z, 0.0F, 0.0F);
		mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), reason, null);
		level.addFreshEntityWithPassengers(mob);
		return mob;
	}

	public static void configureFastZombieSpawner(GameTestHelper context, BlockPos relativePos) {
		BlockPos absolutePos = context.absolutePos(relativePos);
		context.setBlock(relativePos, Blocks.SPAWNER);
		SpawnerBlockEntity spawnerBlockEntity = context.getBlockEntity(relativePos, SpawnerBlockEntity.class);
		spawnerBlockEntity.setEntityId(EntityType.ZOMBIE, context.getLevel().random);

		CompoundTag spawnerTag = new CompoundTag();
		spawnerTag.putShort("Delay", (short) 0);
		spawnerTag.putShort("MinSpawnDelay", (short) 20);
		spawnerTag.putShort("MaxSpawnDelay", (short) 20);
		spawnerTag.putShort("RequiredPlayerRange", (short) 16);
		CompoundTag spawnData = new CompoundTag();
		spawnData.putString("id", "minecraft:zombie");
		spawnerTag.put("SpawnData", spawnData);

		spawnerBlockEntity.getSpawner().load(
				context.getLevel(),
				absolutePos,
				TagValueInput.create(ProblemReporter.DISCARDING, context.getLevel().registryAccess(), spawnerTag)
		);
	}

	public static ServerPlayer placeMockPlayerNear(GameTestHelper context, BlockPos relativePos) {
		ServerPlayer player = context.makeMockServerPlayerInLevel();
		BlockPos absolutePos = context.absolutePos(relativePos);
		player.snapTo(absolutePos.getX() + 0.5, absolutePos.getY(), absolutePos.getZ() + 0.5, 0.0F, 0.0F);
		return player;
	}

	public static void assertElite(Mob mob, boolean expectedElite) {
		boolean actual = EliteState.isElite(mob);
		if (actual != expectedElite) {
			throw fail("Expected elite=" + expectedElite + " but was " + actual + " for " + mob.getType());
		}
	}

	public static GameTestAssertException fail(String message) {
		return new GameTestAssertException(Component.literal(message), 0);
	}
}
