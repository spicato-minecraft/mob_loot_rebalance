package mob_loot_rebalance.loot;

import mob_loot_rebalance.Mob_loot_rebalance;
import mob_loot_rebalance.config.EliteLootConfig;
import net.minecraft.resources.Identifier;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Playtest knobs for the elite outer table. Datapack JSON keeps shipped defaults;
 * runtime generation remaps entry weights from {@link EliteLootConfig}.
 */
public final class OuterSlotWeights {

	private static final ThreadLocal<Deque<Identifier>> GENERATING_TABLES = ThreadLocal.withInitial(ArrayDeque::new);
	private static final ThreadLocal<Deque<OuterSlot>> EXPANDING_SLOTS = ThreadLocal.withInitial(ArrayDeque::new);

	private OuterSlotWeights() {
	}

	public static void pushGeneratingTable(Identifier tableId) {
		GENERATING_TABLES.get().push(tableId);
	}

	public static void popGeneratingTable() {
		Deque<Identifier> stack = GENERATING_TABLES.get();
		if (!stack.isEmpty()) {
			stack.pop();
		}
		if (stack.isEmpty()) {
			GENERATING_TABLES.remove();
		}
	}

	public static boolean remappingOuterSlots() {
		return isOuterEliteTable(GENERATING_TABLES.get().peek());
	}

	public static void pushExpandingSlot(OuterSlot slot) {
		EXPANDING_SLOTS.get().push(slot);
	}

	public static void popExpandingSlot() {
		Deque<OuterSlot> stack = EXPANDING_SLOTS.get();
		if (!stack.isEmpty()) {
			stack.pop();
		}
		if (stack.isEmpty()) {
			EXPANDING_SLOTS.remove();
		}
	}

	public static OuterSlot expandingSlot() {
		return EXPANDING_SLOTS.get().peek();
	}

	public static boolean isOuterEliteTable(Identifier tableId) {
		if (tableId == null || !Mob_loot_rebalance.MOD_ID.equals(tableId.getNamespace())) {
			return false;
		}
		String path = tableId.getPath();
		return path.startsWith("elite/")
				&& !path.startsWith("elite/books/")
				&& !path.startsWith("elite/potions/")
				&& !path.startsWith("elite/pools/");
	}

	public static OuterSlot classifyNestedTable(Identifier nestedTableId) {
		if (nestedTableId == null) {
			return OuterSlot.UNIQUE;
		}
		String path = nestedTableId.getPath();
		if ("minecraft".equals(nestedTableId.getNamespace()) && path.startsWith("entities/")) {
			return OuterSlot.CONSOLATION;
		}
		if (path.contains("/books/")) {
			return OuterSlot.BOOKS;
		}
		if (path.contains("/potions/")) {
			return OuterSlot.POTIONS;
		}
		return OuterSlot.UNIQUE;
	}

	public static int remap(OuterSlot slot, int bakedWeight, EliteLootConfig config) {
		if (bakedWeight <= 0) {
			return 0;
		}
		int layoutDefault = layoutWeight(slot);
		int live = Math.max(0, liveWeight(slot, config));
		if (layoutDefault <= 0) {
			return live;
		}
		return Math.max(0, (int) Math.round(bakedWeight * (live / (double) layoutDefault)));
	}

	public static int layoutWeight(OuterSlot slot) {
		return switch (slot) {
			case CONSOLATION -> EliteLootLayout.CONSOLATION_WEIGHT;
			case BOOKS -> EliteLootLayout.BOOK_SLOT_WEIGHT;
			case POTIONS -> EliteLootLayout.POTION_SLOT_WEIGHT;
			case UNIQUE -> EliteLootLayout.UNIQUE_WEIGHT;
		};
	}

	public static int liveWeight(OuterSlot slot, EliteLootConfig config) {
		return switch (slot) {
			case CONSOLATION -> config.consolation_weight;
			case BOOKS -> config.books_weight;
			case POTIONS -> config.potions_weight;
			case UNIQUE -> config.unique_weight;
		};
	}

	public static int total(EliteLootConfig config) {
		return Math.max(0, config.consolation_weight)
				+ Math.max(0, config.books_weight)
				+ Math.max(0, config.potions_weight)
				+ Math.max(0, config.unique_weight);
	}

	public static double bookSlotRate(EliteLootConfig config) {
		int sum = total(config);
		if (sum <= 0) {
			return 0.0;
		}
		return Math.max(0, config.books_weight) / (double) sum;
	}
}
