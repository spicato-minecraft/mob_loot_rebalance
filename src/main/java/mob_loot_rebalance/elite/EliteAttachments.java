package mob_loot_rebalance.elite;

import com.mojang.serialization.Codec;
import mob_loot_rebalance.Mob_loot_rebalance;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

/**
 * Fabric Data Attachment for elite mob flag (AC-4).
 * Persisted on entity save/load; synced to clients for visuals.
 */
public final class EliteAttachments {

	public static final AttachmentType<Boolean> IS_ELITE = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(Mob_loot_rebalance.MOD_ID, "elite"),
			builder -> builder
					.initializer(() -> Boolean.FALSE)
					.persistent(Codec.BOOL)
					.syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
	);

	private EliteAttachments() {
	}
}
