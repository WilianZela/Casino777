package cl.hakyo.client;

import cl.hakyo.client.render.RouletteRenderer;
import cl.hakyo.client.render.SlotMachineRenderer;
import cl.hakyo.client.render.SwivelChairRenderer;
import cl.hakyo.registry.ModBlockEntities;
import cl.hakyo.registry.ModBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import cl.hakyo.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;

public class ModcasinoClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ModBlockEntities.SLOT_MACHINE, SlotMachineRenderer::new);
		BlockEntityRenderers.register(ModBlockEntities.ROULETTE, RouletteRenderer::new);
		BlockEntityRenderers.register(ModBlockEntities.SWIVEL_CHAIR, SwivelChairRenderer::new);
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PLUSHIE_ZZILEXX, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PLUSHIE_RENKUNSHITI, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PLUSHIE_TV_TOXIC_VT_2, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PLUSHIE_TV_TOXIC_VT_1, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PLUSHIE_BLACK, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PLUSHIE_MUSHROOM_KING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PLUSHIE_KAEL, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PLUSHIE_AZAZEL, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PLUSHIE_CRUCIFIED, RenderType.cutout());
		ModBlocks.FUNKOS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout()));
		EntityRendererRegistry.register(ModEntities.CHAIR_SEAT, NoopRenderer::new);
		ClientTickEvents.END_CLIENT_TICK.register(ChairSpinHandler::tick);
	}
}
