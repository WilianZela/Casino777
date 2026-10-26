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
	private static final ResourceLocation CHROME = Modcasino.id("textures/block/roulette_ball.png");

	private static final float CENTER_X = 0.0F;
	private static final float CENTER_Z = 24.0F;
	private static final float WHEEL_Y = 12.42F;
	private static final float WHEEL_HALF = 7.0F;
	private static final float BALL_Y = 12.5F;
	private static final float BALL_HALF = 0.5F;
	private static final float TRACK_RADIUS = 6.5F;
	private static final float POCKET_RADIUS = 5.53F;
	private static final float CHIP_Y = 12.35F;
	private static final float CHIP_SCALE = 0.3F;
	private static final int STACK_HEIGHT = 12;
	private static final float LAYER_STEP = 0.62F;
	private static final int MAX_CHIPS_PER_ZONE = 72;
	private static final float[] ZONE_CENTER_A = {0.4F, 1.0F, 1.6F};
	private static final float ZONE_CENTER_S = 0.49F;

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

		pose.pushPose();
		pose.translate(CENTER_X / 16.0D, WHEEL_Y / 16.0D, CENTER_Z / 16.0D);
		pose.mulPose(Axis.YP.rotationDegrees(wheelAngle));
		renderWheel(buffers.getBuffer(RenderType.entityCutoutNoCull(WHEEL)), pose.last(), light);
		pose.popPose();

		float ballAngle = (float) Math.toRadians(RouletteWheel.pocketAngle(table.pocket()) - wheelAngle + ballExtra);
		float ballX = CENTER_X + ballRadius * (float) Math.sin(ballAngle);
		float ballZ = CENTER_Z - ballRadius * (float) Math.cos(ballAngle);
		Boxes.box(buffers.getBuffer(RenderType.entityCutoutNoCull(CHROME)), pose.last(), ballX - BALL_HALF, BALL_Y, ballZ - BALL_HALF, ballX + BALL_HALF, BALL_Y + BALL_HALF * 2.0F, ballZ + BALL_HALF, light);

		renderChips(table, level, pose, buffers, light);
		pose.popPose();
	}

	private void renderChips(RouletteBlockEntity table, Level level, PoseStack pose, MultiBufferSource buffers, int light) {
		List<RouletteBlockEntity.Bet> bets = table.bets();
		int[] counts = new int[RouletteBet.values().length];
		for (RouletteBlockEntity.Bet bet : bets) {
			int zone = bet.zone().ordinal();
			int index = counts[zone]++;
			if (index >= MAX_CHIPS_PER_ZONE) {
				continue;
			}
			int stack = index / STACK_HEIGHT;
			int layer = index % STACK_HEIGHT;
			int column = stack % 3;
			int row = stack / 3;
			float a = ZONE_CENTER_A[zone] + (column - 1) * 0.19F;
			float s = ZONE_CENTER_S + (row - 0.5F) * 0.22F;
			float x = 16.0F - 16.0F * a;
			float z = 16.0F * s;
			pose.pushPose();
			pose.translate(x / 16.0D, (CHIP_Y + layer * LAYER_STEP) / 16.0D, z / 16.0D);
			pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
			pose.mulPose(Axis.ZP.rotationDegrees(180.0F + ((index * 37) % 17 - 8)));
			pose.scale(CHIP_SCALE, CHIP_SCALE, CHIP_SCALE);
			itemRenderer.renderStatic(new ItemStack(bet.chip()), ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buffers, level, index);
			pose.popPose();
		}
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
