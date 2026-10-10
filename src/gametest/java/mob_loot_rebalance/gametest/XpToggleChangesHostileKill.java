package mob_loot_rebalance.gametest;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.xp.XpUpliftPolicy;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.Field;

/**
 * xp.enabled false leaves a player-killed hostile at vanilla XP. True applies the 1.35 multiplier.
 */
public class XpToggleChangesHostileKill {

	private static final int VANILLA_ZOMBIE_XP = 5;
	private static final double XP_MULTIPLIER = 1.35;
	private static final BlockPos SPAWN_POS = new BlockPos(1, 1, 1);

	@GameTest(maxTicks = 200)
	public void playerKillXpFollowsToggle(GameTestHelper context) {
		ModConfig previous = ModConfig.get();
		try {
			EliteSpawnGameTestHelper.buildFloor(context, 2);
			ServerLevel level = context.getLevel();
			AABB searchBox = new AABB(context.absolutePos(SPAWN_POS)).inflate(4.0);
			ServerPlayer player = EliteSpawnGameTestHelper.placeMockPlayerNear(context, SPAWN_POS);

			applyXpConfig(false);
			clearOrbs(level, searchBox);
			int disabledXp = killNonEliteZombie(context, level, player, searchBox);
			if (disabledXp != VANILLA_ZOMBIE_XP) {
				throw EliteSpawnGameTestHelper.fail(
						"xp.enabled=false should match vanilla zombie XP " + VANILLA_ZOMBIE_XP + " but was " + disabledXp
				);
			}

			applyXpConfig(true);
			clearOrbs(level, searchBox);
			int enabledXp = killNonEliteZombie(context, level, player, searchBox);
			int expected = XpUpliftPolicy.scaleReward(VANILLA_ZOMBIE_XP, XP_MULTIPLIER);
			if (enabledXp != expected) {
				throw EliteSpawnGameTestHelper.fail(
						"xp.enabled=true at multiplier " + XP_MULTIPLIER
								+ " should drop " + expected + " XP (" + XP_MULTIPLIER + " times vanilla "
								+ VANILLA_ZOMBIE_XP + ") but was " + enabledXp
				);
			}
			context.succeed();
		} finally {
			ModConfig.setForTest(previous);
		}
	}

	private static int killNonEliteZombie(GameTestHelper context, ServerLevel level, ServerPlayer player, AABB searchBox) {
		Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityTypes.ZOMBIE,
				EntitySpawnReason.EVENT,
				SPAWN_POS
		);
		EliteSpawnGameTestHelper.assertElite(zombie, false);
		DamageSource source = level.damageSources().playerAttack(player);
		zombie.setLastHurtByPlayer(player, (int) level.getGameTime());
		if (!zombie.hurtServer(level, source, 1000.0F)) {
			throw EliteSpawnGameTestHelper.fail("Failed to kill non-elite zombie with player damage");
		}
		return level.getEntitiesOfClass(ExperienceOrb.class, searchBox)
				.stream()
				.mapToInt(XpToggleChangesHostileKill::experienceOf)
				.sum();
	}

	/**
	 * Orbs of the same value merge by raising a private count. Pickup grants {@code getValue() * count}.
	 */
	private static int experienceOf(ExperienceOrb orb) {
		try {
			Field count = ExperienceOrb.class.getDeclaredField("count");
			count.setAccessible(true);
			return orb.getValue() * count.getInt(orb);
		} catch (ReflectiveOperationException e) {
			throw EliteSpawnGameTestHelper.fail("Could not read experience orb count: " + e.getMessage());
		}
	}

	private static void clearOrbs(ServerLevel level, AABB searchBox) {
		level.getEntitiesOfClass(ExperienceOrb.class, searchBox).forEach(orb -> orb.discard());
	}

	private static void applyXpConfig(boolean enabled) {
		ModConfig config = ModConfig.defaults();
		config.xp.enabled = enabled;
		config.xp.multiplier = XP_MULTIPLIER;
		ModConfig.setForTest(config);
	}
}
