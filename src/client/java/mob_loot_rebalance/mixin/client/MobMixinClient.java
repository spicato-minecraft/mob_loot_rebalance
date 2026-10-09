package mob_loot_rebalance.mixin.client;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.elite.EliteAmbientParticles;
import mob_loot_rebalance.elite.EliteCombatHandshake;
import mob_loot_rebalance.elite.EliteState;
import mob_loot_rebalance.elite.SpyglassParticleFrame;
import mob_loot_rebalance.elite.SpyglassParticleGate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixinClient {

	@Inject(method = "aiStep", at = @At("TAIL"))
	private void mob_loot_rebalance$clientEliteParticles(CallbackInfo ci) {
		Mob self = (Mob) (Object) this;
		if (!self.level().isClientSide() || !EliteState.isElite(self)) {
			return;
		}
		if (!EliteCombatHandshake.shouldApplyFallbackPresentation()) {
			return;
		}
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}
		SpyglassParticleFrame.capture(player);
		var config = ModConfig.get().elites;
		if (!SpyglassParticleGate.shouldPulse(
				SpyglassParticleFrame.showing(),
				SpyglassParticleFrame.raisedThisTick(),
				self.tickCount,
				config.particle_interval_ticks
		)) {
			return;
		}
		EliteAmbientParticles.emit(self, self.level(), config);
	}
}
