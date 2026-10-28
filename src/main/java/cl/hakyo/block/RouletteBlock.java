package cl.hakyo.block;

import cl.hakyo.registry.ModBlockEntities;
import cl.hakyo.roulette.RouletteBet;
import cl.hakyo.slot.SlotPayouts;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RouletteBlock extends BaseEntityBlock {
	public static final MapCodec<RouletteBlock> CODEC = simpleCodec(RouletteBlock::new);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<RoulettePart> PART = EnumProperty.create("part", RoulettePart.class);

	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

	public RouletteBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, RoulettePart.FRONT_LEFT));
	}

	@Override
	protected MapCodec<RouletteBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, PART);
	}

	public static BlockPos partPos(BlockPos origin, Direction facing, RoulettePart part) {
		BlockPos result = origin;
		for (int i = 0; i < part.column(); i++) {
			result = result.relative(facing.getCounterClockWise());
		}
		if (part.back()) {
			result = result.relative(facing.getOpposite());
		}
		return result;
	}

	public static BlockPos originOf(BlockPos pos, BlockState state) {
		Direction facing = state.getValue(FACING);
		RoulettePart part = state.getValue(PART);
		BlockPos result = pos;
		for (int i = 0; i < part.column(); i++) {
			result = result.relative(facing.getClockWise());
		}
		if (part.back()) {
			result = result.relative(facing);
		}
		return result;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing = context.getHorizontalDirection().getOpposite();
		BlockPos origin = context.getClickedPos();
		Level level = context.getLevel();
		for (RoulettePart part : RoulettePart.values()) {
			if (part != RoulettePart.FRONT_LEFT && !level.getBlockState(partPos(origin, facing, part)).canBeReplaced(context)) {
				return null;
			}
		}
		return defaultBlockState().setValue(FACING, facing).setValue(PART, RoulettePart.FRONT_LEFT);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		Direction facing = state.getValue(FACING);
		for (RoulettePart part : RoulettePart.values()) {
			if (part != RoulettePart.FRONT_LEFT) {
				level.setBlock(partPos(pos, facing, part), state.setValue(PART, part), 3);
			}
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		Direction facing = state.getValue(FACING);
		RoulettePart part = state.getValue(PART);
		Direction depth = part.back() ? facing : facing.getOpposite();
		RoulettePart expected = null;
		if (direction == facing.getCounterClockWise() && part.column() < RoulettePart.COLUMNS - 1) {
			expected = RoulettePart.of(part.column() + 1, part.back());
		} else if (direction == facing.getClockWise() && part.column() > 0) {
			expected = RoulettePart.of(part.column() - 1, part.back());
		} else if (direction == depth) {
			expected = RoulettePart.of(part.column(), !part.back());
		}
		if (expected != null && !(neighborState.is(this) && neighborState.getValue(PART) == expected && neighborState.getValue(FACING) == facing)) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		Direction facing = state.getValue(FACING);
		BlockPos origin = originOf(pos, state);
		if (!level.isClientSide && !player.isCreative() && state.getValue(PART) != RoulettePart.FRONT_LEFT) {
			BlockState originState = level.getBlockState(origin);
			if (originState.is(this)) {
				Block.dropResources(originState, level, origin, level.getBlockEntity(origin), player, player.getMainHandItem());
			}
		}
		for (RoulettePart part : RoulettePart.values()) {
			BlockPos other = partPos(origin, facing, part);
			if (!other.equals(pos) && level.getBlockState(other).is(this)) {
				level.setBlock(other, Blocks.AIR.defaultBlockState(), 51);
			}
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock()) && state.getValue(PART) == RoulettePart.FRONT_LEFT && level instanceof ServerLevel server
				&& level.getBlockEntity(pos) instanceof RouletteBlockEntity table) {
			table.refund(server, state.getValue(FACING));
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(PART) == RoulettePart.FRONT_LEFT ? new RouletteBlockEntity(pos, state) : null;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide || state.getValue(PART) != RoulettePart.FRONT_LEFT) {
			return null;
		}
		return createTickerHelper(type, ModBlockEntities.ROULETTE, RouletteBlockEntity::tick);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	private static RouletteBet zone(BlockState state, BlockPos pos, Vec3 hit) {
		double lx = hit.x - pos.getX();
		double lz = hit.z - pos.getZ();
		double depth;
		double side;
		switch (state.getValue(FACING)) {
			case EAST -> {
				depth = 1.0D - lx;
				side = 1.0D - lz;
			}
			case SOUTH -> {
				depth = 1.0D - lz;
				side = lx;
			}
			case WEST -> {
				depth = lx;
				side = lz;
			}
			default -> {
				depth = lz;
				side = 1.0D - lx;
			}
		}
		RoulettePart part = state.getValue(PART);
		double x = (side + part.column()) * 16.0D;
		double y = RouletteBet.TABLE_DEPTH - (depth + (part.back() ? 1.0D : 0.0D)) * 16.0D;
		return RouletteBet.at(x, y);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!SlotPayouts.isChip(stack)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if (level.isClientSide) {
			return ItemInteractionResult.SUCCESS;
		}
		BlockPos origin = originOf(pos, state);
		if (!(level.getBlockEntity(origin) instanceof RouletteBlockEntity table) || !(level instanceof ServerLevel server) || !(player instanceof ServerPlayer bettor)) {
			return ItemInteractionResult.CONSUME;
		}
		if (table.spinning()) {
			player.displayClientMessage(Component.translatable("message.modcasino.roulette_busy"), true);
			return ItemInteractionResult.CONSUME;
		}
		RouletteBet bet = hitResult.getDirection() == Direction.UP ? zone(state, pos, hitResult.getLocation()) : null;
		if (bet == null) {
			player.displayClientMessage(Component.translatable("message.modcasino.roulette_pick"), true);
			return ItemInteractionResult.CONSUME;
		}
		if (stack.getCount() < SlotPayouts.BET && !player.getAbilities().instabuild) {
			return ItemInteractionResult.CONSUME;
		}
		if (!table.placeBet(server, bettor, stack.getItem(), bet)) {
			player.displayClientMessage(Component.translatable("message.modcasino.roulette_full"), true);
			return ItemInteractionResult.CONSUME;
		}
		if (!player.getAbilities().instabuild) {
			stack.shrink(SlotPayouts.BET);
		}
		player.displayClientMessage(Component.translatable("message.modcasino.roulette_bet", bet.label(), table.betsOf(player.getUUID())), true);
		return ItemInteractionResult.CONSUME;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide) {
			boolean busy = level.getBlockEntity(originOf(pos, state)) instanceof RouletteBlockEntity table && table.spinning();
			player.displayClientMessage(Component.translatable(busy ? "message.modcasino.roulette_busy" : "message.modcasino.roulette_pick"), true);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
