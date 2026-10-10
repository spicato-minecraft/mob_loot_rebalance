package mob_loot_rebalance.loot;

import mob_loot_rebalance.MinecraftTestBootstrap;
import mob_loot_rebalance.killmethod.KillMethod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Direct filter check. Loot rolls stay out of this test so invisibility is not a random drop.
 */
class InvisibilityFilteredWhenDisabled {

	@BeforeAll
	static void boot() {
		MinecraftTestBootstrap.init();
	}

	@Test
	void disabledRemovesInvisibilitySplash() {
		ItemStack splash = invisibilitySplash();
		assertFalse(EliteLootService.shouldDropStack(splash, context(false)));
	}

	@Test
	void enabledKeepsInvisibilitySplash() {
		ItemStack splash = invisibilitySplash();
		assertTrue(EliteLootService.shouldDropStack(splash, context(true)));
	}

	private static ItemStack invisibilitySplash() {
		return PotionContents.createItemStack(Items.SPLASH_POTION, Potions.INVISIBILITY);
	}

	private static EliteLootRollContext context(boolean allowInvisibility) {
		return new EliteLootRollContext(KillMethod.MELEE, allowInvisibility);
	}
}
