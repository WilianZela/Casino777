package cl.hakyo.client;

import cl.hakyo.entity.ChairSeatEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

public final class ChairSpinHandler {
	private static int seatId = -1;
	private static float lastYaw;

	private ChairSpinHandler() {
	}

	public static void tick(Minecraft minecraft) {
		LocalPlayer player = minecraft.player;
		if (player == null || !(player.getVehicle() instanceof ChairSeatEntity seat)) {
			seatId = -1;
			return;
		}
		float yaw = seat.getYRot();
		if (seat.getId() == seatId && seat.isSpinning()) {
			float delta = Mth.wrapDegrees(yaw - lastYaw);
			player.setYRot(player.getYRot() + delta);
			player.yRotO += delta;
			player.yBodyRot += delta;
			player.yBodyRotO += delta;
			player.yHeadRot += delta;
			player.yHeadRotO += delta;
		}
		seatId = seat.getId();
		lastYaw = yaw;
	}
}
