package cl.hakyo.client.render;

import cl.hakyo.Modcasino;
import cl.hakyo.block.RouletteBlock;
import cl.hakyo.block.RouletteBlockEntity;
import cl.hakyo.roulette.RouletteBet;
import cl.hakyo.roulette.RouletteWheel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RouletteRenderer implements BlockEntityRenderer<RouletteBlockEntity> {
	private static final ResourceLocation WHEEL = Modcasino.id("textures/entity/roulette_wheel.png");
	private static final ResourceLocation GOLD = Modcasino.id("textures/block/roulette_gold.png");
	private static final ResourceLocation CHROME = Modcasino.id("textures/block/roulette_ball.png");

	private static final float CENTER_X = -8.0F;
	private static final float CENTER_Z = 23.3F;
	private static final float WHEEL_Y = 12.42F;
	private static final float WHEEL_HALF = 6.3F;
	private static final float RING_INNER = 6.45F;
	private static final float RING_OUTER = 7.1F;
	private static final float RING_BOTTOM = 12.0F;
	private static final float RING_TOP = 13.4F;
	private static final int RING_SEGMENTS = 48;
	private static final float BALL_Y = 12.45F;
	private static final int BALL_STACKS = 12;
	private static final int BALL_SLICES = 24;
	private static final float BALL_HALF = 0.5F;
	private static final float TRACK_RADIUS = 5.15F;
	private static final float POCKET_RADIUS = 4.4F;
	private static final float CHIP_Y = 12.2F;
	private static final float CHIP_SCALE = 0.17F;
	private static final int STACK_HEIGHT = 24;
	private static final float LAYER_STEP = 0.375F;
	private static final int MAX_CHIPS_PER_ZONE = 96;
	private static final float STACK_SPREAD_X = 0.75F;
	private static final float STACK_SPREAD_Z = 0.7F;

	private final ItemRenderer itemRenderer;

	public RouletteRenderer(BlockEntityRendererProvider.Context context) {
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(RouletteBlockEntity table, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
		Level level = table.getLevel();
		BlockState state = table.getBlockState();
		if (level == null || !state.hasProperty(RouletteBlock.FACING)) {
			return;
		}
		Direction facing = state.getValue(RouletteBlock.FACING);
		float elapsed = table.spinning() ? level.getGameTime() + partialTick - table.startTime() : -1.0F;
		float wheelAngle = table.wheelEnd();
		float ballExtra = 0.0F;
		float ballRadius = POCKET_RADIUS;
		if (elapsed >= 0.0F) {
			float progress = Mth.clamp(elapsed / RouletteBlockEntity.TOTAL_TICKS, 0.0F, 1.0F);
			float eased = 1.0F - (1.0F - progress) * (1.0F - progress);
			wheelAngle = table.wheelStart() + (table.wheelEnd() - table.wheelStart()) * eased;
			float ballProgress = Mth.clamp(elapsed / RouletteBlockEntity.BALL_DROP_TICKS, 0.0F, 1.0F);
			ballExtra = -360.0F * RouletteBlockEntity.BALL_TURNS * (1.0F - ballProgress) * (1.0F - ballProgress);
			float drop = Mth.clamp((ballProgress - 0.8F) / 0.2F, 0.0F, 1.0F);
			ballRadius = Mth.lerp(drop, TRACK_RADIUS, POCKET_RADIUS);
		}

		pose.pushPose();
		pose.translate(0.5D, 0.0D, 0.5D);
		pose.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
		pose.translate(-0.5D, 0.0D, -0.5D);

		renderRing(buffers.getBuffer(RenderType.entityCutoutNoCull(GOLD)), pose.last(), light);

		pose.pushPose();
		pose.translate(CENTER_X / 16.0D, WHEEL_Y / 16.0D, CENTER_Z / 16.0D);
		pose.mulPose(Axis.YP.rotationDegrees(wheelAngle));
		renderWheel(buffers.getBuffer(RenderType.entityCutoutNoCull(WHEEL)), pose.last(), light);
		pose.popPose();

		float ballAngle = (float) Math.toRadians(RouletteWheel.pocketAngle(table.pocket()) - wheelAngle + ballExtra);
		float ballX = CENTER_X + ballRadius * (float) Math.sin(ballAngle);
		float ballZ = CENTER_Z - ballRadius * (float) Math.cos(ballAngle);
		boolean rolling = elapsed >= 0.0F && elapsed < RouletteBlockEntity.BALL_DROP_TICKS;
		pose.pushPose();
		pose.translate(ballX / 16.0D, (BALL_Y + BALL_HALF) / 16.0D, ballZ / 16.0D);
		pose.mulPose(Axis.YP.rotation((float) (Math.PI / 2.0D) - ballAngle));
		pose.mulPose(Axis.XP.rotation(rolling ? ballAngle * ballRadius / BALL_HALF : 0.0F));
		renderBall(buffers.getBuffer(RenderType.entityCutoutNoCull(CHROME)), pose.last(), light);
		pose.popPose();

		renderChips(table, level, pose, buffers, light);
		pose.popPose();
	}

	private void renderChips(RouletteBlockEntity table, Level level, PoseStack pose, MultiBufferSource buffers, int light) {
		List<RouletteBlockEntity.Bet> bets = table.bets();
		int[] totals = new int[RouletteBet.COUNT];
		for (RouletteBlockEntity.Bet bet : bets) {
			totals[bet.zone().id()]++;
		}
		int[] counts = new int[RouletteBet.COUNT];
		for (RouletteBlockEntity.Bet bet : bets) {
			int zone = bet.zone().id();
			int index = counts[zone]++;
			if (index >= MAX_CHIPS_PER_ZONE) {
				continue;
			}
			int stacks = Math.min(4, (Math.min(totals[zone], MAX_CHIPS_PER_ZONE) + STACK_HEIGHT - 1) / STACK_HEIGHT);
			int stack = index / STACK_HEIGHT;
			int layer = index % STACK_HEIGHT;
			float offsetX = 0.0F;
			float offsetZ = 0.0F;
			if (stacks == 2) {
				offsetX = (stack == 0 ? -1.0F : 1.0F) * STACK_SPREAD_X;
			} else if (stacks > 2) {
				offsetX = (stack % 2 == 0 ? -1.0F : 1.0F) * STACK_SPREAD_X;
				offsetZ = (stack / 2 == 0 ? -1.0F : 1.0F) * STACK_SPREAD_Z;
			}
			float x = 16.0F - bet.zone().centerX() + offsetX;
			float z = RouletteBet.TABLE_DEPTH - bet.zone().centerY() + offsetZ;
			pose.pushPose();
			pose.translate(x / 16.0D, (CHIP_Y + layer * LAYER_STEP) / 16.0D, z / 16.0D);
			pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
			pose.mulPose(Axis.ZP.rotationDegrees(180.0F + ((index * 37) % 17 - 8)));
			pose.scale(CHIP_SCALE, CHIP_SCALE, CHIP_SCALE);
			itemRenderer.renderStatic(new ItemStack(bet.chip()), ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buffers, level, index);
			pose.popPose();
		}
	}

	private static void renderBall(VertexConsumer consumer, PoseStack.Pose pose, int light) {
		for (int i = 0; i < BALL_STACKS; i++) {
			double phi0 = -Math.PI / 2.0D + Math.PI * i / BALL_STACKS;
			double phi1 = -Math.PI / 2.0D + Math.PI * (i + 1) / BALL_STACKS;
			float v0 = 1.0F - (float) i / BALL_STACKS;
			float v1 = 1.0F - (float) (i + 1) / BALL_STACKS;
			for (int j = 0; j < BALL_SLICES; j++) {
				double th0 = Math.PI * 2.0D * j / BALL_SLICES;
				double th1 = Math.PI * 2.0D * (j + 1) / BALL_SLICES;
				float u0 = (float) j / BALL_SLICES;
				float u1 = (float) (j + 1) / BALL_SLICES;
				ballVertex(consumer, pose, light, phi0, th0, u0, v0);
				ballVertex(consumer, pose, light, phi0, th1, u1, v0);
				ballVertex(consumer, pose, light, phi1, th1, u1, v1);
				ballVertex(consumer, pose, light, phi1, th0, u0, v1);
			}
		}
	}

	private static void ballVertex(VertexConsumer consumer, PoseStack.Pose pose, int light, double phi, double theta, float u, float v) {
		float nx = (float) (Math.cos(phi) * Math.cos(theta));
		float ny = (float) Math.sin(phi);
		float nz = (float) (Math.cos(phi) * Math.sin(theta));
		consumer.addVertex(pose, nx * BALL_HALF / 16.0F, ny * BALL_HALF / 16.0F, nz * BALL_HALF / 16.0F)
				.setColor(255, 255, 255, 255)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(light)
				.setNormal(pose, nx, ny, nz);
	}

	private static void renderRing(VertexConsumer consumer, PoseStack.Pose pose, int light) {
		for (int i = 0; i < RING_SEGMENTS; i++) {
			double a0 = Math.PI * 2.0D * i / RING_SEGMENTS;
			double a1 = Math.PI * 2.0D * (i + 1) / RING_SEGMENTS;
			float s0 = (float) Math.sin(a0);
			float c0 = (float) Math.cos(a0);
			float s1 = (float) Math.sin(a1);
			float c1 = (float) Math.cos(a1);
			float u0 = (float) i / RING_SEGMENTS;
			float u1 = (float) (i + 1) / RING_SEGMENTS;
			float mid = (float) ((a0 + a1) / 2.0D);
			float ox = (float) Math.sin(mid);
			float oz = -(float) Math.cos(mid);
			wall(consumer, pose, light, RING_OUTER, RING_BOTTOM, RING_TOP, s0, c0, s1, c1, u0, u1, ox, oz);
			wall(consumer, pose, light, RING_INNER, RING_TOP, RING_BOTTOM, s1, c1, s0, c0, u1, u0, -ox, -oz);
			ringVertex(consumer, pose, light, RING_INNER * s0, RING_TOP, -RING_INNER * c0, u0, 0.0F, 0.0F, 1.0F, 0.0F);
			ringVertex(consumer, pose, light, RING_OUTER * s0, RING_TOP, -RING_OUTER * c0, u0, 1.0F, 0.0F, 1.0F, 0.0F);
			ringVertex(consumer, pose, light, RING_OUTER * s1, RING_TOP, -RING_OUTER * c1, u1, 1.0F, 0.0F, 1.0F, 0.0F);
			ringVertex(consumer, pose, light, RING_INNER * s1, RING_TOP, -RING_INNER * c1, u1, 0.0F, 0.0F, 1.0F, 0.0F);
		}
	}

	private static void wall(VertexConsumer consumer, PoseStack.Pose pose, int light, float radius, float yFrom, float yTo,
			float s0, float c0, float s1, float c1, float u0, float u1, float nx, float nz) {
		ringVertex(consumer, pose, light, radius * s0, yFrom, -radius * c0, u0, 1.0F, nx, 0.0F, nz);
		ringVertex(consumer, pose, light, radius * s1, yFrom, -radius * c1, u1, 1.0F, nx, 0.0F, nz);
		ringVertex(consumer, pose, light, radius * s1, yTo, -radius * c1, u1, 0.0F, nx, 0.0F, nz);
		ringVertex(consumer, pose, light, radius * s0, yTo, -radius * c0, u0, 0.0F, nx, 0.0F, nz);
	}

	private static void ringVertex(VertexConsumer consumer, PoseStack.Pose pose, int light, float x, float y, float z, float u, float v, float nx, float ny, float nz) {
		consumer.addVertex(pose, (CENTER_X + x) / 16.0F, y / 16.0F, (CENTER_Z + z) / 16.0F)
				.setColor(255, 255, 255, 255)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(light)
				.setNormal(pose, nx, ny, nz);
	}

	private static void renderWheel(VertexConsumer consumer, PoseStack.Pose pose, int light) {
		float h = WHEEL_HALF / 16.0F;
		vertex(consumer, pose, light, -h, -h, 0.0F, 0.0F);
		vertex(consumer, pose, light, -h, h, 0.0F, 1.0F);
		vertex(consumer, pose, light, h, h, 1.0F, 1.0F);
		vertex(consumer, pose, light, h, -h, 1.0F, 0.0F);
	}

	private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int light, float x, float z, float u, float v) {
		consumer.addVertex(pose, x, 0.0F, z)
				.setColor(255, 255, 255, 255)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(light)
				.setNormal(pose, 0.0F, 1.0F, 0.0F);
	}
}
