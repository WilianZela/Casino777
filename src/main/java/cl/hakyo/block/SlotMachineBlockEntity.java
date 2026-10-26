package cl.hakyo.block;

import cl.hakyo.registry.ModBlockEntities;
import cl.hakyo.slot.SlotPayouts;
import cl.hakyo.slot.SlotSymbol;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SlotMachineBlockEntity extends BlockEntity {
	public static final int PULL_TICKS = 8;
	public static final int[] STOP_TICKS = {70, 95, 120};
	public static final int[] TURNS = {6, 8, 10};
	public static final int EASE_TICKS = 12;
	public static final int FINISH_TICKS = PULL_TICKS + STOP_TICKS[2] + 14;
	public static final int CELEBRATION_TICKS = 200;

	private boolean spinning;
	private long startTime;
	private long celebrationUntil;
	private int[] symbols = {0, 0, 0};
	private int[] previous = {0, 0, 0};
	private int payout;
	private Item chip = Items.AIR;

	public SlotMachineBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SLOT_MACHINE, pos, state);
	}

	public boolean spinning() {
		return spinning;
	}

	public long startTime() {
		return startTime;
	}

	public long celebrationUntil() {
		return celebrationUntil;
	}

	public int symbol(int reel) {
		return symbols[reel];
	}

	public int previous(int reel) {
		return previous[reel];
	}

	public boolean start(ServerLevel level, Item bet) {
		if (spinning) {
			return false;
		}
		chip = bet;
		previous = symbols.clone();
		int[] next = new int[3];
		for (int reel = 0; reel < 3; reel++) {
			next[reel] = level.random.nextInt(SlotSymbol.DRUM_SIZE);
		}
		symbols = next;
		payout = SlotPayouts.payout(next);
		spinning = true;
		startTime = level.getGameTime();
		sync();
		level.playSound(null, worldPosition, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.0F, 0.7F);
		level.playSound(null, worldPosition, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, 1.6F);
		return true;
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SlotMachineBlockEntity machine) {
		if (level instanceof ServerLevel server) {
			machine.serverTick(server, state);
		}
	}

	private void serverTick(ServerLevel level, BlockState state) {
		if (!spinning) {
			return;
		}
		long elapsed = level.getGameTime() - startTime;
		if (elapsed < 0) {
			startTime = level.getGameTime();
			elapsed = 0;
		}
		if (elapsed == PULL_TICKS) {
			level.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.BLOCKS, 0.6F, 1.2F);
		}
		if (elapsed > PULL_TICKS && elapsed % 3 == 0 && elapsed < PULL_TICKS + STOP_TICKS[2]) {
			level.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.BLOCKS, 0.35F, 1.4F + (elapsed % 9) * 0.05F);
		}
		for (int reel = 0; reel < 3; reel++) {
			if (elapsed == PULL_TICKS + STOP_TICKS[reel]) {
				level.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.BLOCKS, 0.8F, 0.8F + reel * 0.2F);
				level.playSound(null, worldPosition, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.4F, 1.8F);
			}
		}
		if (elapsed >= FINISH_TICKS) {
			finish(level, state);
		}
	}

	private void finish(ServerLevel level, BlockState state) {
		spinning = false;
		boolean jackpot = SlotPayouts.isJackpot(symbols);
		if (jackpot) {
			celebrationUntil = level.getGameTime() + CELEBRATION_TICKS;
		}
		sync();
		Direction facing = state.getValue(SlotMachineBlock.FACING);
		Vec3 center = Vec3.atBottomCenterOf(worldPosition.below());
		if (payout > 0 && chip != Items.AIR) {
			level.playSound(null, worldPosition, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.8F, jackpot ? 0.6F : 1.4F);
			int remaining = payout;
			while (remaining > 0) {
				int count = Math.min(remaining, chip.getDefaultMaxStackSize());
				remaining -= count;
				ItemEntity item = new ItemEntity(level, center.x + facing.getStepX() * 0.7D, center.y + 0.4D, center.z + facing.getStepZ() * 0.7D, new ItemStack(chip, count));
				item.setDeltaMovement(facing.getStepX() * 0.12D + (level.random.nextDouble() - 0.5D) * 0.06D, 0.18D, facing.getStepZ() * 0.12D + (level.random.nextDouble() - 0.5D) * 0.06D);
				item.setDefaultPickUpDelay();
				level.addFreshEntity(item);
			}
		}
		if (jackpot) {
			level.playSound(null, worldPosition, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1.6F, 1.0F);
			level.playSound(null, worldPosition, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.BLOCKS, 1.4F, 0.9F);
			level.sendParticles(ParticleTypes.FIREWORK, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.1D, worldPosition.getZ() + 0.5D, 90, 0.7D, 0.6D, 0.7D, 0.12D);
			level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.6D, worldPosition.getZ() + 0.5D, 60, 0.6D, 0.8D, 0.6D, 0.3D);
			Component message = Component.translatable("message.modcasino.jackpot", payout);
			for (ServerPlayer player : level.players()) {
				if (player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY(), worldPosition.getZ() + 0.5D) < 32.0D * 32.0D) {
					player.displayClientMessage(message, false);
					player.displayClientMessage(message, true);
				}
			}
		}
	}

	private void sync() {
		setChanged();
		if (level != null) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveWithoutMetadata(registries);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putBoolean("Spinning", spinning);
		tag.putLong("StartTime", startTime);
		tag.putLong("CelebrationUntil", celebrationUntil);
		tag.putIntArray("Symbols", symbols);
		tag.putIntArray("Previous", previous);
		tag.putInt("Payout", payout);
		tag.putString("Chip", BuiltInRegistries.ITEM.getKey(chip).toString());
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		spinning = tag.getBoolean("Spinning");
		startTime = tag.getLong("StartTime");
		celebrationUntil = tag.getLong("CelebrationUntil");
		symbols = read(tag.getIntArray("Symbols"));
		previous = read(tag.getIntArray("Previous"));
		payout = tag.getInt("Payout");
		ResourceLocation chipId = ResourceLocation.tryParse(tag.getString("Chip"));
		chip = chipId == null ? Items.AIR : BuiltInRegistries.ITEM.get(chipId);
	}

	private static int[] read(int[] stored) {
		int[] result = new int[3];
		for (int i = 0; i < 3 && i < stored.length; i++) {
			result[i] = Math.floorMod(stored[i], SlotSymbol.DRUM_SIZE);
		}
		return result;
	}
}
