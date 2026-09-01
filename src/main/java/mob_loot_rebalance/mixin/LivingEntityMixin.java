package mob_loot_rebalance.mixin;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.xp.EliteBonusXpPolicy;
import mob_loot_rebalance.xp.XpUpliftPolicy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "getExperienceReward", at = @At("RETURN"), cancellable = true)
	private void mob_loot_rebalance$adjustExperienceReward(ServerLevel level, Entity entity, CallbackInfoReturnable<Integer> cir) {
		LivingEntity self = (LivingEntity) (Object) this;
		ModConfig config = ModConfig.get();
		int vanillaReward = cir.getReturnValue();
		int upliftedReward = XpUpliftPolicy.resolveUpliftedReward(config, self, vanillaReward);
		int eliteBonus = EliteBonusXpPolicy.resolveBonus(config, self, upliftedReward);
		int totalReward = upliftedReward + eliteBonus;
		if (totalReward != vanillaReward) {
			cir.setReturnValue(totalReward);
		}
	}
}
