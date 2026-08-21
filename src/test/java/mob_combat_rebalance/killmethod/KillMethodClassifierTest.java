package mob_combat_rebalance.killmethod;

import mob_combat_rebalance.MinecraftTestBootstrap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KillMethodClassifierTest {

	@BeforeAll
	static void bootstrapMinecraft() {
		MinecraftTestBootstrap.init();
		bindItemTags();
	}

	@Test
	void swordHitIsMelee() {
		assertEquals(KillMethod.MELEE, classify(DamageTypes.PLAYER_ATTACK, new ItemStack(Items.IRON_SWORD)));
	}

	@Test
	void axeHitIsMelee() {
		assertEquals(KillMethod.MELEE, classify(DamageTypes.PLAYER_ATTACK, new ItemStack(Items.IRON_AXE)));
	}

	@Test
	void fistHitIsMelee() {
		assertEquals(KillMethod.MELEE, classify(DamageTypes.PLAYER_ATTACK, ItemStack.EMPTY));
	}

	@Test
	void bowHitIsRanged() {
		assertEquals(KillMethod.RANGED, classify(DamageTypes.ARROW, new ItemStack(Items.BOW)));
	}

	@Test
	void thrownTridentIsRanged() {
		assertEquals(KillMethod.RANGED, classify(DamageTypes.TRIDENT, new ItemStack(Items.TRIDENT)));
	}

	@Test
	void tridentMeleeIsMelee() {
		assertEquals(KillMethod.MELEE, classify(DamageTypes.PLAYER_ATTACK, new ItemStack(Items.TRIDENT)));
	}

	@Test
	void maceHitIsMelee() {
		assertEquals(KillMethod.MELEE, classify(DamageTypes.MACE_SMASH, new ItemStack(Items.MACE)));
	}

	@Test
	void spearJabIsMelee() {
		assertEquals(KillMethod.MELEE, classify(DamageTypes.SPEAR, new ItemStack(Items.IRON_SPEAR)));
	}

	@Test
	void thrownSpearIsRanged() {
		assertEquals(KillMethod.RANGED, classify(DamageTypes.THROWN, new ItemStack(Items.IRON_SPEAR)));
	}

	@Test
	void fireBurnOutDoesNotClassify() {
		assertTrue(KillMethodClassifier.classifyAttributedHit(DamageTypes.ON_FIRE, ItemStack.EMPTY).isEmpty());
		assertTrue(KillMethodClassifier.isFireBurnOutKey(DamageTypes.ON_FIRE));
	}

	@Test
	void environmentalDamageWithoutPlayerDoesNotClassify() {
		assertTrue(KillMethodClassifier.classifyAttributedHit(DamageTypes.FALL, ItemStack.EMPTY).isEmpty());
	}

	private static KillMethod classify(net.minecraft.resources.ResourceKey<net.minecraft.world.damagesource.DamageType> type, ItemStack weapon) {
		Optional<KillMethod> method = KillMethodClassifier.classifyAttributedHit(type, weapon);
		assertTrue(method.isPresent(), "Expected classified kill method for " + type);
		return method.get();
	}

	private static void bindItemTags() {
		BuiltInRegistries.ITEM.forEach(item -> bindTagsOnItem(item, List.of()));
		bindTagsOnItem(Items.IRON_SWORD, List.of(ItemTags.SWORDS));
		bindTagsOnItem(Items.IRON_AXE, List.of(ItemTags.AXES));
		bindTagsOnItem(Items.IRON_SPEAR, List.of(ItemTags.SPEARS));
	}

	private static void bindTagsOnItem(Item item, List<net.minecraft.tags.TagKey<Item>> tags) {
		Holder<Item> holder = BuiltInRegistries.ITEM.wrapAsHolder(item);
		if (holder instanceof Holder.Reference<Item> reference) {
			try {
				Method bindTags = Holder.Reference.class.getDeclaredMethod("bindTags", Collection.class);
				bindTags.setAccessible(true);
				bindTags.invoke(reference, tags);
			} catch (ReflectiveOperationException e) {
				throw new IllegalStateException("Failed to bind item tags for tests", e);
			}
		}
	}
}
