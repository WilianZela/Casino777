package cl.hakyo.roulette;

public enum RouletteBet {
	RED(2),
	GREEN(38),
	BLACK(2);

	private final int payout;

	RouletteBet(int payout) {
		this.payout = payout;
	}

	public int payoutFor(int number) {
		boolean win = switch (this) {
			case RED -> RouletteWheel.isRed(number);
			case GREEN -> number == 0;
			case BLACK -> number != 0 && !RouletteWheel.isRed(number);
		};
		return win ? payout : 0;
	}

	public String key() {
		return name().toLowerCase();
	}

	public static RouletteBet byOrdinal(int ordinal) {
		RouletteBet[] values = values();
		return values[Math.floorMod(ordinal, values.length)];
	}
}
