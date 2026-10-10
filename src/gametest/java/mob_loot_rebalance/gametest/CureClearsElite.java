package mob_loot_rebalance.gametest;

import mob_loot_rebalance.elite.EliteCombatHandshake;
import mob_loot_rebalance.elite.EliteModifiers;
import mob_loot_rebalance.elite.EliteState;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Starting a cure strips elite identity and the elite health modifier immediately.
 */
public class CureClearsElite {

	private static final BlockPos SPAWN_POS = new BlockPos(1, 1, 1);

	@GameTest(maxTicks = 100)
	public void cureStartClearsElite(GameTestHelper context) {
		EliteCombatHandshake.overrideCombatModLoadedForTest(() -> false);
		EliteCombatHandshake.overrideSizeModLoadedForTest(() -> false);
		try {
			EliteSpawnGameTestHelper.applyEliteSpawnTestConfig();
			EliteSpawnGameTestHelper.buildFloor(context, 2);

			ZombieVillager villager = spawnEliteZombieVillager(context);
			if (!EliteModifiers.hasCombatStatModifiers(villager)) {
				throw EliteSpawnGameTestHelper.fail("Elite zombie villager was missing the elite health modifier before cure");
			}

			villager.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200));
			ServerPlayer player = EliteSpawnGameTestHelper.placeMockPlayerNear(context, SPAWN_POS);
			player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_APPLE));
			villager.mobInteract(player, InteractionHand.MAIN_HAND);

			if (!villager.isConverting()) {
				throw EliteSpawnGameTestHelper.fail("Golden apple on a weakened elite zombie villager did not start conversion");
			}
			EliteSpawnGameTestHelper.assertElite(villager, false);
			if (EliteModifiers.hasCombatStatModifiers(villager)) {
				throw EliteSpawnGameTestHelper.fail("Cure start left the elite health modifier on the zombie villager");
			}
			context.succeed();
		} finally {
			EliteCombatHandshake.overrideCombatModLoadedForTest(null);
			EliteCombatHandshake.overrideSizeModLoadedForTest(null);
		}
	}

	private static ZombieVillager spawnEliteZombieVillager(GameTestHelper context) {
		for (int attempt = 0; attempt < 40; attempt++) {
			Mob mob = EliteSpawnGameTestHelper.spawnAdultMob(
					context,
					EntityTypes.ZOMBIE_VILLAGER,
					EntitySpawnReason.NATURAL,
					SPAWN_POS
			);
			if (mob instanceof ZombieVillager villager && EliteState.isElite(villager) && EliteModifiers.hasCombatStatModifiers(villager)) {
				return villager;
			}
			mob.discard();
		}
		throw EliteSpawnGameTestHelper.fail("Could not spawn an elite zombie villager with combat stats");
	}
}
