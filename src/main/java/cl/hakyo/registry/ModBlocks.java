package cl.hakyo.registry;

import cl.hakyo.Modcasino;
import cl.hakyo.block.CrossPlushBlock;
import cl.hakyo.block.FunkoBlock;
import cl.hakyo.block.PlushBlock;
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

import java.util.ArrayList;
import java.util.List;

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

	public static final Block PLUSHIE_ZZILEXX = registerWithItem("plushie_zzilexx", new PlushBlock(BlockBehaviour.Properties.of()
			.strength(0.8F)
			.sound(SoundType.WOOL)
			.noOcclusion()));

	public static final Block PLUSHIE_RENKUNSHITI = registerWithItem("plushie_renkunshiti", new PlushBlock(BlockBehaviour.Properties.of()
			.strength(0.8F)
			.sound(SoundType.WOOL)
			.noOcclusion()));

	public static final Block PLUSHIE_TV_TOXIC_VT_2 = registerWithItem("plushie_tv_toxic_vt_2", new PlushBlock(BlockBehaviour.Properties.of()
			.strength(0.8F)
			.sound(SoundType.WOOL)
			.noOcclusion()));

	public static final Block PLUSHIE_TV_TOXIC_VT_1 = registerWithItem("plushie_tv_toxic_vt_1", new PlushBlock(BlockBehaviour.Properties.of()
			.strength(0.8F)
			.sound(SoundType.WOOL)
			.noOcclusion()));

	public static final Block PLUSHIE_BLACK = registerWithItem("plushie_black", new PlushBlock(BlockBehaviour.Properties.of()
			.strength(0.8F)
			.sound(SoundType.WOOL)
			.noOcclusion()));

	public static final Block PLUSHIE_MUSHROOM_KING = registerWithItem("plushie_mushroom_king", new PlushBlock(BlockBehaviour.Properties.of()
			.strength(0.8F)
			.sound(SoundType.WOOL)
			.noOcclusion()));

	public static final Block PLUSHIE_KAEL = registerWithItem("plushie_kael", new PlushBlock(BlockBehaviour.Properties.of()
			.strength(0.8F)
			.sound(SoundType.WOOL)
			.noOcclusion()));

	public static final Block PLUSHIE_AZAZEL = registerWithItem("plushie_azazel", new PlushBlock(BlockBehaviour.Properties.of()
			.strength(0.8F)
			.sound(SoundType.WOOL)
			.noOcclusion()));

	public static final Block PLUSHIE_CRUCIFIED = registerWithItem("plushie_crucified", new CrossPlushBlock(BlockBehaviour.Properties.of()
			.strength(0.8F)
			.sound(SoundType.WOOL)
			.noOcclusion()));

	public static final List<Block> FUNKOS = registerFunkos("zzilexx", "renkunshiti", "tv_toxic_vt_2", "tv_toxic_vt_1", "black", "mushroom_king", "kael", "azazel");

	private ModBlocks() {
	}

	private static Block registerWithItem(String name, Block block) {
		Registry.register(BuiltInRegistries.BLOCK, Modcasino.id(name), block);
		Registry.register(BuiltInRegistries.ITEM, Modcasino.id(name), new BlockItem(block, new Item.Properties()));
		return block;
	}

	private static List<Block> registerFunkos(String... names) {
		List<Block> blocks = new ArrayList<>();
		for (String name : names) {
			blocks.add(registerWithItem("funko_" + name, new FunkoBlock(BlockBehaviour.Properties.of()
					.strength(0.8F)
					.sound(SoundType.STONE)
					.noOcclusion())));
		}
		return List.copyOf(blocks);
	}

	public static void register() {
	}
}
