package mob_combat_rebalance.config;

/**
 * Elite spawn pillar (AC-4–8, AC-7, AC-20).
 */
public final class ElitesConfig {
	public boolean enabled = true;
	/** Fraction of eligible natural spawns that roll elite at spawn time. 0 disables elites. */
	public double spawn_chance = 0.07;
	/** Vanilla particle id. Use {@code minecraft:dust} for colored specks ({@link #particle_dust_color}). */
	public String ambient_particle = "minecraft:dust";
	/** ARGB color when {@link #ambient_particle} is {@code minecraft:dust}. Default: opaque red. */
	public int particle_dust_color = 0xFFFF0000;
	/** Size multiplier when {@link #ambient_particle} is {@code minecraft:dust}. */
	public float particle_dust_scale = 0.4F;
	/** Ticks between ambient particle spawns. 0 or negative disables particles. */
	public int particle_interval_ticks = 30;
	/** Outline glow for UAT spawn-rate checks only. Default off — use {@link #ambient_particle} instead. */
	public boolean uat_glow = false;
}
