package cl.hakyo.client.render;

import cl.hakyo.Modcasino;
import cl.hakyo.block.SlotMachineBlock;
import cl.hakyo.block.SlotMachineBlockEntity;
import cl.hakyo.slot.SlotSymbol;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SlotMachineRenderer implements BlockEntityRenderer<SlotMachineBlockEntity> {
	private static final ResourceLocation REELS = Modcasino.id("textures/entity/slot_reels.png");
	private static final ResourceLocation GLOW = Modcasino.id("textures/entity/slot_glow.png");
	private static final ResourceLocation NEON_A = Modcasino.id("textures/entity/slot_neon_a.png");
	private static final ResourceLocation NEON_B = Modcasino.id("textures/entity/slot_neon_b.png");
	private static final ResourceLocation TUBE = Modcasino.id("textures/block/sm_neon.png");
	private static final ResourceLocation BLACK = Modcasino.id("textures/block/sm_black.png");

	private static final float[] REEL_CENTER_X = {5.4F, 8.0F, 10.6F};
	private static final float REEL_HALF_WIDTH = 1.2F;
	private static final float REEL_CENTER_Y = 7.0F;
	private static final float REEL_CENTER_Z = 6.4F;
	private static final float REEL_RADIUS = 4.0F;
	private static final float FACE_STEP = 360.0F / SlotSymbol.DRUM_SIZE;
	private static final float LEVER_PULL_ANGLE = -75.0F;
	private static final int BULB_COLUMNS = 15;

	public SlotMachineRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(SlotMachineBlockEntity machine, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
		Level level = machine.getLevel();
		BlockState state = machine.getBlockState();
		if (level == null || !state.hasProperty(SlotMachineBlock.FACING)) {
			return;
		}
		Direction facing = state.getValue(SlotMachineBlock.FACING);
		float time = level.getGameTime() + partialTick;
		float elapsed = machine.spinning() ? time - machine.startTime() : -1.0F;

		pose.pushPose();
		pose.translate(0.5D, 0.0D, 0.5D);
		pose.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
		pose.translate(-0.5D, 0.0D, -0.5D);

		int reelLight = LightTexture.pack(Math.max(LightTexture.block(light), 11), LightTexture.sky(light));
		VertexConsumer reels = buffers.getBuffer(RenderType.entityCutoutNoCull(REELS));
		for (int reel = 0; reel < 3; reel++) {
			float angle = reelAngle(machine, reel, elapsed);
			renderReel(reels, pose.last(), REEL_CENTER_X[reel], angle, reelLight);
		}

		renderLever(buffers, pose, leverAngle(elapsed), light);
		renderNeon(machine, buffers, pose, time);
		pose.popPose();
	}

	private static float reelAngle(SlotMachineBlockEntity machine, int reel, float elapsed) {
		float target = -machine.symbol(reel) * FACE_STEP;
		if (elapsed < 0.0F) {
			return target;
		}
		float previous = -machine.previous(reel) * FACE_STEP;
		float start = SlotMachineBlockEntity.STOP_TICKS[reel];
		float ease = SlotMachineBlockEntity.EASE_TICKS;
		float total = 360.0F * SlotMachineBlockEntity.TURNS[reel] + Math.floorMod((int) Math.round(previous - target), 360);
		float speed = total / (start - ease / 2.0F);
		float t = elapsed - SlotMachineBlockEntity.PULL_TICKS;
		if (t <= 0.0F) {
			return previous;
		}
		float remaining = start - t;
		if (remaining <= 0.0F) {
			return target;
		}
		float rest = remaining >= ease ? speed * (remaining - ease / 2.0F) : speed * remaining * remaining / (2.0F * ease);
		return target + rest;
	}

	private static float leverAngle(float elapsed) {
		if (elapsed < 0.0F || elapsed >= SlotMachineBlockEntity.PULL_TICKS) {
			return 0.0F;
		}
		float half = SlotMachineBlockEntity.PULL_TICKS / 2.0F;
		float progress = elapsed < half ? elapsed / half : 1.0F - (elapsed - half) / half;
		float eased = progress * progress * (3.0F - 2.0F * progress);
		return LEVER_PULL_ANGLE * eased;
	}

	private static void renderReel(VertexConsumer consumer, PoseStack.Pose pose, float centerX, float angle, int light) {
		float x1 = (centerX - REEL_HALF_WIDTH) / 16.0F;
		float x2 = (centerX + REEL_HALF_WIDTH) / 16.0F;
		float cy = REEL_CENTER_Y / 16.0F;
		float cz = REEL_CENTER_Z / 16.0F;
		float radius = REEL_RADIUS / 16.0F;
		float cellHeight = 1.0F / SlotSymbol.DRUM_SIZE;
		for (int face = 0; face < SlotSymbol.DRUM_SIZE; face++) {
			float center = face * FACE_STEP + angle;
			float low = (float) Math.toRadians(center - FACE_STEP / 2.0F);
			float high = (float) Math.toRadians(center + FACE_STEP / 2.0F);
			float mid = (float) Math.toRadians(center);
			float facing = (float) Math.cos(mid);
			if (facing < -0.2F) {
				continue;
			}
			int shade = Math.round(255.0F * (0.5F + 0.5F * Math.max(0.0F, facing)));
			float nx = 0.0F;
			float ny = (float) Math.sin(mid);
			float nz = -facing;
			float yLow = cy + radius * (float) Math.sin(low);
			float zLow = cz - radius * (float) Math.cos(low);
			float yHigh = cy + radius * (float) Math.sin(high);
			float zHigh = cz - radius * (float) Math.cos(high);
			float v1 = face * cellHeight;
			float v2 = v1 + cellHeight;
			vertex(consumer, pose, shade, light, nx, ny, nz, x1, yLow, zLow, 1.0F, v2);
			vertex(consumer, pose, shade, light, nx, ny, nz, x2, yLow, zLow, 0.0F, v2);
			vertex(consumer, pose, shade, light, nx, ny, nz, x2, yHigh, zHigh, 0.0F, v1);
			vertex(consumer, pose, shade, light, nx, ny, nz, x1, yHigh, zHigh, 1.0F, v1);
		}
	}

	private static void renderLever(MultiBufferSource buffers, PoseStack pose, float angle, int light) {
		pose.pushPose();
		pose.translate(0.5D / 16.0D, 7.5D / 16.0D, 6.5D / 16.0D);
		pose.mulPose(Axis.XP.rotationDegrees(angle));
		box(buffers.getBuffer(RenderType.entityCutoutNoCull(BLACK)), pose.last(), -0.4F, 0.0F, -0.4F, 0.4F, 5.0F, 0.4F, 255, light);
		box(buffers.getBuffer(RenderType.entityCutoutNoCull(BLACK)), pose.last(), -0.9F, -0.9F, -0.9F, 0.9F, 0.9F, 0.9F, 255, light);
		box(buffers.getBuffer(RenderType.entityCutoutNoCull(BLACK)), pose.last(), -1.1F, 5.0F, -1.1F, 1.1F, 7.2F, 1.1F, 255, light);
		pose.popPose();
	}

	private static void renderNeon(SlotMachineBlockEntity machine, MultiBufferSource buffers, PoseStack pose, float time) {
		Level level = machine.getLevel();
		boolean celebrating = level != null && level.getGameTime() < machine.celebrationUntil();
		boolean spinning = machine.spinning();
		float speed = celebrating ? 0.35F : spinning ? 0.2F : 0.05F;
		float pulse = 0.65F + 0.35F * (float) Math.sin(time * speed);
		float hue = celebrating ? (time * 0.02F) % 1.0F : 0.0F;
		int[] cyan = celebrating ? rainbow(hue) : new int[] {0, 229, 255};
		int[] magenta = celebrating ? rainbow(hue + 0.5F) : new int[] {255, 43, 214};

		VertexConsumer tube = buffers.getBuffer(RenderType.entityCutoutNoCull(TUBE));
		float t = 0.3F;
		float z = 1.79F;
		strip(tube, pose.last(), cyan, pulse, 3.3F, 3.8F - t, 12.7F, 3.8F, z);
		strip(tube, pose.last(), cyan, pulse, 3.3F, 10.2F, 12.7F, 10.2F + t, z);
		strip(tube, pose.last(), magenta, pulse, 3.3F - t, 3.8F - t, 3.3F, 10.2F + t, z);
		strip(tube, pose.last(), magenta, pulse, 12.7F, 3.8F - t, 12.7F + t, 10.2F + t, z);

		VertexConsumer glow = buffers.getBuffer(RenderType.entityTranslucent(GLOW));
		for (int column = 0; column < BULB_COLUMNS; column++) {
			for (int row = 0; row < 2; row++) {
				boolean lit;
				if (celebrating) {
					lit = ((int) (time / 3.0F) + column + row) % 2 == 0;
				} else if (spinning) {
					lit = ((int) (time / 2.0F) + column * (row == 0 ? 1 : -1)) % 3 == 0;
				} else {
					lit = (column * 7 + row * 3 + (int) (time / 12.0F)) % 4 == 0;
				}
				if (!lit) {
					continue;
				}
				float x = 2.0F + (15.5F + column * 16.0F) / 256.0F * 12.0F;
				float y = row == 0 ? 16.0F - 10.5F / 128.0F * 6.0F : 16.0F - 118.5F / 128.0F * 6.0F;
				int[] color = row == 0 ? magenta : cyan;
				quadZ(glow, pose.last(), color, 255, x - 0.6F, y - 0.6F, x + 0.6F, y + 0.6F, 1.96F);
			}
		}

		int alpha = Math.round(255.0F * pulse);
		sideGlow(buffers.getBuffer(RenderType.entityTranslucent(NEON_A)), pose.last(), cyan, alpha);
		sideGlow(buffers.getBuffer(RenderType.entityTranslucent(NEON_B)), pose.last(), magenta, Math.round(255.0F * Math.min(1.0F, 1.3F - pulse)));
	}

	private static int[] rainbow(float hue) {
		int rgb = net.minecraft.util.Mth.hsvToRgb(hue - (float) Math.floor(hue), 0.9F, 1.0F);
		return new int[] {(rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255};
	}

	private static void strip(VertexConsumer consumer, PoseStack.Pose pose, int[] color, float pulse, float x1, float y1, float x2, float y2, float z) {
		float k = 0.7F + 0.3F * pulse;
		int[] c = {Math.round(color[0] * k), Math.round(color[1] * k), Math.round(color[2] * k)};
		quadZ(consumer, pose, c, 255, x1, y1, x2, y2, z);
	}

	private static void quadZ(VertexConsumer consumer, PoseStack.Pose pose, int[] c, int alpha, float x1, float y1, float x2, float y2, float z) {
		vertexGlow(consumer, pose, c, alpha, x1, y1, z, 0.0F, 1.0F, 0.0F, 0.0F, -1.0F);
		vertexGlow(consumer, pose, c, alpha, x2, y1, z, 1.0F, 1.0F, 0.0F, 0.0F, -1.0F);
		vertexGlow(consumer, pose, c, alpha, x2, y2, z, 1.0F, 0.0F, 0.0F, 0.0F, -1.0F);
		vertexGlow(consumer, pose, c, alpha, x1, y2, z, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F);
	}

	private static void sideGlow(VertexConsumer consumer, PoseStack.Pose pose, int[] c, int alpha) {
		sideQuad(consumer, pose, c, alpha, 1.98F, -14.0F, 0.0F, 0.875F, -1.0F);
		sideQuad(consumer, pose, c, alpha, 1.98F, 0.0F, 16.0F, 1.0F, -1.0F);
		sideQuad(consumer, pose, c, alpha, 14.02F, -14.0F, 0.0F, 0.875F, 1.0F);
		sideQuad(consumer, pose, c, alpha, 14.02F, 0.0F, 16.0F, 1.0F, 1.0F);
	}

	private static void sideQuad(VertexConsumer consumer, PoseStack.Pose pose, int[] c, int alpha, float x, float yLow, float yHigh, float vLow, float nx) {
		float vHigh = vLow - (yHigh - yLow) / 16.0F;
		float zLow = nx < 0 ? 2.0F : 14.0F;
		float zHigh = nx < 0 ? 14.0F : 2.0F;
		float uLow = 0.125F;
		float uHigh = 0.875F;
		vertexGlow(consumer, pose, c, alpha, x, yLow, zLow, uLow, vLow, nx, 0.0F, 0.0F);
		vertexGlow(consumer, pose, c, alpha, x, yLow, zHigh, uHigh, vLow, nx, 0.0F, 0.0F);
		vertexGlow(consumer, pose, c, alpha, x, yHigh, zHigh, uHigh, vHigh, nx, 0.0F, 0.0F);
		vertexGlow(consumer, pose, c, alpha, x, yHigh, zLow, uLow, vHigh, nx, 0.0F, 0.0F);
	}

	private static void vertexGlow(VertexConsumer consumer, PoseStack.Pose pose, int[] c, int alpha, float x, float y, float z, float u, float v, float nx, float ny, float nz) {
		consumer.addVertex(pose, x / 16.0F, y / 16.0F, z / 16.0F)
				.setColor(c[0], c[1], c[2], alpha)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(LightTexture.FULL_BRIGHT)
				.setNormal(pose, nx, ny, nz);
	}

	private static void box(VertexConsumer consumer, PoseStack.Pose pose, float ux1, float uy1, float uz1, float ux2, float uy2, float uz2, int shade, int light) {
		float x1 = ux1 / 16.0F;
		float y1 = uy1 / 16.0F;
		float z1 = uz1 / 16.0F;
		float x2 = ux2 / 16.0F;
		float y2 = uy2 / 16.0F;
		float z2 = uz2 / 16.0F;
		quad(consumer, pose, shade, light, 0, -1, 0, x1, y1, z2, x1, y1, z1, x2, y1, z1, x2, y1, z2);
		quad(consumer, pose, shade, light, 0, 1, 0, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1);
		quad(consumer, pose, shade, light, 0, 0, -1, x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1);
		quad(consumer, pose, shade, light, 0, 0, 1, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
		quad(consumer, pose, shade, light, -1, 0, 0, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1);
		quad(consumer, pose, shade, light, 1, 0, 0, x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2);
	}

	private static void quad(VertexConsumer consumer, PoseStack.Pose pose, int shade, int light, float nx, float ny, float nz,
			float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz) {
		vertex(consumer, pose, shade, light, nx, ny, nz, ax, ay, az, 0.0F, 1.0F);
		vertex(consumer, pose, shade, light, nx, ny, nz, bx, by, bz, 1.0F, 1.0F);
		vertex(consumer, pose, shade, light, nx, ny, nz, cx, cy, cz, 1.0F, 0.0F);
		vertex(consumer, pose, shade, light, nx, ny, nz, dx, dy, dz, 0.0F, 0.0F);
	}

	private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int shade, int light, float nx, float ny, float nz, float x, float y, float z, float u, float v) {
		consumer.addVertex(pose, x, y, z)
				.setColor(shade, shade, shade, 255)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(light)
				.setNormal(pose, nx, ny, nz);
	}
}
