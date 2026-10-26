package cl.hakyo.client.render;

import cl.hakyo.Modcasino;
import cl.hakyo.block.SwivelChairBlockEntity;
import cl.hakyo.entity.ChairSeatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SwivelChairRenderer implements BlockEntityRenderer<SwivelChairBlockEntity> {
	private static final ResourceLocation METAL = Modcasino.id("textures/block/chair_metal.png");
	private static final ResourceLocation SEAT = Modcasino.id("textures/block/chair_seat.png");
	private static final ResourceLocation BACK = Modcasino.id("textures/block/chair_back.png");
	private static final ResourceLocation ARM = Modcasino.id("textures/block/chair_arm.png");

	public SwivelChairRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(SwivelChairBlockEntity chair, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
		Level level = chair.getLevel();
		if (level == null) {
			return;
		}
		float yaw = chair.restYaw();
		for (ChairSeatEntity seat : level.getEntitiesOfClass(ChairSeatEntity.class, new AABB(chair.getBlockPos()))) {
			yaw = Mth.rotLerp(partialTick, seat.yRotO, seat.getYRot());
			break;
		}

		pose.pushPose();
		pose.translate(0.5D, 0.0D, 0.5D);
		pose.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
		pose.translate(-0.5D, 0.0D, -0.5D);

		PoseStack.Pose last = pose.last();
		VertexConsumer chrome = buffers.getBuffer(RenderType.entityCutoutNoCull(METAL));
		Boxes.box(chrome, last, 7.3F, 6.0F, 7.3F, 8.7F, 7.0F, 8.7F, light);
		Boxes.box(chrome, last, 6.0F, 6.0F, 6.0F, 10.0F, 7.5F, 10.0F, light);
		Boxes.box(chrome, last, 5.0F, 9.0F, 11.2F, 6.0F, 10.5F, 12.3F, light);
		Boxes.box(chrome, last, 10.0F, 9.0F, 11.2F, 11.0F, 10.5F, 12.3F, light);
		Boxes.box(chrome, last, 3.0F, 9.0F, 6.0F, 4.0F, 11.0F, 7.0F, light);
		Boxes.box(chrome, last, 12.0F, 9.0F, 6.0F, 13.0F, 11.0F, 7.0F, light);

		Boxes.box(buffers.getBuffer(RenderType.entityCutoutNoCull(SEAT)), last, 3.5F, 7.5F, 3.5F, 12.5F, 9.0F, 12.5F, light);
		Boxes.box(buffers.getBuffer(RenderType.entityCutoutNoCull(BACK)), last, 4.0F, 9.5F, 11.0F, 12.0F, 16.5F, 12.6F, light);
		VertexConsumer arm = buffers.getBuffer(RenderType.entityCutoutNoCull(ARM));
		Boxes.box(arm, last, 2.8F, 11.0F, 4.5F, 4.2F, 12.0F, 9.5F, light);
		Boxes.box(arm, last, 11.8F, 11.0F, 4.5F, 13.2F, 12.0F, 9.5F, light);
		pose.popPose();
	}
}
