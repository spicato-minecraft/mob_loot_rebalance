package mob_combat_rebalance.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mob_combat_rebalance.Mob_combat_rebalance;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Server-side config at {@code config/mob_combat_rebalance.json}.
 * Defaults are generated on first run; user file merges on top for missing keys.
 */
public final class ModConfig {
	private static final Logger LOGGER = LogManager.getLogger(Mob_combat_rebalance.MOD_ID + ".config");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FILE_NAME = Mob_combat_rebalance.MOD_ID + ".json";
	private static volatile ModConfig instance;

	public XpConfig xp = new XpConfig();
	public ElitesConfig elites = new ElitesConfig();
	public EliteLootConfig elite_loot = new EliteLootConfig();
	public KillMethodConfig kill_method = new KillMethodConfig();
	public CombatPotionConfig combat_potion = new CombatPotionConfig();
	public FeedbackConfig feedback = new FeedbackConfig();

	public static ModConfig get() {
		ModConfig current = instance;
		if (current == null) {
			current = load();
			instance = current;
		}
		return current;
	}

	public static void set(ModConfig config) {
		instance = config;
	}

	/** Test-only override; does not write to disk. */
	public static void setForTest(ModConfig config) {
		instance = config;
	}

	public static ModConfig load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
		ModConfig config = defaults();
		if (Files.isRegularFile(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
				ModConfig fromFile = GSON.fromJson(root, ModConfig.class);
				if (fromFile != null) {
					config = fromFile;
					boolean addedKeys = applyDefaultsForMissingKeys(root, config);
					normalize(config);
					if (addedKeys) {
						config.save();
					}
				}
			} catch (IOException | RuntimeException e) {
				LOGGER.error("Failed to read config {}, using defaults", path, e);
				normalize(config);
			}
		} else {
			try {
				Files.createDirectories(path.getParent());
				normalize(config);
				config.save();
			} catch (IOException e) {
				LOGGER.warn("Could not write default config to {}", path, e);
			}
		}
		return config;
	}

	public static ModConfig defaults() {
		return new ModConfig();
	}

	public boolean isXpPillarActive() {
		return xp.enabled;
	}

	public boolean isElitesPillarActive() {
		return elites.enabled && elites.spawn_chance > 0.0;
	}

	public boolean isEliteLootPillarActive() {
		return elite_loot.enabled;
	}

	public void save() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
		try (Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(this, writer);
		} catch (IOException e) {
			LOGGER.error("Failed to save config {}", path, e);
		}
	}

	static boolean applyDefaultsForMissingKeys(JsonObject root, ModConfig config) {
		boolean addedKeys = false;
		JsonObject xp = objectOrEmpty(root, "xp");
		JsonObject elites = objectOrEmpty(root, "elites");
		JsonObject eliteLoot = objectOrEmpty(root, "elite_loot");
		JsonObject killMethod = objectOrEmpty(root, "kill_method");
		JsonObject combatPotion = objectOrEmpty(root, "combat_potion");
		JsonObject feedback = objectOrEmpty(root, "feedback");

		if (!root.has("xp")) {
			config.xp = new XpConfig();
			addedKeys = true;
		} else {
			if (!xp.has("enabled")) {
				config.xp.enabled = false;
				addedKeys = true;
			}
			if (!xp.has("multiplier")) {
				config.xp.multiplier = 1.35;
				addedKeys = true;
			}
		}

		if (!root.has("elites")) {
			config.elites = new ElitesConfig();
			addedKeys = true;
		} else {
			if (!elites.has("enabled")) {
				config.elites.enabled = true;
				addedKeys = true;
			}
			if (!elites.has("spawn_chance")) {
				config.elites.spawn_chance = 0.07;
				addedKeys = true;
			}
			if (!elites.has("uat_glow")) {
				config.elites.uat_glow = false;
				addedKeys = true;
			}
			if (!elites.has("ambient_particle")) {
				config.elites.ambient_particle = "minecraft:dust";
				addedKeys = true;
			}
			if (!elites.has("particle_dust_color")) {
				config.elites.particle_dust_color = 0xFFFF0000;
				addedKeys = true;
			}
			if (!elites.has("particle_dust_scale")) {
				config.elites.particle_dust_scale = 0.4F;
				addedKeys = true;
			}
			if (!elites.has("particle_interval_ticks")) {
				config.elites.particle_interval_ticks = 30;
				addedKeys = true;
			}
		}

		if (!root.has("elite_loot")) {
			config.elite_loot = new EliteLootConfig();
			addedKeys = true;
		} else if (!eliteLoot.has("enabled")) {
			config.elite_loot.enabled = true;
			addedKeys = true;
		}

		if (!root.has("kill_method")) {
			config.kill_method = new KillMethodConfig();
			addedKeys = true;
		} else if (!killMethod.has("lookback_ticks")) {
			config.kill_method.lookback_ticks = 100;
			addedKeys = true;
		}

		if (!root.has("combat_potion")) {
			config.combat_potion = new CombatPotionConfig();
			addedKeys = true;
		} else if (!combatPotion.has("allow_invisibility")) {
			config.combat_potion.allow_invisibility = false;
			addedKeys = true;
		}

		if (!root.has("feedback")) {
			config.feedback = new FeedbackConfig();
			addedKeys = true;
		} else if (!feedback.has("elite_kill_sound")) {
			config.feedback.elite_kill_sound = "minecraft:block.amethyst_block.chime";
			addedKeys = true;
		}

		return addedKeys;
	}

	static void normalize(ModConfig config) {
		if (config.xp == null) {
			config.xp = new XpConfig();
		}
		if (config.elites == null) {
			config.elites = new ElitesConfig();
		}
		if (config.elite_loot == null) {
			config.elite_loot = new EliteLootConfig();
		}
		if (config.kill_method == null) {
			config.kill_method = new KillMethodConfig();
		}
		if (config.combat_potion == null) {
			config.combat_potion = new CombatPotionConfig();
		}
		if (config.feedback == null) {
			config.feedback = new FeedbackConfig();
		}

		if (config.xp.multiplier < 0.0) {
			LOGGER.warn("xp.multiplier {} is negative; clamping to 0", config.xp.multiplier);
			config.xp.multiplier = 0.0;
		}
		if (config.elites.spawn_chance < 0.0) {
			LOGGER.warn("elites.spawn_chance {} is negative; clamping to 0", config.elites.spawn_chance);
			config.elites.spawn_chance = 0.0;
		} else if (config.elites.spawn_chance > 1.0) {
			LOGGER.warn("elites.spawn_chance {} exceeds 1; clamping to 1", config.elites.spawn_chance);
			config.elites.spawn_chance = 1.0;
		}
		if (config.elites.particle_interval_ticks < 0) {
			LOGGER.warn("elites.particle_interval_ticks {} is negative; clamping to 0", config.elites.particle_interval_ticks);
			config.elites.particle_interval_ticks = 0;
		}
		if (config.elites.ambient_particle == null) {
			config.elites.ambient_particle = "minecraft:dust";
		}
		if (config.elites.particle_dust_scale < 0.0F) {
			LOGGER.warn("elites.particle_dust_scale {} is negative; clamping to 0", config.elites.particle_dust_scale);
			config.elites.particle_dust_scale = 0.0F;
		} else if (config.elites.particle_dust_scale > 4.0F) {
			LOGGER.warn("elites.particle_dust_scale {} exceeds 4; clamping to 4", config.elites.particle_dust_scale);
			config.elites.particle_dust_scale = 4.0F;
		}
		if (config.kill_method.lookback_ticks < 0) {
			LOGGER.warn("kill_method.lookback_ticks {} is negative; clamping to 0", config.kill_method.lookback_ticks);
			config.kill_method.lookback_ticks = 0;
		}
		if (config.feedback.elite_kill_sound == null || config.feedback.elite_kill_sound.isBlank()) {
			config.feedback.elite_kill_sound = "minecraft:block.amethyst_block.chime";
		}
	}

	private static JsonObject objectOrEmpty(JsonObject root, String key) {
		if (!root.has(key) || !root.get(key).isJsonObject()) {
			return new JsonObject();
		}
		return root.getAsJsonObject(key);
	}
}
