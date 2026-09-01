package mob_loot_rebalance.mixin;

import mob_loot_rebalance.elite.EliteAmbientParticles;
import mob_loot_rebalance.elite.EliteModifiers;
import mob_loot_rebalance.elite.EliteSpawnService;
import mob_loot_rebalance.elite.EliteState;
import mob_loot_rebalance.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.jspecify.annotations.Nullable;

@Mixin(Mob.class)
public abstract class MobMixin {

	@Inject(method = "finalizeSpawn", at = @At("RETURN"))
	private void mob_loot_rebalance$rollEliteAtSpawn(
			ServerLevelAccessor level,
			DifficultyInstance difficulty,
			EntitySpawnReason reason,
			@Nullable SpawnGroupData groupData,
			CallbackInfoReturnable<SpawnGroupData> cir
	) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		Mob self = (Mob) (Object) this;
		EliteSpawnService.tryRollElite(self, serverLevel, reason);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void mob_loot_rebalance$reapplyEliteAfterLoad(ValueInput valueInput, CallbackInfo ci) {
		Mob self = (Mob) (Object) this;
		if (self.level() instanceof ServerLevel serverLevel) {
			EliteModifiers.ensureApplied(self, serverLevel);
		}
	}

	@Inject(method = "aiStep", at = @At("TAIL"))
	private void mob_loot_rebalance$eliteAmbientParticles(CallbackInfo ci) {
		Mob self = (Mob) (Object) this;
		if (self.level().isClientSide() || !EliteState.isElite(self)) {
			return;
		}
		ModConfig config = ModConfig.get();
		int interval = config.elites.particle_interval_ticks;
		if (!EliteAmbientParticles.shouldEmit(config.elites) || self.tickCount % interval != 0) {
			return;
		}
		EliteAmbientParticles.emit(self, (ServerLevel) self.level(), config.elites);
	}
}
