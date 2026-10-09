package mob_loot_rebalance.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Elite enchanted books match {@code structure_loot_rebalance} loot books: stored enchants plus
 * the same {@code custom_data} tag so stacks merge with structure loot and read as normal books.
 */
public final class EliteEnchantedBooks {

	/**
	 * Keep in sync with {@code structure_loot_rebalance.loot.ModEnchantedBooks} for stack identity.
	 */
	public static final String LOOT_BOOK_CUSTOM_DATA_KEY = "structure_loot_rebalance";

	private EliteEnchantedBooks() {
	}

	public static ItemStack create(Holder<Enchantment> enchantment, int level) {
		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		ItemEnchantments.Mutable stored = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		stored.set(enchantment, level);
		book.set(DataComponents.STORED_ENCHANTMENTS, stored.toImmutable());
		tagAsLootBook(book);
		return book;
	}

	public static ItemStack create(String enchantmentId, int level, HolderGetter.Provider registries) {
		Identifier id = Identifier.parse(enchantmentId);
		Holder<Enchantment> holder = registries.lookupOrThrow(Registries.ENCHANTMENT)
				.getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, id));
		return create(holder, level);
	}

	private static void tagAsLootBook(ItemStack book) {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean(LOOT_BOOK_CUSTOM_DATA_KEY, true);
		book.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}
}
