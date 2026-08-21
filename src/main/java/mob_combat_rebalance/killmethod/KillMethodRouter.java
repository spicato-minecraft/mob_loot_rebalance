package mob_combat_rebalance.killmethod;

import mob_combat_rebalance.config.ModConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Resolves which elite enchanting-book sub-pool applies for a kill (AC-16, AC-17, AC-17b-d).
 *
 * <p>Phase 6 loot pipeline should call {@link #resolve(LivingEntity, Entity, ModConfig, RandomSource)}
 * when generating the {@code enchanting_book_roll} entry for an elite kill.
 */
public final class KillMethodRouter {
	static final int MELEE_ARMOR_WEIGHT = 4;
	static final int MELEE_MELEE_WEIGHT = 4;
	static final int MELEE_MENDING_WEIGHT = 1;

	static final int RANGED_ARMOR_WEIGHT = 1;
	static final int RANGED_RANGED_WEIGHT = 2;
	static final int RANGED_MENDING_WEIGHT = 1;

	private KillMethodRouter() {
	}

	public static KillMethod resolveKillMethod(LivingEntity victim, @Nullable Entity killer, ModConfig config) {
		return resolveKillMethod(
				victim.level().getGameTime(),
				config.kill_method.lookback_ticks,
				historyOf(victim),
				classifyKillingBlow(victim)
		);
	}

	static KillMethod resolveKillMethod(
			long currentTick,
			int lookbackTicks,
			PlayerDamageHistory history,
			Optional<KillMethod> killingBlow
	) {
		history.pruneOlderThan(currentTick, lookbackTicks);
		if (killingBlow.isPresent()) {
			return killingBlow.get();
		}
		return history.mostRecentInWindow(currentTick, lookbackTicks).orElse(KillMethod.FALLBACK);
	}

	public static Identifier resolve(LivingEntity victim, @Nullable Entity killer, ModConfig config, RandomSource random) {
		return poolFor(resolveKillMethod(victim, killer, config), random);
	}

	public static Identifier poolFor(KillMethod method, RandomSource random) {
		return switch (method) {
			case MELEE -> weightedPick(
					random,
					EliteBookPools.ARMOR, MELEE_ARMOR_WEIGHT,
					EliteBookPools.MELEE, MELEE_MELEE_WEIGHT,
					EliteBookPools.MENDING, MELEE_MENDING_WEIGHT
			);
			case RANGED -> weightedPick(
					random,
					EliteBookPools.ARMOR, RANGED_ARMOR_WEIGHT,
					EliteBookPools.RANGED, RANGED_RANGED_WEIGHT,
					EliteBookPools.MENDING, RANGED_MENDING_WEIGHT
			);
			case FALLBACK -> EliteBookPools.FALLBACK;
		};
	}

	private static Optional<KillMethod> classifyKillingBlow(LivingEntity victim) {
		DamageSource lastDamage = victim.getLastDamageSource();
		if (lastDamage == null) {
			return Optional.empty();
		}
		return KillMethodClassifier.classify(lastDamage);
	}

	static PlayerDamageHistory historyOf(LivingEntity entity) {
		if (entity instanceof DamageHistoryAccess access) {
			return access.mob_combat_rebalance$getDamageHistory();
		}
		return new PlayerDamageHistory();
	}

	private static Identifier weightedPick(RandomSource random, Object... poolWeightPairs) {
		int total = 0;
		for (int i = 1; i < poolWeightPairs.length; i += 2) {
			total += (Integer) poolWeightPairs[i];
		}
		int roll = random.nextInt(total);
		int cursor = 0;
		for (int i = 0; i < poolWeightPairs.length; i += 2) {
			cursor += (Integer) poolWeightPairs[i + 1];
			if (roll < cursor) {
				return (Identifier) poolWeightPairs[i];
			}
		}
		return (Identifier) poolWeightPairs[poolWeightPairs.length - 2];
	}
}
