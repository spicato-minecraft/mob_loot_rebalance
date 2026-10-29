package mob_loot_rebalance.loot;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Set;

/**
 * Doubles gunpowder from vanilla creeper and ghast entity tables after each generate.
 * Elite unique gunpowder lives in datapack JSON (creeper jackpot, witch ingredients).
 */
public final class GunpowderDrops {

	public static final int QUANTITY_MULTIPLIER = 2;

	public static final Identifier CREEPER_TABLE = Identifier.fromNamespaceAndPath("minecraft", "entities/creeper");
	public static final Identifier GHAST_TABLE = Identifier.fromNamespaceAndPath("minecraft", "entities/ghast");

	private static final Set<Identifier> VANILLA_GUNPOWDER_TABLES = Set.of(CREEPER_TABLE, GHAST_TABLE);

	private GunpowderDrops() {
	}

	public static void init() {
		LootTableEvents.MODIFY_DROPS.register((holder, context, drops) -> {
			Identifier tableId = holder.unwrapKey()
					.map(ResourceKey::identifier)
					.orElse(null);
			if (!appliesTo(tableId)) {
				return;
			}
			multiplyGunpowder(drops);
		});
	}

	static boolean appliesTo(Identifier tableId) {
		return tableId != null && VANILLA_GUNPOWDER_TABLES.contains(tableId);
	}

	static void multiplyGunpowder(List<ItemStack> drops) {
		for (ItemStack stack : drops) {
			if (!stack.is(Items.GUNPOWDER) || stack.isEmpty()) {
				continue;
			}
			int doubled = Math.min(stack.getCount() * QUANTITY_MULTIPLIER, stack.getMaxStackSize());
			stack.setCount(doubled);
		}
	}
}
