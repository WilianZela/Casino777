package cl.hakyo.slot;

import cl.hakyo.Modcasino;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class SlotPayouts {
	public static final TagKey<Item> CHIPS = TagKey.create(Registries.ITEM, Modcasino.id("chips"));
	public static final int BET = 1;
	public static final int JACKPOT = 100;

	private SlotPayouts() {
	}

	public static boolean isChip(ItemStack stack) {
		return stack.is(CHIPS);
	}

	public static int payout(int[] drumIndices) {
		SlotSymbol first = SlotSymbol.at(drumIndices[0]);
		SlotSymbol second = SlotSymbol.at(drumIndices[1]);
		SlotSymbol third = SlotSymbol.at(drumIndices[2]);
		if (first == second && second == third) {
			return switch (first) {
				case SEVEN -> JACKPOT;
				case DIAMOND -> 40;
				case BAR -> 12;
				case BELL -> 8;
				case LEMON -> 5;
				case CHERRY -> 5;
			};
		}
		int cherries = 0;
		for (SlotSymbol symbol : new SlotSymbol[] {first, second, third}) {
			if (symbol == SlotSymbol.CHERRY) {
				cherries++;
			}
		}
		return switch (cherries) {
			case 1 -> 1;
			case 2 -> 2;
			default -> 0;
		};
	}

	public static boolean isJackpot(int[] drumIndices) {
		return SlotSymbol.at(drumIndices[0]) == SlotSymbol.SEVEN
				&& SlotSymbol.at(drumIndices[1]) == SlotSymbol.SEVEN
				&& SlotSymbol.at(drumIndices[2]) == SlotSymbol.SEVEN;
	}
}
