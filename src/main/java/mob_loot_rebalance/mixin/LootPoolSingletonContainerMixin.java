package mob_loot_rebalance.mixin;

import mob_loot_rebalance.loot.NestedLootTableRefs;
import mob_loot_rebalance.loot.OuterSlot;
import mob_loot_rebalance.loot.OuterSlotWeights;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.ExpandableContainerBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.entries.SingleEntryContainerBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin({SingleEntryContainerBase.class, ExpandableContainerBase.class})
public abstract class LootPoolSingletonContainerMixin {

	@Inject(method = "expandRaw", at = @At("HEAD"))
	private void mob_loot_rebalance$pushOuterSlot(
			LootContext context,
			Consumer<LootPoolEntry> consumer,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (!OuterSlotWeights.remappingOuterSlots()) {
			return;
		}
		OuterSlotWeights.pushExpandingSlot(classify(this));
	}

	@Inject(method = "expandRaw", at = @At("RETURN"))
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

	private static OuterSlot classify(Object container) {
		if (container instanceof NestedLootTable nested) {
			Identifier nestedId = NestedLootTableRefs.referenceId(
					((NestedLootTableAccessor) nested).mob_loot_rebalance$value()
			);
			return OuterSlotWeights.classifyNestedTable(nestedId);
		}
		return OuterSlot.UNIQUE;
	}
}
