package mob_loot_rebalance.elite;

import mob_loot_rebalance.Mob_loot_rebalance;
import mob_loot_rebalance.config.ModConfig;
import mob_loot_rebalance.mixin.CreeperAccessor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;

/**
 * Stat modifiers applied to elite mobs (AC-5, AC-6, AC-7, AC-8).
 * Visual cues: {@link #SCALE_MULTIPLIER} plus configurable ambient particles ({@code elites.ambient_particle}).
 */
public final class EliteModifiers {

	public static final double HEALTH_MULTIPLIER = 1.5;
	public static final double DAMAGE_MULTIPLIER = 1.5;
	public static final double SCALE_MULTIPLIER = 1.3;
	public static final double EXPLOSION_MULTIPLIER = 1.5;
	public static final double KNOCKBACK_RESISTANCE_BONUS = 0.2;

	private static final Identifier HEALTH_MODIFIER_ID = id("health");
	private static final Identifier DAMAGE_MODIFIER_ID = id("damage");
	private static final Identifier SCALE_MODIFIER_ID = id("scale");
	private static final Identifier KNOCKBACK_RESISTANCE_MODIFIER_ID = id("knockback_resistance");

	private EliteModifiers() {
	}

	public static void apply(Mob mob, ServerLevel level) {
		applyAttributes(mob);
		applyCreeperExplosionRadius(mob);
		syncUatGlow(mob);
	}

	public static void ensureApplied(Mob mob, ServerLevel level) {
		if (!EliteState.isElite(mob)) {
			return;
		}
		if (!hasEliteModifiers(mob)) {
			applyAttributes(mob);
			applyCreeperExplosionRadius(mob);
		}
		syncUatGlow(mob);
	}

	public static void remove(Mob mob) {
		removeAttributeModifier(mob, Attributes.MAX_HEALTH, HEALTH_MODIFIER_ID);
		removeAttributeModifier(mob, Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER_ID);
		removeAttributeModifier(mob, Attributes.SCALE, SCALE_MODIFIER_ID);
		removeAttributeModifier(mob, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER_ID);
		mob.removeEffect(MobEffects.GLOWING);
		if (mob instanceof Creeper creeper) {
			CreeperAccessor accessor = (CreeperAccessor) creeper;
			int restored = Math.max(1, Math.round(accessor.mob_loot_rebalance$getExplosionRadius() / (float) EXPLOSION_MULTIPLIER));
			accessor.mob_loot_rebalance$setExplosionRadius(restored);
		}
	}

	static void syncUatGlow(Mob mob) {
		if (ModConfig.get().elites.uat_glow) {
			if (!mob.hasEffect(MobEffects.GLOWING)) {
				mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, MobEffectInstance.INFINITE_DURATION, 0, false, false, true));
			}
		} else {
			mob.removeEffect(MobEffects.GLOWING);
		}
	}

	private static void applyAttributes(Mob mob) {
		addMultiplierModifier(mob, Attributes.MAX_HEALTH, HEALTH_MODIFIER_ID, HEALTH_MULTIPLIER);
		addMultiplierModifier(mob, Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER_ID, DAMAGE_MULTIPLIER);
		addMultiplierModifier(mob, Attributes.SCALE, SCALE_MODIFIER_ID, SCALE_MULTIPLIER);
		addFlatModifier(mob, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER_ID, KNOCKBACK_RESISTANCE_BONUS);
		mob.setHealth(mob.getMaxHealth());
	}

	private static void applyCreeperExplosionRadius(Mob mob) {
		if (mob instanceof Creeper creeper) {
			CreeperAccessor accessor = (CreeperAccessor) creeper;
			int scaled = Math.max(1, Math.round(accessor.mob_loot_rebalance$getExplosionRadius() * (float) EXPLOSION_MULTIPLIER));
			accessor.mob_loot_rebalance$setExplosionRadius(scaled);
		}
	}

	private static boolean hasEliteModifiers(Mob mob) {
		AttributeInstance health = mob.getAttribute(Attributes.MAX_HEALTH);
		return health != null && health.getModifier(HEALTH_MODIFIER_ID) != null;
	}

	private static void addMultiplierModifier(Mob mob, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, Identifier modifierId, double multiplier) {
		AttributeInstance instance = mob.getAttribute(attribute);
		if (instance == null || instance.getModifier(modifierId) != null) {
			return;
		}
		instance.addPermanentModifier(new AttributeModifier(
				modifierId,
				multiplier - 1.0,
				AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
		));
	}

	private static void addFlatModifier(Mob mob, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, Identifier modifierId, double amount) {
		AttributeInstance instance = mob.getAttribute(attribute);
		if (instance == null || instance.getModifier(modifierId) != null) {
			return;
		}
		instance.addPermanentModifier(new AttributeModifier(
				modifierId,
				amount,
				AttributeModifier.Operation.ADD_VALUE
		));
	}

	private static void removeAttributeModifier(Mob mob, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, Identifier modifierId) {
		AttributeInstance instance = mob.getAttribute(attribute);
		if (instance != null) {
			instance.removeModifier(modifierId);
		}
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Mob_loot_rebalance.MOD_ID, path);
	}
}
