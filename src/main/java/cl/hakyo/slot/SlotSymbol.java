package cl.hakyo.slot;

public enum SlotSymbol {
	SEVEN,
	CHERRY,
	LEMON,
	BAR,
	BELL,
	DIAMOND;

	public static final SlotSymbol[] DRUM = {
			SEVEN, CHERRY, LEMON, BAR, BELL, CHERRY, DIAMOND, LEMON, BELL, CHERRY, BAR, LEMON
	};
	public static final int DRUM_SIZE = DRUM.length;

	public static SlotSymbol at(int index) {
		return DRUM[Math.floorMod(index, DRUM_SIZE)];
	}
}
