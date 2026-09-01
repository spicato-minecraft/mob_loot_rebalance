package mob_loot_rebalance.killmethod;

import mob_loot_rebalance.config.ModConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Classifies kill method for core weapon books (Sharpness / Power) and theme signatures.
 */
public final class KillMethodRouter {

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

	private static Optional<KillMethod> classifyKillingBlow(LivingEntity victim) {
		DamageSource lastDamage = victim.getLastDamageSource();
		if (lastDamage == null) {
			return Optional.empty();
		}
		return KillMethodClassifier.classify(lastDamage);
	}

	static PlayerDamageHistory historyOf(LivingEntity entity) {
		if (entity instanceof DamageHistoryAccess access) {
			return access.mob_loot_rebalance$getDamageHistory();
		}
		return new PlayerDamageHistory();
	}
}
