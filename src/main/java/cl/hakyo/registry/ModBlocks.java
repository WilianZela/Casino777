package cl.hakyo.registry;

import cl.hakyo.Modcasino;
import cl.hakyo.block.RouletteBlock;
import cl.hakyo.block.SlotMachineBlock;
import cl.hakyo.block.SwivelChairBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
	public static final Block SLOT_MACHINE = registerWithItem("slot_machine", new SlotMachineBlock(BlockBehaviour.Properties.of()
			.strength(3.0F, 6.0F)
			.sound(SoundType.METAL)
			.lightLevel(state -> 6)
			.noOcclusion()));

	public static final Block SWIVEL_CHAIR = registerWithItem("swivel_chair", new SwivelChairBlock(BlockBehaviour.Properties.of()
			.strength(1.5F, 3.0F)
			.sound(SoundType.METAL)
			.noOcclusion()));

	public static final Block ROULETTE = registerWithItem("roulette", new RouletteBlock(BlockBehaviour.Properties.of()
			.strength(2.0F, 4.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));

	private ModBlocks() {
	}

	private static Block registerWithItem(String name, Block block) {
		Registry.register(BuiltInRegistries.BLOCK, Modcasino.id(name), block);
		Registry.register(BuiltInRegistries.ITEM, Modcasino.id(name), new BlockItem(block, new Item.Properties()));
		return block;
	}

	public static void register() {
	}
}
