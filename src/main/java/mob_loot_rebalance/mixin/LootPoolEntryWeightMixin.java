package mob_loot_rebalance.mixin;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.loot.OuterSlot;
import mob_loot_rebalance.loot.OuterSlotWeights;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.level.storage.loot.entries.UniformContainerBase$EntryBase")
public abstract class LootPoolEntryWeightMixin {

	@Inject(method = "getWeight", at = @At("RETURN"), cancellable = true)
	private void mob_loot_rebalance$remapOuterSlotWeights(float luck, CallbackInfoReturnable<Integer> cir) {
		OuterSlot slot = OuterSlotWeights.expandingSlot();
		if (slot == null || !OuterSlotWeights.remappingOuterSlots()) {
			return;
		}
		int remapped = OuterSlotWeights.remap(slot, cir.getReturnValueI(), ModConfig.get().elite_loot);
		if (remapped != cir.getReturnValueI()) {
			cir.setReturnValue(remapped);
		}
	}
}
