package cl.hakyo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrossPlushBlock extends PlushBlock {
	public static final MapCodec<CrossPlushBlock> CODEC = simpleCodec(CrossPlushBlock::new);

	private static final VoxelShape NORTH = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 13.0D);
	private static final VoxelShape SOUTH = Block.box(0.0D, 0.0D, 3.0D, 16.0D, 16.0D, 12.0D);
	private static final VoxelShape EAST = Block.box(3.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
	private static final VoxelShape WEST = Block.box(4.0D, 0.0D, 0.0D, 13.0D, 16.0D, 16.0D);

	public CrossPlushBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case SOUTH -> SOUTH;
			case EAST -> EAST;
			case WEST -> WEST;
			default -> NORTH;
		};
	}
}
