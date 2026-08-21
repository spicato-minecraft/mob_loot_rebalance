package mob_combat_rebalance.killmethod;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TridentItem;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Maps player-caused {@link DamageSource} instances to {@link KillMethod} (AC-16, AC-17).
 */
public final class KillMethodClassifier {
	private KillMethodClassifier() {
	}

	public static Optional<KillMethod> classify(DamageSource source) {
		if (isFireBurnOutDamage(source)) {
			return Optional.empty();
		}

		if (KillMethodClassifier.resolveAttackingPlayer(source) == null) {
			return Optional.empty();
		}

		return classifyAttributedHit(damageTypeKey(source), source.getWeaponItem());
	}

	/**
	 * Classifies a player-attributed hit without requiring a live {@link DamageSource} registry lookup.
	 * Package-private for unit tests.
	 */
	static Optional<KillMethod> classifyAttributedHit(ResourceKey<DamageType> damageType, ItemStack weapon) {
		if (isFireBurnOutKey(damageType)) {
			return Optional.empty();
		}
		if (damageType.equals(DamageTypes.ARROW) || damageType.equals(DamageTypes.TRIDENT)) {
			return Optional.of(KillMethod.RANGED);
		}
		if (damageType.equals(DamageTypes.THROWN)) {
			if (weapon != null && weapon.is(ItemTags.SPEARS)) {
				return Optional.of(KillMethod.RANGED);
			}
			return Optional.empty();
		}
		if (damageType.equals(DamageTypes.MACE_SMASH) || damageType.equals(DamageTypes.SPEAR)) {
			return Optional.of(KillMethod.MELEE);
		}
		if (damageType.equals(DamageTypes.PLAYER_ATTACK)) {
			return classifyPlayerAttackWeapon(weapon);
		}
		return Optional.empty();
	}

	static boolean isFireBurnOutKey(ResourceKey<DamageType> damageType) {
		return damageType.equals(DamageTypes.ON_FIRE)
				|| damageType.equals(DamageTypes.IN_FIRE)
				|| damageType.equals(DamageTypes.CAMPFIRE);
	}

	private static ResourceKey<DamageType> damageTypeKey(DamageSource source) {
		return source.typeHolder().unwrapKey().orElseThrow();
	}

	public static boolean isFireBurnOutDamage(DamageSource source) {
		return source.is(DamageTypeTags.IS_FIRE)
				|| source.is(DamageTypes.ON_FIRE)
				|| source.is(DamageTypes.IN_FIRE)
				|| source.is(DamageTypes.CAMPFIRE);
	}

	public static @Nullable Player resolveAttackingPlayer(DamageSource source) {
		Entity entity = source.getEntity();
		if (entity instanceof Player player) {
			return player;
		}

		Entity direct = source.getDirectEntity();
		if (direct instanceof Player player) {
			return player;
		}
		if (direct instanceof Projectile projectile) {
			Entity owner = projectile.getOwner();
			if (owner instanceof Player player) {
				return player;
			}
		}

		return null;
	}

	private static Optional<KillMethod> classifyPlayerAttackWeapon(ItemStack weapon) {
		if (weapon == null || weapon.isEmpty()) {
			return Optional.of(KillMethod.MELEE);
		}

		if (weapon.getItem() instanceof MaceItem
				|| weapon.getItem() instanceof TridentItem
				|| weapon.getItem() instanceof AxeItem) {
			return Optional.of(KillMethod.MELEE);
		}

		if (weapon.is(ItemTags.SWORDS)
				|| weapon.is(ItemTags.AXES)
				|| weapon.is(ItemTags.SPEARS)) {
			return Optional.of(KillMethod.MELEE);
		}

		return Optional.empty();
	}
}
