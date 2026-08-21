package mob_combat_rebalance.killmethod;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Rolling player-damage log for a single {@link net.minecraft.world.entity.LivingEntity}.
 */
public final class PlayerDamageHistory {
	private final List<PlayerDamageRecord> records = new ArrayList<>();

	public void record(long tick, KillMethod method, int retentionTicks) {
		records.add(new PlayerDamageRecord(tick, method));
		pruneOlderThan(tick, retentionTicks);
	}

	public Optional<KillMethod> mostRecentInWindow(long currentTick, int lookbackTicks) {
		if (lookbackTicks <= 0 || records.isEmpty()) {
			return Optional.empty();
		}
		long cutoff = currentTick - lookbackTicks;
		@Nullable KillMethod latest = null;
		long latestTick = Long.MIN_VALUE;
		for (PlayerDamageRecord record : records) {
			if (record.tick() >= cutoff && record.tick() >= latestTick) {
				latestTick = record.tick();
				latest = record.method();
			}
		}
		return Optional.ofNullable(latest);
	}

	public void pruneOlderThan(long currentTick, int lookbackTicks) {
		if (records.isEmpty()) {
			return;
		}
		long cutoff = currentTick - lookbackTicks;
		records.removeIf(record -> record.tick() < cutoff);
	}

	public List<PlayerDamageRecord> snapshot() {
		return List.copyOf(records);
	}

	public void clear() {
		records.clear();
	}
}
