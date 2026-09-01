package mob_loot_rebalance.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.entity.monster.Creeper;

@Mixin(Creeper.class)
public interface CreeperAccessor {

	@Accessor("explosionRadius")
	int mob_loot_rebalance$getExplosionRadius();

	@Accessor("explosionRadius")
	void mob_loot_rebalance$setExplosionRadius(int radius);
}
