package mob_loot_rebalance.mixin;

import mob_loot_rebalance.loot.OuterSlot;
import mob_loot_rebalance.loot.OuterSlotWeights;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(LootPoolSingletonContainer.class)
public abstract class LootPoolSingletonContainerMixin {

	@Inject(method = "expand", at = @At("HEAD"))
	private void mob_loot_rebalance$pushOuterSlot(
			LootContext context,
			Consumer<LootPoolEntry> consumer,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (!OuterSlotWeights.remappingOuterSlots()) {
			return;
		}
		OuterSlotWeights.pushExpandingSlot(classify((LootPoolSingletonContainer) (Object) this));
	}

	@Inject(method = "expand", at = @At("RETURN"))
	private void mob_loot_rebalance$popOuterSlot(
			LootContext context,
			Consumer<LootPoolEntry> consumer,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (!OuterSlotWeights.remappingOuterSlots()) {
			return;
		}
		OuterSlotWeights.popExpandingSlot();
	}

	private static OuterSlot classify(LootPoolSingletonContainer container) {
		if (container instanceof NestedLootTable nested) {
			Identifier nestedId = ((NestedLootTableAccessor) nested).mob_loot_rebalance$contents()
					.left()
					.map(ResourceKey::identifier)
					.orElse(null);
			return OuterSlotWeights.classifyNestedTable(nestedId);
		}
		return OuterSlot.UNIQUE;
	}
}
