package mob_combat_rebalance.xp;

import mob_combat_rebalance.MinecraftTestBootstrap;
import net.minecraft.world.entity.EntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XpUpliftPolicyTest {

	@BeforeAll
	static void bootstrapMinecraft() {
		MinecraftTestBootstrap.init();
	}

	@Test
	void bossTypesAreExcludedFromUplift() {
		assertTrue(XpUpliftPolicy.isBoss(EntityType.WITHER));
		assertTrue(XpUpliftPolicy.isBoss(EntityType.ENDER_DRAGON));
		assertTrue(XpUpliftPolicy.isBoss(EntityType.WARDEN));
		assertFalse(XpUpliftPolicy.isBoss(EntityType.ZOMBIE));
		assertFalse(XpUpliftPolicy.isBoss(EntityType.CREEPER));
	}

	@Test
	void bossTypesKeepVanillaMultiplier() {
		assertEquals(1.0, XpUpliftPolicy.resolveMultiplier(true, 1.35, EntityType.WITHER, true, true));
		assertEquals(1.0, XpUpliftPolicy.resolveMultiplier(true, 1.35, EntityType.ENDER_DRAGON, true, true));
		assertEquals(1.0, XpUpliftPolicy.resolveMultiplier(true, 1.35, EntityType.WARDEN, true, true));
	}

	@Test
	void disabledPillarUsesVanillaMultiplier() {
		assertEquals(1.0, XpUpliftPolicy.resolveMultiplier(false, 1.35, EntityType.ZOMBIE, true, true));
	}

	@Test
	void nonHostilesAreExcluded() {
		assertEquals(1.0, XpUpliftPolicy.resolveMultiplier(true, 1.35, EntityType.ZOMBIE, false, true));
		assertEquals(1.0, XpUpliftPolicy.resolveMultiplier(true, 1.35, EntityType.COW, false, true));
	}

	@Test
	void nonPlayerKillsAreExcluded() {
		assertEquals(1.0, XpUpliftPolicy.resolveMultiplier(true, 1.35, EntityType.ZOMBIE, true, false));
	}

	@Test
	void activePillarAppliesConfigMultiplierToEligibleHostiles() {
		assertEquals(1.35, XpUpliftPolicy.resolveMultiplier(true, 1.35, EntityType.ZOMBIE, true, true), 0.0001);
		assertEquals(2.0, XpUpliftPolicy.resolveMultiplier(true, 2.0, EntityType.CREEPER, true, true), 0.0001);
	}

	@Test
	void scaleRewardPreservesRelativeTiers() {
		assertEquals(7, XpUpliftPolicy.scaleReward(5, 1.35));
		assertEquals(14, XpUpliftPolicy.scaleReward(10, 1.35));
		assertEquals(27, XpUpliftPolicy.scaleReward(20, 1.35));
	}

	@Test
	void scaleRewardIsNoOpForVanillaMultiplierOrZeroXp() {
		assertEquals(5, XpUpliftPolicy.scaleReward(5, 1.0));
		assertEquals(0, XpUpliftPolicy.scaleReward(0, 1.35));
	}
}
