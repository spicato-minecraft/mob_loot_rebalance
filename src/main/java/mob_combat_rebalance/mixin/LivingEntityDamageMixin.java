package mob_combat_rebalance.mixin;

import mob_combat_rebalance.config.ModConfig;
import mob_combat_rebalance.killmethod.DamageHistoryAccess;
import mob_combat_rebalance.killmethod.KillMethod;
import mob_combat_rebalance.killmethod.KillMethodClassifier;
import mob_combat_rebalance.killmethod.PlayerDamageHistory;
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
	private final PlayerDamageHistory mob_combat_rebalance$damageHistory = new PlayerDamageHistory();

	@Inject(method = "hurtServer", at = @At("RETURN"))
	private void mob_combat_rebalance$recordPlayerDamage(
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
			mob_combat_rebalance$damageHistory.record(tick, method, retention);
		});
	}

	@Override
	public PlayerDamageHistory mob_combat_rebalance$getDamageHistory() {
		return mob_combat_rebalance$damageHistory;
	}
}
