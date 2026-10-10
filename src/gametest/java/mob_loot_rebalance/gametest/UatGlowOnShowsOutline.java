package mob_loot_rebalance.gametest;

import mob_loot_rebalance.config.ModConfig;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;

/**
 * elites.uat_glow outlines an elite. The glow-off case stays in {@link EliteSpawnGameTest}.
 */
public class UatGlowOnShowsOutline {

	@GameTest(maxTicks = 100)
	public void eliteGlowsWhenUatGlowOn(GameTestHelper context) {
		ModConfig previous = ModConfig.get();
		try {
			ModConfig config = EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
			config.elites.uat_glow = true;
			ModConfig.setForTest(config);
			EliteSpawnGameTestHelper.buildFloor(context, 2);

			Mob zombie = EliteSpawnGameTestHelper.spawnAdultMob(
					context,
					EntityTypes.ZOMBIE,
					EntitySpawnReason.NATURAL,
					new BlockPos(1, 1, 1)
			);
			EliteSpawnGameTestHelper.assertElite(zombie, true);
			if (!zombie.hasEffect(MobEffects.GLOWING) || !zombie.hasGlowingTag()) {
				throw EliteSpawnGameTestHelper.fail(
						"uat_glow=true but elite glowing effect=" + zombie.hasEffect(MobEffects.GLOWING)
								+ " tag=" + zombie.hasGlowingTag()
				);
			}
			context.succeed();
		} finally {
			ModConfig.setForTest(previous);
		}
	}
}
