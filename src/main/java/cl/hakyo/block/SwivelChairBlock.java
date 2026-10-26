package cl.hakyo.block;

import cl.hakyo.entity.ChairSeatEntity;
import cl.hakyo.registry.ModEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class SwivelChairBlock extends BaseEntityBlock {
	public static final MapCodec<SwivelChairBlock> CODEC = simpleCodec(SwivelChairBlock::new);

	private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 17.0D, 13.0D);
	private static final VoxelShape COLLISION = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 9.0D, 13.0D);

	public SwivelChairBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<SwivelChairBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SwivelChairBlockEntity(pos, state);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof SwivelChairBlockEntity chair && placer != null) {
			chair.setRestYaw(Mth.wrapDegrees(Math.round((placer.getYRot() + 180.0F) / 45.0F) * 45.0F));
		}
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
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return COLLISION;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		if (player.isPassenger() && !(player.getVehicle() instanceof ChairSeatEntity)) {
			return InteractionResult.CONSUME;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.CONSUME;
		}
		List<ChairSeatEntity> seats = server.getEntitiesOfClass(ChairSeatEntity.class, new AABB(pos));
		ChairSeatEntity seat = seats.isEmpty() ? null : seats.get(0);
		boolean riding = seat != null && player.getVehicle() == seat;
		boolean occupied = seat != null && seat.isVehicle();
		if (riding || occupied || player.isShiftKeyDown()) {
			if (seat == null) {
				seat = spawnSeat(server, pos, player);
			}
			Vec3 offset = hitResult.getLocation().subtract(pos.getX() + 0.5D, 0.0D, pos.getZ() + 0.5D);
			double yaw = Math.toRadians(player.getYRot());
			double side = offset.x * -Math.cos(yaw) + offset.z * -Math.sin(yaw);
			seat.push(side > 0.0D ? -1.0F : 1.0F);
			return InteractionResult.CONSUME;
		}
		if (seat == null) {
			seat = spawnSeat(server, pos, player);
		}
		player.startRiding(seat);
		return InteractionResult.CONSUME;
	}

	private static ChairSeatEntity spawnSeat(ServerLevel server, BlockPos pos, Player player) {
		float yaw = server.getBlockEntity(pos) instanceof SwivelChairBlockEntity chair ? chair.restYaw() : player.getYRot();
		ChairSeatEntity seat = new ChairSeatEntity(ModEntities.CHAIR_SEAT, server);
		seat.moveTo(pos.getX() + 0.5D, pos.getY() + ChairSeatEntity.SEAT_HEIGHT - 0.1D, pos.getZ() + 0.5D, yaw, 0.0F);
		server.addFreshEntity(seat);
		return seat;
	}
}
