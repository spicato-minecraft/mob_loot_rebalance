package mob_combat_rebalance.elite;

import net.minecraft.world.entity.Mob;

/**
 * Read/write helpers for the elite data attachment.
 */
public final class EliteState {

	private EliteState() {
	}

	public static boolean isElite(Mob mob) {
		return Boolean.TRUE.equals(mob.getAttachedOrElse(EliteAttachments.IS_ELITE, Boolean.FALSE));
	}

	public static void markElite(Mob mob) {
		mob.setAttached(EliteAttachments.IS_ELITE, Boolean.TRUE);
	}

	public static void clearElite(Mob mob) {
		mob.removeAttached(EliteAttachments.IS_ELITE);
	}
}
