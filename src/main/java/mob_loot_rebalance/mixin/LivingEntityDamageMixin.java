package mob_loot_rebalance.mixin;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.killmethod.DamageHistoryAccess;
import mob_loot_rebalance.killmethod.KillMethod;
import mob_loot_rebalance.killmethod.KillMethodClassifier;
import mob_loot_rebalance.killmethod.PlayerDamageHistory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageMixin implements DamageHistoryAccess {
	@Unique
	private final PlayerDamageHistory mob_loot_rebalance$damageHistory = new PlayerDamageHistory();

	@Inject(method = "hurtServer", at = @At("RETURN"))
	private void mob_loot_rebalance$recordPlayerDamage(
			ServerLevel serverLevel,
			DamageSource damageSource,
			float amount,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (!Boolean.TRUE.equals(cir.getReturnValue())) {
			return;
		}

		KillMethodClassifier.classify(damageSource).ifPresent(method -> {
			long tick = serverLevel.getGameTime();
			int retention = ModConfig.get().kill_method.lookback_ticks;
			mob_loot_rebalance$damageHistory.record(tick, method, retention);
		});
	}

	@Override
	public PlayerDamageHistory mob_loot_rebalance$getDamageHistory() {
		return mob_loot_rebalance$damageHistory;
	}
}
