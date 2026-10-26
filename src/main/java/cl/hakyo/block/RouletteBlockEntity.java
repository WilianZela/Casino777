package cl.hakyo.block;

import cl.hakyo.registry.ModBlockEntities;
import cl.hakyo.roulette.RouletteBet;
import cl.hakyo.roulette.RouletteWheel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RouletteBlockEntity extends BlockEntity {
	public static final int BETTING_TICKS = 300;
	public static final int TOTAL_TICKS = 200;
	public static final int BALL_DROP_TICKS = 180;
	public static final float BALL_TURNS = 8.0F;
	public static final int MAX_BETS = 256;

	private static final int IDLE = 0;
	private static final int BETTING = 1;
	private static final int SPINNING = 2;

	public record Bet(UUID player, Item chip, RouletteBet zone) {
	}

	private final List<Bet> bets = new ArrayList<>();
	private int phase;
	private long phaseStart;
	private int pocket;
	private float wheelStart;
	private float wheelEnd;

	public RouletteBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ROULETTE, pos, state);
	}

	public boolean spinning() {
		return phase == SPINNING;
	}

	public long startTime() {
		return phaseStart;
	}

	public int pocket() {
		return pocket;
	}

	public float wheelStart() {
		return wheelStart;
	}

	public float wheelEnd() {
		return wheelEnd;
	}

	public List<Bet> bets() {
		return bets;
	}

	public int betsOf(UUID player) {
		int count = 0;
		for (Bet bet : bets) {
			if (bet.player().equals(player)) {
				count++;
			}
		}
		return count;
	}

	public boolean placeBet(ServerLevel level, ServerPlayer player, Item chip, RouletteBet zone) {
		if (phase == SPINNING || bets.size() >= MAX_BETS) {
			return false;
		}
		bets.add(new Bet(player.getUUID(), chip, zone));
		if (phase == IDLE) {
			phase = BETTING;
			phaseStart = level.getGameTime();
			level.playSound(null, worldPosition, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.0F, 0.6F);
		}
		level.playSound(null, worldPosition, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.7F, 1.3F + level.random.nextFloat() * 0.3F);
		sync();
		return true;
	}

	public static void tick(Level level, BlockPos pos, BlockState state, RouletteBlockEntity table) {
		if (level instanceof ServerLevel server) {
			table.serverTick(server, state);
		}
	}

	private void serverTick(ServerLevel level, BlockState state) {
		if (phase == IDLE) {
			return;
		}
		long elapsed = level.getGameTime() - phaseStart;
		if (elapsed < 0) {
			phaseStart = level.getGameTime();
			elapsed = 0;
		}
		if (phase == BETTING) {
			if (bets.isEmpty()) {
				phase = IDLE;
				sync();
				return;
			}
			if (elapsed % 20 == 0 && elapsed < BETTING_TICKS) {
				Component closing = Component.translatable("message.modcasino.roulette_closing", (BETTING_TICKS - elapsed) / 20);
				notifyNearby(level, closing, 10.0D);
			}
			if (elapsed >= BETTING_TICKS) {
				startSpin(level);
			}
			return;
		}
		if (elapsed > 10 && elapsed < BALL_DROP_TICKS) {
			long interval = 2 + elapsed / 30;
			if (elapsed % interval == 0) {
				level.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.BLOCKS, 0.3F, 1.5F + (elapsed % 5) * 0.05F);
			}
		}
		if (elapsed == BALL_DROP_TICKS) {
			level.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.BLOCKS, 0.9F, 0.9F);
			level.playSound(null, worldPosition, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.8F, 1.6F);
		}
		if (elapsed >= TOTAL_TICKS) {
			finish(level, state);
		}
	}

	private void startSpin(ServerLevel level) {
		phase = SPINNING;
		phaseStart = level.getGameTime();
		pocket = level.random.nextInt(RouletteWheel.POCKETS);
		wheelStart = Math.floorMod(Math.round(wheelEnd), 360);
		wheelEnd = wheelStart + 1080.0F + level.random.nextInt(360);
		sync();
		level.playSound(null, worldPosition, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), SoundSource.BLOCKS, 0.9F, 0.8F);
		notifyNearby(level, Component.translatable("message.modcasino.roulette_no_more_bets"), 10.0D);
	}

	private void finish(ServerLevel level, BlockState state) {
		int number = RouletteWheel.number(pocket);
		Direction facing = state.getValue(RouletteBlock.FACING);
		Vec3 front = Vec3.atBottomCenterOf(worldPosition).add(facing.getStepX() * 0.5D, 1.0D, facing.getStepZ() * 0.5D);
		Map<UUID, Map<Item, Integer>> won = new LinkedHashMap<>();
		Map<UUID, Integer> staked = new LinkedHashMap<>();
		for (Bet bet : bets) {
			staked.merge(bet.player(), 1, Integer::sum);
			int payout = bet.zone().payoutFor(number);
			if (payout > 0) {
				won.computeIfAbsent(bet.player(), key -> new LinkedHashMap<>()).merge(bet.chip(), payout, Integer::sum);
			}
		}
		Component result = Component.translatable("message.modcasino.roulette_result", number, Component.translatable("message.modcasino.roulette_" + RouletteWheel.colorKey(number)));
		for (UUID id : staked.keySet()) {
			Map<Item, Integer> prizes = won.get(id);
			ServerPlayer player = level.getServer().getPlayerList().getPlayer(id);
			int total = 0;
			if (prizes != null) {
				for (Map.Entry<Item, Integer> entry : prizes.entrySet()) {
					total += entry.getValue();
					give(level, player, entry.getKey(), entry.getValue(), front);
				}
			}
			if (player != null) {
				Component outcome = total > 0
						? Component.translatable("message.modcasino.roulette_you_win", total)
						: Component.translatable("message.modcasino.roulette_you_lose");
				player.displayClientMessage(Component.empty().append(result).append(Component.literal(" · ")).append(outcome), true);
			}
		}
		if (!won.isEmpty()) {
			level.playSound(null, worldPosition, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.8F, 1.3F);
		}
		notifyNearby(level, result, 12.0D, staked.keySet());
		bets.clear();
		phase = IDLE;
		sync();
	}

	private static void give(ServerLevel level, ServerPlayer player, Item chip, int amount, Vec3 fallback) {
		int remaining = amount;
		while (remaining > 0) {
			int count = Math.min(remaining, chip.getDefaultMaxStackSize());
			remaining -= count;
			ItemStack stack = new ItemStack(chip, count);
			if (player != null) {
				player.getInventory().add(stack);
				if (!stack.isEmpty()) {
					player.drop(stack, false);
				}
			} else {
				ItemEntity item = new ItemEntity(level, fallback.x, fallback.y, fallback.z, stack);
				item.setDefaultPickUpDelay();
				level.addFreshEntity(item);
			}
		}
	}

	private void notifyNearby(ServerLevel level, Component message, double range) {
		notifyNearby(level, message, range, Set.of());
	}

	private void notifyNearby(ServerLevel level, Component message, double range, Set<UUID> skip) {
		for (ServerPlayer player : level.players()) {
			if (!skip.contains(player.getUUID()) && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY(), worldPosition.getZ() + 0.5D) < range * range) {
				player.displayClientMessage(message, true);
			}
		}
	}

	public void refund(ServerLevel level, Direction facing) {
		Vec3 front = Vec3.atBottomCenterOf(worldPosition).add(facing.getStepX() * 0.5D, 0.5D, facing.getStepZ() * 0.5D);
		Map<Item, Integer> totals = new LinkedHashMap<>();
		for (Bet bet : bets) {
			totals.merge(bet.chip(), 1, Integer::sum);
		}
		for (Map.Entry<Item, Integer> entry : totals.entrySet()) {
			give(level, null, entry.getKey(), entry.getValue(), front);
		}
		bets.clear();
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
		tag.putInt("Phase", phase);
		tag.putLong("PhaseStart", phaseStart);
		tag.putInt("Pocket", pocket);
		tag.putFloat("WheelStart", wheelStart);
		tag.putFloat("WheelEnd", wheelEnd);
		ListTag list = new ListTag();
		for (Bet bet : bets) {
			CompoundTag entry = new CompoundTag();
			entry.putUUID("Player", bet.player());
			entry.putString("Chip", BuiltInRegistries.ITEM.getKey(bet.chip()).toString());
			entry.putInt("Zone", bet.zone().ordinal());
			list.add(entry);
		}
		tag.put("Bets", list);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		phase = tag.getInt("Phase");
		phaseStart = tag.getLong("PhaseStart");
		pocket = Math.floorMod(tag.getInt("Pocket"), RouletteWheel.POCKETS);
		wheelStart = tag.getFloat("WheelStart");
		wheelEnd = tag.getFloat("WheelEnd");
		bets.clear();
		ListTag list = tag.getList("Bets", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entry = list.getCompound(i);
			ResourceLocation id = ResourceLocation.tryParse(entry.getString("Chip"));
			Item chip = id == null ? Items.AIR : BuiltInRegistries.ITEM.get(id);
			if (chip != Items.AIR && entry.hasUUID("Player")) {
				bets.add(new Bet(entry.getUUID("Player"), chip, RouletteBet.byOrdinal(entry.getInt("Zone"))));
			}
		}
	}
}
