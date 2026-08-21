package mob_combat_rebalance.loot;

import com.mojang.serialization.MapCodec;
import mob_combat_rebalance.Mob_combat_rebalance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves {@code enchanting_book_roll} via {@link KillMethodRouter} sub-pool selected for this kill.
 */
public record RoutedEnchantedBookFunction() implements LootItemFunction {

	public static final MapCodec<RoutedEnchantedBookFunction> CODEC = MapCodec.unit(new RoutedEnchantedBookFunction());
	public static final LootItemFunctionType<RoutedEnchantedBookFunction> TYPE = new LootItemFunctionType<>(CODEC);

	public static void register() {
		Registry.register(
				BuiltInRegistries.LOOT_FUNCTION_TYPE,
				Identifier.fromNamespaceAndPath(Mob_combat_rebalance.MOD_ID, "routed_enchanted_book"),
				TYPE
		);
	}

	@Override
	public LootItemFunctionType<RoutedEnchantedBookFunction> getType() {
		return TYPE;
	}

	@Override
	public ItemStack apply(ItemStack stack, LootContext context) {
		EliteLootRollContext rollContext = EliteLootRollContext.current();
		if (rollContext == null) {
			return ItemStack.EMPTY;
		}

		ResourceKey<LootTable> poolKey = ResourceKey.create(Registries.LOOT_TABLE, rollContext.bookPool());
		LootTable poolTable = context.getLevel().getServer().reloadableRegistries().getLootTable(poolKey);
		List<ItemStack> items = new ArrayList<>();
		poolTable.getRandomItems(context, items::add);
		if (items.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return items.getFirst();
	}
}
