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

	private ModCreativeTabs() {
	}

	public static void register() {
	}
}
