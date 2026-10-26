package cl.hakyo.registry;

import cl.hakyo.Modcasino;
import cl.hakyo.entity.ChairSeatEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
	public static final EntityType<ChairSeatEntity> CHAIR_SEAT = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			Modcasino.id("chair_seat"),
			EntityType.Builder.<ChairSeatEntity>of(ChairSeatEntity::new, MobCategory.MISC)
					.sized(0.1F, 0.1F)
					.clientTrackingRange(10)
					.updateInterval(1)
					.noSave()
					.fireImmune()
					.noSummon()
					.build("chair_seat"));

	private ModEntities() {
	}

	public static void register() {
	}
}
