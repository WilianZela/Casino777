package cl.hakyo.entity;

import cl.hakyo.block.SwivelChairBlockEntity;
import cl.hakyo.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChairSeatEntity extends Entity {
	public static final float SEAT_HEIGHT = 0.5625F;
	private static final float TURN_SPEED = 14.0F;
	private static final float PUSH = 22.0F;
	private static final float MAX_SPIN = 45.0F;
	private static final float FRICTION = 0.94F;
	private static final float MIN_SPIN = 0.6F;
	private static final EntityDataAccessor<Boolean> SPINNING = SynchedEntityData.defineId(ChairSeatEntity.class, EntityDataSerializers.BOOLEAN);

	private float spin;

	public ChairSeatEntity(EntityType<?> type, Level level) {
		super(type, level);
		noPhysics = true;
		setNoGravity(true);
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide) {
			return;
		}
		Entity rider = getFirstPassenger();
		BlockPos pos = blockPosition();
		if ((rider == null && Math.abs(spin) < MIN_SPIN) || !level().getBlockState(pos).is(ModBlocks.SWIVEL_CHAIR)) {
			release(pos);
			return;
		}
		if (Math.abs(spin) >= MIN_SPIN) {
			entityData.set(SPINNING, true);
			setYRot(getYRot() + spin);
			spin *= FRICTION;
			return;
		}
		entityData.set(SPINNING, false);
		spin = 0.0F;
		if (rider == null) {
			return;
		}
		float difference = Mth.wrapDegrees(rider.getYRot() - getYRot());
		setYRot(getYRot() + Mth.clamp(difference, -TURN_SPEED, TURN_SPEED));
	}

	public void push(float direction) {
		float sign = Math.abs(spin) >= MIN_SPIN ? Math.signum(spin) : direction;
		spin = Mth.clamp(spin + sign * PUSH, -MAX_SPIN, MAX_SPIN);
		level().playSound(null, getX(), getY(), getZ(), SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.6F, 1.2F);
	}

	private void release(BlockPos pos) {
		if (level().getBlockEntity(pos) instanceof SwivelChairBlockEntity chair) {
			chair.setRestYaw(getYRot());
		}
		discard();
	}

	@Override
	public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
		return new Vec3(getX(), getY() + 0.2D, getZ());
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return true;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(SPINNING, false);
	}

	public boolean isSpinning() {
		return entityData.get(SPINNING);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
	}
}
