package mob_loot_rebalance.gametest;

import mob_loot_rebalance.elite.EliteCombatHandshake;
import mob_loot_rebalance.elite.EliteModifiers;
import mob_loot_rebalance.mixin.CreeperAccessor;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.Creeper;

/**
 * Elite creepers keep a larger explosion radius when companion combat mods are absent.
 */
public class CreeperEliteBlastLarger {

	@GameTest(maxTicks = 100)
	public void eliteExplosionRadiusExceedsNormal(GameTestHelper context) {
		EliteCombatHandshake.overrideCombatModLoadedForTest(() -> false);
		EliteCombatHandshake.overrideSizeModLoadedForTest(() -> false);
		try {
			EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
			EliteSpawnGameTestHelper.buildFloor(context, 2);

			Creeper elite = spawnCreeper(context, EntitySpawnReason.NATURAL, true);
			Creeper normal = spawnCreeper(context, EntitySpawnReason.EVENT, false);
			int eliteRadius = ((CreeperAccessor) elite).mob_loot_rebalance$getExplosionRadius();
			int normalRadius = ((CreeperAccessor) normal).mob_loot_rebalance$getExplosionRadius();
			int expected = Math.max(1, Math.round(normalRadius * (float) EliteModifiers.EXPLOSION_MULTIPLIER));
			if (eliteRadius <= normalRadius || eliteRadius != expected) {
				throw EliteSpawnGameTestHelper.fail(
						"Expected elite creeper explosion radius " + expected
								+ " (greater than normal " + normalRadius + ") but was " + eliteRadius
				);
			}
			context.succeed();
		} finally {
			EliteCombatHandshake.overrideCombatModLoadedForTest(null);
			EliteCombatHandshake.overrideSizeModLoadedForTest(null);
		}
	}

	private static Creeper spawnCreeper(GameTestHelper context, EntitySpawnReason reason, boolean elite) {
		var mob = EliteSpawnGameTestHelper.spawnAdultMob(
				context,
				EntityTypes.CREEPER,
				reason,
				new BlockPos(1, 1, 1)
		);
		if (!(mob instanceof Creeper creeper)) {
			throw EliteSpawnGameTestHelper.fail("Expected a creeper");
		}
		EliteSpawnGameTestHelper.assertElite(creeper, elite);
		return creeper;
	}
}
