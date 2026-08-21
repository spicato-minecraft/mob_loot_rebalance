package mob_combat_rebalance.feedback;

import mob_combat_rebalance.Mob_combat_rebalance;
import mob_combat_rebalance.config.ModConfig;
import mob_combat_rebalance.payout.ElitePayoutPolicy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Short vanilla sound cue on player elite kills (AC-22).
 */
public final class EliteKillFeedbackService {

	private static final Logger LOGGER = LogManager.getLogger(Mob_combat_rebalance.MOD_ID + ".feedback");
	private static final Identifier DEFAULT_SOUND = Identifier.parse("minecraft:block.amethyst_block.chime");

	private EliteKillFeedbackService() {
	}

	public static void playEliteKillFeedback(LivingEntity entity, ServerLevel level, DamageSource damageSource) {
		ModConfig config = ModConfig.get();
		if (!ElitePayoutPolicy.isPlayerKilledElite(entity, config)) {
			return;
		}

		SoundEvent sound = resolveSound(config.feedback.elite_kill_sound);
		level.playSound(
				null,
				entity.getX(),
				entity.getY(),
				entity.getZ(),
				sound,
				SoundSource.PLAYERS,
				0.85F,
				1.0F
		);
	}

	static SoundEvent resolveSound(String configuredSound) {
		Identifier id = parseSoundId(configuredSound);
		SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(id);
		if (sound == null) {
			LOGGER.warn("Unknown elite_kill_sound {}; falling back to {}", id, DEFAULT_SOUND);
			sound = BuiltInRegistries.SOUND_EVENT.getValue(DEFAULT_SOUND);
		}
		return sound;
	}

	private static Identifier parseSoundId(String configuredSound) {
		if (configuredSound == null || configuredSound.isBlank()) {
			return DEFAULT_SOUND;
		}
		return Identifier.parse(configuredSound.trim());
	}
}
