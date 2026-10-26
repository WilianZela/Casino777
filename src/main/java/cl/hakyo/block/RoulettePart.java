package cl.hakyo.block;

import net.minecraft.util.StringRepresentable;

public enum RoulettePart implements StringRepresentable {
	FRONT_LEFT("front_left", false, false),
	FRONT_RIGHT("front_right", true, false),
	BACK_LEFT("back_left", false, true),
	BACK_RIGHT("back_right", true, true);

	private final String name;
	private final boolean right;
	private final boolean back;

	RoulettePart(String name, boolean right, boolean back) {
		this.name = name;
		this.right = right;
		this.back = back;
	}

	public boolean right() {
		return right;
	}

	public boolean back() {
		return back;
	}

	public static RoulettePart of(boolean right, boolean back) {
		return back ? (right ? BACK_RIGHT : BACK_LEFT) : (right ? FRONT_RIGHT : FRONT_LEFT);
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
