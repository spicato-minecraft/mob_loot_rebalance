package mob_loot_rebalance.gametest;

import mob_loot_rebalance.elite.EliteCombatHandshake;
import mob_loot_rebalance.elite.EliteModifiers;
import mob_loot_rebalance.elite.EliteState;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Fallback elite combat presentation when mob size and mob combat companion mods are absent.
 */
public class EliteCombatStats {

	private static final double STAT_EPSILON = 0.001;
	private static final BlockPos SPAWN_POS = new BlockPos(1, 1, 1);

	@GameTest(maxTicks = 100)
	public void fallbackEliteZombieStats(GameTestHelper context) {
		EliteCombatHandshake.overrideCombatModLoadedForTest(() -> false);
		EliteCombatHandshake.overrideSizeModLoadedForTest(() -> false);
		try {
			EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
			EliteSpawnGameTestHelper.buildFloor(context, 2);

			Mob elite = spawnCleanEliteZombie(context);
			Mob normal = spawnCleanNormalZombie(context);

			double scale = elite.getAttributeValue(Attributes.SCALE);
			if (Math.abs(scale - EliteModifiers.SCALE_MULTIPLIER) > STAT_EPSILON) {
				throw EliteSpawnGameTestHelper.fail(
						"Expected elite scale " + EliteModifiers.SCALE_MULTIPLIER + " but was " + scale
				);
			}

			float normalHealth = normal.getMaxHealth();
			float eliteHealth = elite.getMaxHealth();
			double healthRatio = eliteHealth / normalHealth;
			if (Math.abs(healthRatio - EliteModifiers.HEALTH_MULTIPLIER) > STAT_EPSILON) {
				throw EliteSpawnGameTestHelper.fail(
						"Expected elite max health " + EliteModifiers.HEALTH_MULTIPLIER
								+ "x a normal zombie (" + normalHealth + ") but was " + eliteHealth
				);
			}

			double eliteKnockback = elite.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
			double normalKnockback = normal.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
			if (eliteKnockback <= normalKnockback) {
				throw EliteSpawnGameTestHelper.fail(
						"Expected elite knockback resistance above vanilla " + normalKnockback + " but was " + eliteKnockback
				);
			}
			if (eliteKnockback + STAT_EPSILON < EliteModifiers.KNOCKBACK_RESISTANCE_BONUS) {
				throw EliteSpawnGameTestHelper.fail(
						"Expected elite knockback resistance of at least " + EliteModifiers.KNOCKBACK_RESISTANCE_BONUS
								+ " but was " + eliteKnockback
				);
			}
			context.succeed();
		} finally {
			EliteCombatHandshake.overrideCombatModLoadedForTest(null);
			EliteCombatHandshake.overrideSizeModLoadedForTest(null);
		}
	}

	private static Mob spawnCleanEliteZombie(GameTestHelper context) {
		for (int attempt = 0; attempt < 40; attempt++) {
			Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
					context,
					EntityTypes.ZOMBIE,
					EntitySpawnReason.NATURAL,
					SPAWN_POS
			);
			if (EliteState.isElite(zombie) && maxHealthModifierCount(zombie) == 1) {
				return zombie;
			}
			zombie.discard();
		}
		throw EliteSpawnGameTestHelper.fail("Could not spawn a non-leader elite zombie");
	}

	private static Mob spawnCleanNormalZombie(GameTestHelper context) {
		for (int attempt = 0; attempt < 40; attempt++) {
			Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
					context,
					EntityTypes.ZOMBIE,
					EntitySpawnReason.EVENT,
					SPAWN_POS
			);
			if (!EliteState.isElite(zombie) && maxHealthModifierCount(zombie) == 0) {
				return zombie;
			}
			zombie.discard();
		}
		throw EliteSpawnGameTestHelper.fail("Could not spawn a non-leader normal zombie");
	}

	private static int maxHealthModifierCount(Mob mob) {
		var health = mob.getAttribute(Attributes.MAX_HEALTH);
		if (health == null) {
			throw EliteSpawnGameTestHelper.fail("Zombie is missing max health");
		}
		return health.getModifiers().size();
	}
}
