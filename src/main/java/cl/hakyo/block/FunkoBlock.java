package cl.hakyo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FunkoBlock extends PlushBlock {
	public static final MapCodec<FunkoBlock> CODEC = simpleCodec(FunkoBlock::new);

	private static final VoxelShape NORTH_SOUTH = Block.box(2.0D, 0.0D, 4.0D, 14.0D, 14.0D, 12.0D);
	private static final VoxelShape EAST_WEST = Block.box(4.0D, 0.0D, 2.0D, 12.0D, 14.0D, 14.0D);

	public FunkoBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case EAST, WEST -> EAST_WEST;
			default -> NORTH_SOUTH;
		};
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return InteractionResult.PASS;
	}
}
