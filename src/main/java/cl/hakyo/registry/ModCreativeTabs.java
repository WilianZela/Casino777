package cl.hakyo.registry;

import cl.hakyo.Modcasino;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {
	public static final CreativeModeTab MAIN = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			Modcasino.id("main"),
			FabricItemGroup.builder()
					.title(Component.translatable("itemGroup." + Modcasino.MOD_ID + ".main"))
					.icon(() -> new ItemStack(ModBlocks.SLOT_MACHINE))
					.displayItems((parameters, output) -> {
						output.accept(ModBlocks.SLOT_MACHINE);
						output.accept(ModBlocks.ROULETTE);
						output.accept(ModBlocks.SWIVEL_CHAIR);
						ModItems.CHIPS.forEach(output::accept);
					})
					.build());

	public static final CreativeModeTab PLUSHIES = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			Modcasino.id("plushies"),
			FabricItemGroup.builder()
					.title(Component.translatable("itemGroup." + Modcasino.MOD_ID + ".plushies"))
					.icon(() -> new ItemStack(ModBlocks.PLUSHIE_ZZILEXX))
					.displayItems((parameters, output) -> {
						output.accept(ModBlocks.PLUSHIE_ZZILEXX);
						output.accept(ModBlocks.PLUSHIE_RENKUNSHITI);
						output.accept(ModBlocks.PLUSHIE_TV_TOXIC_VT_2);
						output.accept(ModBlocks.PLUSHIE_TV_TOXIC_VT_1);
						output.accept(ModBlocks.PLUSHIE_BLACK);
						output.accept(ModBlocks.PLUSHIE_MUSHROOM_KING);
						output.accept(ModBlocks.PLUSHIE_KAEL);
						output.accept(ModBlocks.PLUSHIE_AZAZEL);
						output.accept(ModBlocks.PLUSHIE_CRUCIFIED);
					})
					.build());

	public static final CreativeModeTab FUNKOS = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			Modcasino.id("funkos"),
			FabricItemGroup.builder()
					.title(Component.translatable("itemGroup." + Modcasino.MOD_ID + ".funkos"))
					.icon(() -> new ItemStack(ModBlocks.FUNKOS.get(0)))
					.displayItems((parameters, output) -> {
						ModBlocks.FUNKOS.forEach(output::accept);
					})
					.build());

	private ModCreativeTabs() {
	}

	public static void register() {
	}
}
