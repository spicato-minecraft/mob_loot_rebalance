package mob_loot_rebalance.mixin;

import mob_loot_rebalance.loot.OuterSlotWeights;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(NestedLootTable.class)
public abstract class NestedLootTableMixin {

	@Inject(method = "createItemStack", at = @At("HEAD"))
	private void mob_loot_rebalance$pushNestedTable(Consumer<ItemStack> consumer, LootContext context, CallbackInfo ci) {
		((NestedLootTableAccessor) this).mob_loot_rebalance$contents()
				.left()
				.map(ResourceKey::identifier)
				.ifPresent(OuterSlotWeights::pushGeneratingTable);
	}

	@Inject(method = "createItemStack", at = @At("RETURN"))
	private void mob_loot_rebalance$popNestedTable(Consumer<ItemStack> consumer, LootContext context, CallbackInfo ci) {
		if (((NestedLootTableAccessor) this).mob_loot_rebalance$contents().left().isPresent()) {
			OuterSlotWeights.popGeneratingTable();
		}
	}
}
