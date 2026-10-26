package cl.hakyo.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;

final class Boxes {
	private Boxes() {
	}

	static void box(VertexConsumer consumer, PoseStack.Pose pose, float ux1, float uy1, float uz1, float ux2, float uy2, float uz2, int light) {
		float x1 = ux1 / 16.0F;
		float y1 = uy1 / 16.0F;
		float z1 = uz1 / 16.0F;
		float x2 = ux2 / 16.0F;
		float y2 = uy2 / 16.0F;
		float z2 = uz2 / 16.0F;
		quad(consumer, pose, light, 0, -1, 0, x1, y1, z2, x1, y1, z1, x2, y1, z1, x2, y1, z2);
		quad(consumer, pose, light, 0, 1, 0, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1);
		quad(consumer, pose, light, 0, 0, -1, x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1);
		quad(consumer, pose, light, 0, 0, 1, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
		quad(consumer, pose, light, -1, 0, 0, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1);
		quad(consumer, pose, light, 1, 0, 0, x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2);
	}

	private static void quad(VertexConsumer consumer, PoseStack.Pose pose, int light, float nx, float ny, float nz,
			float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz) {
		vertex(consumer, pose, light, nx, ny, nz, ax, ay, az, 0.0F, 1.0F);
		vertex(consumer, pose, light, nx, ny, nz, bx, by, bz, 1.0F, 1.0F);
		vertex(consumer, pose, light, nx, ny, nz, cx, cy, cz, 1.0F, 0.0F);
		vertex(consumer, pose, light, nx, ny, nz, dx, dy, dz, 0.0F, 0.0F);
	}

	private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int light, float nx, float ny, float nz, float x, float y, float z, float u, float v) {
		consumer.addVertex(pose, x, y, z)
				.setColor(255, 255, 255, 255)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(light)
				.setNormal(pose, nx, ny, nz);
	}
}
