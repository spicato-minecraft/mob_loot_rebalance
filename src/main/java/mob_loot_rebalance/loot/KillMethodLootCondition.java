package mob_loot_rebalance.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mob_loot_rebalance.Mob_loot_rebalance;
import mob_loot_rebalance.killmethod.KillMethod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Locale;

/**
 * Gates a loot entry on the elite kill method stored in {@link EliteLootRollContext}.
 * Fallback never matches {@code melee} or {@code ranged}.
 */
public record KillMethodLootCondition(KillMethod method) implements LootItemCondition {

	public static final MapCodec<KillMethodLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.xmap(KillMethodLootCondition::parseMethod, KillMethodLootCondition::formatMethod)
					.fieldOf("method")
					.forGetter(KillMethodLootCondition::method)
	).apply(instance, KillMethodLootCondition::new));

	public static final LootItemConditionType TYPE = new LootItemConditionType(CODEC);

	public static void register() {
		Registry.register(
				BuiltInRegistries.LOOT_CONDITION_TYPE,
				Identifier.fromNamespaceAndPath(Mob_loot_rebalance.MOD_ID, "kill_method"),
				TYPE
		);
	}

	public KillMethodLootCondition {
		if (method != KillMethod.MELEE && method != KillMethod.RANGED) {
			throw new IllegalArgumentException("kill_method condition must be melee or ranged, not " + method);
		}
	}

	@Override
	public LootItemConditionType getType() {
		return TYPE;
	}

	@Override
	public boolean test(LootContext context) {
		EliteLootRollContext rollContext = EliteLootRollContext.current();
		if (rollContext == null) {
			return false;
		}
		return rollContext.killMethod() == method;
	}

	private static KillMethod parseMethod(String raw) {
		return KillMethod.valueOf(raw.trim().toUpperCase(Locale.ROOT));
	}

	private static String formatMethod(KillMethod method) {
		return method.name().toLowerCase(Locale.ROOT);
	}
}
