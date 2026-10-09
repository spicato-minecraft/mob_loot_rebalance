package mob_loot_rebalance.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mob_loot_rebalance.Mob_loot_rebalance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

/**
 * Replaces the rolled stack with a normal enchanted book (same shape as structure loot rebalance).
 */
public final class FixedEnchantBookFunction implements LootItemFunction {

	public static final MapCodec<FixedEnchantBookFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
					Codec.STRING.fieldOf("enchantment").forGetter(FixedEnchantBookFunction::enchantmentId),
					Codec.INT.optionalFieldOf("level", 1).forGetter(FixedEnchantBookFunction::level)
			).apply(instance, FixedEnchantBookFunction::new)
	);

	private final String enchantmentId;
	private final int level;

	public FixedEnchantBookFunction(String enchantmentId, int level) {
		this.enchantmentId = enchantmentId;
		this.level = Math.max(1, level);
	}

	public static void register() {
		Registry.register(
				BuiltInRegistries.LOOT_FUNCTION_TYPE,
				Identifier.fromNamespaceAndPath(Mob_loot_rebalance.MOD_ID, "fixed_enchant_book"),
				CODEC
		);
	}

	@Override
	public MapCodec<FixedEnchantBookFunction> codec() {
		return CODEC;
	}

	public String enchantmentId() {
		return enchantmentId;
	}

	public int level() {
		return level;
	}

	@Override
	public ItemStack apply(ItemStack stack, LootContext context) {
		return EliteEnchantedBooks.create(enchantmentId, level, context.getResolver());
	}
}
