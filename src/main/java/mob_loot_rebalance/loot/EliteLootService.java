package mob_loot_rebalance.loot;

import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.killmethod.KillMethodRouter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs exactly one additional generate of the mob's elite table after vanilla death loot (AC-10, AC-11b, AC-21).
 */
public final class EliteLootService {

	private EliteLootService() {
	}

	public static void dropEliteLoot(LivingEntity entity, ServerLevel level, DamageSource damageSource) {
		ModConfig config = ModConfig.get();
		if (!EliteLootDropPolicy.shouldDropEliteLoot(entity, damageSource, config)) {
			return;
		}

		Entity killer = entity.getKillCredit();
		var killMethod = KillMethodRouter.resolveKillMethod(entity, killer, config);
		EliteLootRollContext context = new EliteLootRollContext(
				killMethod,
				config.combat_potion.allow_invisibility
		);

		if (OuterSlotWeights.total(config.elite_loot) <= 0) {
			return;
		}

		EliteLootRollContext.run(context, () -> {
			ResourceKey<LootTable> tableKey = ResourceKey.create(
					Registries.LOOT_TABLE,
					EliteLootTables.forEntityType(entity.getType())
			);
			OuterSlotWeights.pushGeneratingTable(tableKey.identifier());
			try {
				List<ItemStack> generated = new ArrayList<>();
				entity.dropFromLootTable(level, damageSource, false, tableKey, generated::add);
				for (ItemStack stack : generated) {
					if (shouldDropStack(stack, context)) {
						entity.spawnAtLocation(level, stack);
					}
				}
			} finally {
				OuterSlotWeights.popGeneratingTable();
			}
		});
	}

	static boolean shouldDropStack(ItemStack stack, EliteLootRollContext context) {
		if (stack.isEmpty()) {
			return false;
		}
		if (context.allowInvisibilityPotion()) {
			return true;
		}
		if (!stack.is(Items.SPLASH_POTION) && !stack.is(Items.LINGERING_POTION)) {
			return true;
		}
		PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
		return !contents.is(Potions.INVISIBILITY);
	}
}
