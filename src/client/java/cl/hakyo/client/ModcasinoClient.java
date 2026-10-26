package cl.hakyo.client;

import cl.hakyo.client.render.RouletteRenderer;
import cl.hakyo.client.render.SlotMachineRenderer;
import cl.hakyo.client.render.SwivelChairRenderer;
import cl.hakyo.registry.ModBlockEntities;
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
		EntityRendererRegistry.register(ModEntities.CHAIR_SEAT, NoopRenderer::new);
		ClientTickEvents.END_CLIENT_TICK.register(ChairSpinHandler::tick);
	}
}
