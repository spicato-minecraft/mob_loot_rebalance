package mob_loot_rebalance.xp;

import mob_loot_rebalance.config.ModConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

/**
 * Baseline XP uplift for player-killed hostile mobs (AC-1, AC-2, AC-3, AC-20).
 */
public final class XpUpliftPolicy {

	private static final Set<EntityType<?>> BOSS_TYPES = Set.of(
			EntityTypes.WITHER,
			EntityTypes.ENDER_DRAGON,
			EntityTypes.WARDEN
	);

	private XpUpliftPolicy() {
	}

	public static double resolveMultiplier(ModConfig config, LivingEntity entity) {
		return resolveMultiplier(
				config.isXpPillarActive(),
				config.xp.multiplier,
				entity.getType(),
				entity instanceof Enemy,
				isPlayerKill(entity)
		);
	}

	static double resolveMultiplier(
			boolean pillarActive,
			double configMultiplier,
			EntityType<?> entityType,
			boolean hostile,
			boolean playerKill
	) {
		if (!pillarActive || !hostile || !playerKill || isBoss(entityType)) {
			return 1.0;
		}
		return configMultiplier;
	}

	public static int resolveUpliftedReward(ModConfig config, LivingEntity entity, int vanillaReward) {
		return scaleReward(vanillaReward, resolveMultiplier(config, entity));
	}

	public static int scaleReward(int vanillaReward, double multiplier) {
		if (multiplier == 1.0 || vanillaReward <= 0) {
			return vanillaReward;
		}
		return Math.max(0, (int) Math.round(vanillaReward * multiplier));
	}

	public static boolean isBoss(EntityType<?> entityType) {
		return BOSS_TYPES.contains(entityType);
	}

	public static boolean isPlayerKill(LivingEntity entity) {
		if (entity.getLastHurtByPlayer() != null) {
			return true;
		}
		return entity.getKillCredit() instanceof Player;
	}
}
