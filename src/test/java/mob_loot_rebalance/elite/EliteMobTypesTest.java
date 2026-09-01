package mob_loot_rebalance.elite;

import mob_loot_rebalance.loot.EliteLootTables;
import mob_loot_rebalance.MinecraftTestBootstrap;
import net.minecraft.world.entity.EntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EliteMobTypesTest {

	@BeforeAll
	static void bootstrapMinecraft() {
		MinecraftTestBootstrap.init();
	}

	@Test
	void eligibleSetHasThirtyFiveTypesAndExcludesBosses() {
		assertEquals(35, EliteMobTypes.ELIGIBLE.size());
		assertTrue(EliteMobTypes.isEligible(EntityType.ZOMBIE));
		assertTrue(EliteMobTypes.isEligible(EntityType.BREEZE));
		assertFalse(EliteMobTypes.isEligible(EntityType.WITHER));
		assertFalse(EliteMobTypes.isEligible(EntityType.ENDER_DRAGON));
		assertFalse(EliteMobTypes.isEligible(EntityType.WARDEN));
		assertFalse(EliteMobTypes.isEligible(EntityType.BEE));
	}

	@Test
	void everyEligibleTypeHasAnEliteLootTable() {
		for (EntityType<?> type : EliteMobTypes.ELIGIBLE) {
			EliteLootTables.forEntityType(type);
		}
	}
}
