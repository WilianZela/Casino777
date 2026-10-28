package cl.hakyo.roulette;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class RouletteBet {
	public static final float TABLE_WIDTH = 48.0F;
	public static final float TABLE_DEPTH = 32.0F;

	private static final float ZERO_X0 = 2.6F;
	private static final float NUM_X0 = 5.9F;
	private static final float CELL_W = 3.1F;
	private static final float TAIL_X0 = 43.1F;
	private static final float TAIL_X1 = 45.4F;
	private static final float GRID_Y0 = 16.5F;
	private static final float ROW_H = 2.7F;
	private static final float DOZEN_Y0 = 24.6F;
	private static final float DOZEN_H = 2.4F;
	private static final float EVEN_Y0 = 27.0F;
	private static final float EVEN_H = 2.4F;

	private static final int DOZEN_BASE = 37;
	private static final int COLUMN_BASE = 40;
	private static final int OUTSIDE_BASE = 43;
	private static final String[] OUTSIDE_KEYS = {"low", "even", "red", "black", "odd", "high"};

	private static final List<RouletteBet> ALL = new ArrayList<>();

	static {
		for (int number = 0; number <= 36; number++) {
			if (number == 0) {
				ALL.add(new RouletteBet(0, ZERO_X0, GRID_Y0, NUM_X0, GRID_Y0 + 3 * ROW_H));
			} else {
				int column = (number - 1) / 3;
				int row = 2 - (number - 1) % 3;
				float x = NUM_X0 + column * CELL_W;
				float y = GRID_Y0 + row * ROW_H;
				ALL.add(new RouletteBet(number, x, y, x + CELL_W, y + ROW_H));
			}
		}
		for (int dozen = 0; dozen < 3; dozen++) {
			float x = NUM_X0 + dozen * 4 * CELL_W;
			ALL.add(new RouletteBet(DOZEN_BASE + dozen, x, DOZEN_Y0, x + 4 * CELL_W, DOZEN_Y0 + DOZEN_H));
		}
		for (int column = 0; column < 3; column++) {
			float y = GRID_Y0 + (2 - column) * ROW_H;
			ALL.add(new RouletteBet(COLUMN_BASE + column, TAIL_X0, y, TAIL_X1, y + ROW_H));
		}
		for (int index = 0; index < OUTSIDE_KEYS.length; index++) {
			float x = NUM_X0 + index * 2 * CELL_W;
			ALL.add(new RouletteBet(OUTSIDE_BASE + index, x, EVEN_Y0, x + 2 * CELL_W, EVEN_Y0 + EVEN_H));
		}
	}

	public static final int COUNT = ALL.size();

	private final int id;
	private final float x0;
	private final float y0;
	private final float x1;
	private final float y1;

	private RouletteBet(int id, float x0, float y0, float x1, float y1) {
		this.id = id;
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}

	public int id() {
		return id;
	}

	public float centerX() {
		return (x0 + x1) / 2.0F;
	}

	public float centerY() {
		return (y0 + y1) / 2.0F;
	}

	public int payoutFor(int number) {
		if (id <= 36) {
			return number == id ? 36 : 0;
		}
		if (id < COLUMN_BASE) {
			return number >= 1 && (number - 1) / 12 == id - DOZEN_BASE ? 3 : 0;
		}
		if (id < OUTSIDE_BASE) {
			return number >= 1 && number % 3 == (id - COLUMN_BASE + 1) % 3 ? 3 : 0;
		}
		boolean win = switch (OUTSIDE_KEYS[id - OUTSIDE_BASE]) {
			case "low" -> number >= 1 && number <= 18;
			case "high" -> number >= 19;
			case "even" -> number >= 1 && number % 2 == 0;
			case "odd" -> number % 2 == 1;
			case "red" -> RouletteWheel.isRed(number);
			default -> number != 0 && !RouletteWheel.isRed(number);
		};
		return win ? 2 : 0;
	}

	public Component label() {
		if (id <= 36) {
			return Component.literal(Integer.toString(id));
		}
		if (id < COLUMN_BASE) {
			return Component.translatable("message.modcasino.roulette_dozen_" + (id - DOZEN_BASE + 1));
		}
		if (id < OUTSIDE_BASE) {
			return Component.translatable("message.modcasino.roulette_column_" + (id - COLUMN_BASE + 1));
		}
		return Component.translatable("message.modcasino.roulette_" + OUTSIDE_KEYS[id - OUTSIDE_BASE]);
	}

	public static RouletteBet byId(int id) {
		return ALL.get(Math.floorMod(id, COUNT));
	}

	public static RouletteBet at(double x, double y) {
		for (RouletteBet bet : ALL) {
			if (x >= bet.x0 && x < bet.x1 && y >= bet.y0 && y < bet.y1) {
				return bet;
			}
		}
		return null;
	}
}
