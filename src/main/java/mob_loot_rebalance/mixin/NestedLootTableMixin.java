package mob_loot_rebalance.mixin;

import mob_loot_rebalance.loot.NestedLootTableRefs;
import mob_loot_rebalance.loot.OuterSlotWeights;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(targets = {
		"net.minecraft.world.level.storage.loot.entries.NestedLootTable$1",
		"net.minecraft.world.level.storage.loot.entries.NestedLootTable$2"
})
public abstract class NestedLootTableMixin {

	@Shadow
	@Final
	private UniformContainerBase this$0;

	@Inject(method = "createItemStack", at = @At("HEAD"))
	private void mob_loot_rebalance$pushNestedTable(Consumer<ItemStack> consumer, LootContext context, CallbackInfo ci) {
		Identifier nestedId = nestedReferenceId();
		if (nestedId != null) {
			OuterSlotWeights.pushGeneratingTable(nestedId);
		}
	}

	@Inject(method = "createItemStack", at = @At("RETURN"))
	private void mob_loot_rebalance$popNestedTable(Consumer<ItemStack> consumer, LootContext context, CallbackInfo ci) {
		if (nestedReferenceId() != null) {
			OuterSlotWeights.popGeneratingTable();
		}
	}

	private Identifier nestedReferenceId() {
		if (!(this.this$0 instanceof NestedLootTable nested)) {
			return null;
		}
		return NestedLootTableRefs.referenceId(((NestedLootTableAccessor) nested).mob_loot_rebalance$value());
	}
}
