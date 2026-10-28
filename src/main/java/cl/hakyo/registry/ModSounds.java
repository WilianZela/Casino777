package cl.hakyo.registry;

import cl.hakyo.Modcasino;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {
	public static final SoundEvent PLUSH_SQUEAK = register("plush_squeak");

	private ModSounds() {
	}

	private static SoundEvent register(String name) {
		return Registry.register(BuiltInRegistries.SOUND_EVENT, Modcasino.id(name), SoundEvent.createVariableRangeEvent(Modcasino.id(name)));
	}

	public static void register() {
	}
}
