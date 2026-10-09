package mob_loot_rebalance.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mob_loot_rebalance.Mob_loot_rebalance;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Server-side config at {@code config/mob_loot_rebalance.json}.
 * Defaults are generated on first run; user file merges on top for missing keys.
 */
public final class ModConfig {
	private static final Logger LOGGER = LogManager.getLogger(Mob_loot_rebalance.MOD_ID + ".config");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FILE_NAME = Mob_loot_rebalance.MOD_ID + ".json";
	private static volatile ModConfig instance;
	private static volatile long loadedFileMtime = Long.MIN_VALUE;
	private static volatile boolean skipDiskReload;

	public XpConfig xp = new XpConfig();
	public ElitesConfig elites = new ElitesConfig();
	public EliteLootConfig elite_loot = new EliteLootConfig();
	public KillMethodConfig kill_method = new KillMethodConfig();
	public CombatPotionConfig combat_potion = new CombatPotionConfig();
	public FeedbackConfig feedback = new FeedbackConfig();

	public static ModConfig get() {
		if (skipDiskReload) {
			return instance;
		}
		Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
		long mtime = fileMtime(path);
		ModConfig current = instance;
		if (current == null || mtime != loadedFileMtime) {
			current = load();
			instance = current;
		}
		return current;
	}

	public static void set(ModConfig config) {
		instance = config;
		skipDiskReload = false;
	}

	/** Test-only override; does not write to disk. */
	public static void setForTest(ModConfig config) {
		instance = config;
		skipDiskReload = true;
	}

	/**
	 * Force a re-read from {@code config/mob_loot_rebalance.json}. Ignores mtime so
	 * Apply can refresh even when the filesystem clock does not tick.
	 */
	public static ModConfig reload() {
		skipDiskReload = false;
		instance = load();
		return instance;
	}

	/**
	 * Force a re-read from {@code configDir/mob_loot_rebalance.json} into the
	 * process singleton. {@link #get()} will not replace it from the game config
	 * dir until {@link #reload()} or {@link #set}.
	 */
	public static ModConfig reloadFrom(Path configDir) {
		skipDiskReload = true;
		instance = loadFrom(configDir.resolve(FILE_NAME));
		return instance;
	}

	public static ModConfig load() {
		return loadFrom(FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME));
	}

	static ModConfig loadFrom(Path path) {
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
						config.saveTo(path);
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
				config.saveTo(path);
			} catch (IOException e) {
				LOGGER.warn("Could not write default config to {}", path, e);
			}
		}
		loadedFileMtime = fileMtime(path);
		return config;
	}

	private static long fileMtime(Path path) {
		try {
			if (Files.isRegularFile(path)) {
				return Files.getLastModifiedTime(path).toMillis();
			}
		} catch (IOException ignored) {
		}
		return 0L;
	}

	public static ModConfig defaults() {
		return new ModConfig();
	}

	public boolean isXpPillarActive() {
		return xp.enabled;
	}

	public boolean isElitesPillarActive() {
		return elites.enabled
				&& (elites.spawn_chance > 0.0 || elites.spawner_trial_spawn_chance > 0.0);
	}

	public boolean isEliteLootPillarActive() {
		return elite_loot.enabled;
	}

	public void save() {
		saveTo(FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME));
	}

	public void saveTo(Path path) {
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(this, writer);
			}
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
				config.elites.spawn_chance = 0.14;
				addedKeys = true;
			}
			if (!elites.has("spawner_trial_spawn_chance")) {
				config.elites.spawner_trial_spawn_chance = 0.02;
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
				config.elites.particle_dust_color = 0xFFE8EEF8;
				addedKeys = true;
			}
			if (!elites.has("particle_dust_scale")) {
				config.elites.particle_dust_scale = 0.5F;
				addedKeys = true;
			}
			if (!elites.has("particle_interval_ticks")) {
				config.elites.particle_interval_ticks = 60;
				addedKeys = true;
			}
		}

		if (!root.has("elite_loot")) {
			config.elite_loot = new EliteLootConfig();
			addedKeys = true;
		} else {
			if (!eliteLoot.has("enabled")) {
				config.elite_loot.enabled = true;
				addedKeys = true;
			}
			if (!eliteLoot.has("consolation_weight")) {
				config.elite_loot.consolation_weight = 15;
				addedKeys = true;
			}
			if (!eliteLoot.has("books_weight")) {
				config.elite_loot.books_weight = 23;
				addedKeys = true;
			}
			if (!eliteLoot.has("potions_weight")) {
				config.elite_loot.potions_weight = 40;
				addedKeys = true;
			}
			if (!eliteLoot.has("unique_weight")) {
				config.elite_loot.unique_weight = 22;
				addedKeys = true;
			}
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
		if (config.elites.spawner_trial_spawn_chance < 0.0) {
			LOGGER.warn("elites.spawner_trial_spawn_chance {} is negative; clamping to 0", config.elites.spawner_trial_spawn_chance);
			config.elites.spawner_trial_spawn_chance = 0.0;
		} else if (config.elites.spawner_trial_spawn_chance > 1.0) {
			LOGGER.warn("elites.spawner_trial_spawn_chance {} exceeds 1; clamping to 1", config.elites.spawner_trial_spawn_chance);
			config.elites.spawner_trial_spawn_chance = 1.0;
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
		config.elite_loot.consolation_weight = clampWeight(config.elite_loot.consolation_weight, "elite_loot.consolation_weight");
		config.elite_loot.books_weight = clampWeight(config.elite_loot.books_weight, "elite_loot.books_weight");
		config.elite_loot.potions_weight = clampWeight(config.elite_loot.potions_weight, "elite_loot.potions_weight");
		config.elite_loot.unique_weight = clampWeight(config.elite_loot.unique_weight, "elite_loot.unique_weight");
	}

	private static int clampWeight(int value, String key) {
		if (value < 0) {
			LOGGER.warn("{} {} is negative; clamping to 0", key, value);
			return 0;
		}
		if (value > 100) {
			LOGGER.warn("{} {} exceeds 100; clamping to 100", key, value);
			return 100;
		}
		return value;
	}

	private static JsonObject objectOrEmpty(JsonObject root, String key) {
		if (!root.has(key) || !root.get(key).isJsonObject()) {
			return new JsonObject();
		}
		return root.getAsJsonObject(key);
	}
}
