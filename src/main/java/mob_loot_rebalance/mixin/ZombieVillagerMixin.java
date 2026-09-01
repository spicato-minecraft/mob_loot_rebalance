package mob_loot_rebalance.mixin;

import mob_loot_rebalance.elite.EliteModifiers;
import mob_loot_rebalance.elite.EliteState;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * AC-4b: strip elite state before zombie villager converts (entity is discarded; villager is fresh).
 */
@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerMixin {

	@Inject(method = "startConverting", at = @At("HEAD"))
	private void mob_loot_rebalance$stripEliteOnCureStart(CallbackInfo ci) {
		ZombieVillager self = (ZombieVillager) (Object) this;
		if (EliteState.isElite(self)) {
			EliteState.clearElite(self);
			EliteModifiers.remove(self);
		}
	}
}
