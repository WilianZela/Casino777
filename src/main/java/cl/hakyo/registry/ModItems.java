package cl.hakyo.registry;

import cl.hakyo.Modcasino;
import cl.hakyo.item.ChipItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.List;

public final class ModItems {
	public static final ChipItem CHIP_1K = register("1k_chip", new ChipItem(new Item.Properties(), 1_000L));
	public static final ChipItem CHIP_5K = register("5k_chip", new ChipItem(new Item.Properties(), 5_000L));
	public static final ChipItem CHIP_25K = register("25k_chip", new ChipItem(new Item.Properties(), 25_000L));
	public static final ChipItem CHIP_100K = register("100k_chip", new ChipItem(new Item.Properties(), 100_000L));
	public static final ChipItem CHIP_500K = register("500k_chip", new ChipItem(new Item.Properties(), 500_000L));
	public static final ChipItem CHIP_1M = register("1m_chip", new ChipItem(new Item.Properties(), 1_000_000L));
	public static final ChipItem CHIP_5M = register("5m_chip", new ChipItem(new Item.Properties(), 5_000_000L));
	public static final ChipItem CHIP_10M = register("10m_chip", new ChipItem(new Item.Properties(), 10_000_000L));
	public static final ChipItem CHIP_50M = register("50m_chip", new ChipItem(new Item.Properties(), 50_000_000L));
	public static final ChipItem CHIP_100M = register("100m_chip", new ChipItem(new Item.Properties(), 100_000_000L));
	public static final ChipItem CHIP_500M = register("500m_chip", new ChipItem(new Item.Properties(), 500_000_000L));
	public static final ChipItem CHIP_1B = register("1b_chip", new ChipItem(new Item.Properties(), 1_000_000_000L));

	public static final List<ChipItem> CHIPS = List.of(
			CHIP_1K, CHIP_5K, CHIP_25K, CHIP_100K, CHIP_500K, CHIP_1M, CHIP_5M, CHIP_10M, CHIP_50M, CHIP_100M, CHIP_500M, CHIP_1B);

	private ModItems() {
	}

	private static <T extends Item> T register(String name, T item) {
		return Registry.register(BuiltInRegistries.ITEM, Modcasino.id(name), item);
	}

	public static void register() {
	}
}
