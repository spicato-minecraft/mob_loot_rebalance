package mob_loot_rebalance.mixin;

import mob_loot_rebalance.feedback.EliteKillFeedbackService;
import mob_loot_rebalance.loot.EliteLootService;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityLootMixin {

	@Inject(method = "dropAllDeathLoot", at = @At("RETURN"))
	private void mob_loot_rebalance$eliteKillPayouts(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		EliteLootService.dropEliteLoot(self, level, damageSource);
		EliteKillFeedbackService.playEliteKillFeedback(self, level, damageSource);
	}
}
