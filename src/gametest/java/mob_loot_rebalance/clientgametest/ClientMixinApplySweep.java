package mob_loot_rebalance.clientgametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

/**
 * Loads MobMixinClient's target on the client thread. A renamed Mob class fails as soon as it is initialized.
 */
public class ClientMixinApplySweep implements FabricClientGameTest {

	@Override
	public void runTest(ClientGameTestContext context) {
		context.computeOnClient(client -> {
			try {
				Class.forName("net.minecraft.world.entity.Mob");
			} catch (ClassNotFoundException e) {
				throw new AssertionError("Mixin target failed to load: net.minecraft.world.entity.Mob", e);
			}
			return null;
		});
	}
}
