package mob_combat_rebalance.xp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EliteBonusXpPolicyTest {

	@Test
	void bonusIsThreeTimesUpliftedReward() {
		assertEquals(21, EliteBonusXpPolicy.scaleBonus(7, EliteBonusXpPolicy.BONUS_MULTIPLIER));
		assertEquals(15, EliteBonusXpPolicy.scaleBonus(5, EliteBonusXpPolicy.BONUS_MULTIPLIER));
	}

	@Test
	void zeroUpliftedRewardYieldsNoBonus() {
		assertEquals(0, EliteBonusXpPolicy.scaleBonus(0, EliteBonusXpPolicy.BONUS_MULTIPLIER));
	}

	@Test
	void bonusMultiplierConstantIsLocked() {
		assertEquals(3.0, EliteBonusXpPolicy.BONUS_MULTIPLIER, 0.0001);
	}
}
