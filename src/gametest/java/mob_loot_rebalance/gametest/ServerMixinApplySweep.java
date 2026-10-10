package mob_loot_rebalance.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Loads every behavior mixin target so a renamed class fails here instead of in a player session.
 * Pure accessors that are not separate classes (Creeper, nested-table contents) are skipped.
 */
public class ServerMixinApplySweep {

	private static final String[] TARGETS = {
			"net.minecraft.world.entity.Mob",
			"net.minecraft.world.entity.LivingEntity",
			"net.minecraft.world.entity.monster.zombie.ZombieVillager",
			"net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer",
			"net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer$EntryBase",
			"net.minecraft.world.level.storage.loot.entries.NestedLootTable"
	};

	@GameTest(maxTicks = 20)
	public void loadsBehaviorMixinTargets(GameTestHelper context) {
		for (String name : TARGETS) {
			load(name);
		}
		context.succeed();
	}

	private static void load(String name) {
		try {
			Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw EliteSpawnGameTestHelper.fail("Mixin target failed to load: " + name);
		}
	}
}
