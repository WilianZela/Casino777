package cl.hakyo.registry;

import cl.hakyo.Modcasino;
import cl.hakyo.block.RouletteBlockEntity;
import cl.hakyo.block.SlotMachineBlockEntity;
import cl.hakyo.block.SwivelChairBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
	public static final BlockEntityType<SlotMachineBlockEntity> SLOT_MACHINE = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Modcasino.id("slot_machine"),
			BlockEntityType.Builder.of(SlotMachineBlockEntity::new, ModBlocks.SLOT_MACHINE).build(null));

	public static final BlockEntityType<SwivelChairBlockEntity> SWIVEL_CHAIR = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Modcasino.id("swivel_chair"),
			BlockEntityType.Builder.of(SwivelChairBlockEntity::new, ModBlocks.SWIVEL_CHAIR).build(null));

	public static final BlockEntityType<RouletteBlockEntity> ROULETTE = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Modcasino.id("roulette"),
			BlockEntityType.Builder.of(RouletteBlockEntity::new, ModBlocks.ROULETTE).build(null));

	private ModBlockEntities() {
	}

	public static void register() {
	}
}
