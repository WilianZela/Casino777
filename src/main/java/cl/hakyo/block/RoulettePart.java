package cl.hakyo.block;

import net.minecraft.util.StringRepresentable;

public enum RoulettePart implements StringRepresentable {
	FRONT_LEFT("front_left", 0, false),
	FRONT_MIDDLE("front_middle", 1, false),
	FRONT_RIGHT("front_right", 2, false),
	BACK_LEFT("back_left", 0, true),
	BACK_MIDDLE("back_middle", 1, true),
	BACK_RIGHT("back_right", 2, true);

	public static final int COLUMNS = 3;

	private final String name;
	private final int column;
	private final boolean back;

	RoulettePart(String name, int column, boolean back) {
		this.name = name;
		this.column = column;
		this.back = back;
	}

	public int column() {
		return column;
	}

	public boolean back() {
		return back;
	}

	public static RoulettePart of(int column, boolean back) {
		for (RoulettePart part : values()) {
			if (part.column == column && part.back == back) {
				return part;
			}
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
