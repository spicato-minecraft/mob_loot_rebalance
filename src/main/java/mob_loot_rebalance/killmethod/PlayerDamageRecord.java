package mob_loot_rebalance.killmethod;

/**
 * One player-attributed damage event stored on a victim for kill-method lookback.
 */
public record PlayerDamageRecord(long tick, KillMethod method) {
}
