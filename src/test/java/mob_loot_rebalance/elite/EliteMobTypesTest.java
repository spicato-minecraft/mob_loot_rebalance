package mob_loot_rebalance.elite;

import mob_loot_rebalance.loot.EliteLootTables;
import mob_loot_rebalance.MinecraftTestBootstrap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
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
		assertTrue(EliteMobTypes.isEligible(EntityTypes.ZOMBIE));
		assertTrue(EliteMobTypes.isEligible(EntityTypes.BREEZE));
		assertFalse(EliteMobTypes.isEligible(EntityTypes.SULFUR_CUBE));
		assertFalse(EliteMobTypes.isEligible(EntityTypes.WITHER));
		assertFalse(EliteMobTypes.isEligible(EntityTypes.ENDER_DRAGON));
		assertFalse(EliteMobTypes.isEligible(EntityTypes.WARDEN));
		assertFalse(EliteMobTypes.isEligible(EntityTypes.BEE));
	}

	@Test
	void everyEligibleTypeHasAnEliteLootTable() {
		for (EntityType<?> type : EliteMobTypes.ELIGIBLE) {
			EliteLootTables.forEntityType(type);
		}
	}
}
