package cl.hakyo.block;

import cl.hakyo.registry.ModBlockEntities;
import cl.hakyo.slot.SlotPayouts;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlotMachineBlock extends BaseEntityBlock {
	public static final MapCodec<SlotMachineBlock> CODEC = simpleCodec(SlotMachineBlock::new);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

	public SlotMachineBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	protected MapCodec<SlotMachineBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, HALF);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();
		if (pos.getY() >= level.getMaxBuildHeight() - 1 || !level.getBlockState(pos.above()).canBeReplaced(context)) {
			return null;
		}
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HALF, DoubleBlockHalf.LOWER);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		DoubleBlockHalf half = state.getValue(HALF);
		if (direction.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (direction == Direction.UP)) {
			return neighborState.is(this) && neighborState.getValue(HALF) != half ? state : Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			BlockPos lowerPos = pos.below();
			BlockState lower = level.getBlockState(lowerPos);
			if (lower.is(this)) {
				if (!level.isClientSide && !player.isCreative()) {
					Block.dropResources(lower, level, lowerPos, null, player, player.getMainHandItem());
				}
				level.setBlock(lowerPos, Blocks.AIR.defaultBlockState(), 35);
				level.levelEvent(player, 2001, lowerPos, Block.getId(lower));
			}
		}
		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.UPPER ? new SlotMachineBlockEntity(pos, state) : null;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide || state.getValue(HALF) != DoubleBlockHalf.UPPER) {
			return null;
		}
		return createTickerHelper(type, ModBlockEntities.SLOT_MACHINE, SlotMachineBlockEntity::tick);
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

	private SlotMachineBlockEntity machine(Level level, BlockPos pos, BlockState state) {
		BlockPos upper = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();
		return level.getBlockEntity(upper) instanceof SlotMachineBlockEntity machine ? machine : null;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!SlotPayouts.isChip(stack)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if (level.isClientSide) {
			return ItemInteractionResult.SUCCESS;
		}
		SlotMachineBlockEntity machine = machine(level, pos, state);
		if (machine == null || !(level instanceof ServerLevel server)) {
			return ItemInteractionResult.CONSUME;
		}
		if (stack.getCount() < SlotPayouts.BET && !player.getAbilities().instabuild) {
			return ItemInteractionResult.CONSUME;
		}
		if (machine.spinning()) {
			player.displayClientMessage(Component.translatable("message.modcasino.busy"), true);
			return ItemInteractionResult.CONSUME;
		}
		if (machine.start(server, stack.getItem()) && !player.getAbilities().instabuild) {
			stack.shrink(SlotPayouts.BET);
		}
		return ItemInteractionResult.CONSUME;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide) {
			SlotMachineBlockEntity machine = machine(level, pos, state);
			Component message = machine != null && machine.spinning()
					? Component.translatable("message.modcasino.busy")
					: Component.translatable("message.modcasino.insert_coin");
			player.displayClientMessage(message, true);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
