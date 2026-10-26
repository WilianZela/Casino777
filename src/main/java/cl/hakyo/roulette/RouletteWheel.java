package cl.hakyo.roulette;

import java.util.Set;

public final class RouletteWheel {
	public static final int[] ORDER = {
			0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26
	};
	public static final int POCKETS = ORDER.length;

	private static final Set<Integer> RED = Set.of(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36);

	private RouletteWheel() {
	}

	public static boolean isRed(int number) {
		return RED.contains(number);
	}

	public static int number(int pocket) {
		return ORDER[Math.floorMod(pocket, POCKETS)];
	}

	public static float pocketAngle(int pocket) {
		return Math.floorMod(pocket, POCKETS) * 360.0F / POCKETS;
	}

	public static String colorKey(int number) {
		return number == 0 ? "green" : isRed(number) ? "red" : "black";
	}
}
