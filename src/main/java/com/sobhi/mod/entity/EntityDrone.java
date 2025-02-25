package com.sobhi.mod.entity;

import com.mojang.serialization.Codec;
import com.sobhi.mod.MyMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EntityDrone extends Entity{



    private long lastInteractionTick = -1;
    // Synced entity data
    private static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID =
            SynchedEntityData.defineId(EntityDrone.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> MOVING_FORWARD = SynchedEntityData.defineId(EntityDrone.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MOVING_BACKWARD = SynchedEntityData.defineId(EntityDrone.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MOVING_LEFT = SynchedEntityData.defineId(EntityDrone.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MOVING_RIGHT = SynchedEntityData.defineId(EntityDrone.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MOVING_UP = SynchedEntityData.defineId(EntityDrone.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MOVING_DOWN = SynchedEntityData.defineId(EntityDrone.class, EntityDataSerializers.BOOLEAN);


    // Movement parameters
    private float moveSpeed = 0.5f;
    private float verticalSpeed = 0.1f;

    public EntityDrone(EntityType<?> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static EntityType<EntityDrone> createType() {
        return EntityType.Builder.<EntityDrone>of(EntityDrone::new, MobCategory.MISC)
                .sized(0.8f, 0.6f)
                .build("drone");
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // Define base entity flags first (required)
        builder.define(DATA_SHARED_FLAGS_ID, (byte)0);
        builder.define(MOVING_FORWARD, false);
        builder.define(MOVING_BACKWARD, false);
        builder.define(MOVING_LEFT, false);
        builder.define(MOVING_RIGHT, false);
        builder.define(MOVING_UP, false);
        builder.define(MOVING_DOWN, false);

        // Define custom data
        //builder.define(BOOSTING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (MOVING_FORWARD.equals(data) ||
                MOVING_BACKWARD.equals(data) ||
                MOVING_LEFT.equals(data) ||
                MOVING_RIGHT.equals(data) ||
                MOVING_UP.equals(data) ||
                MOVING_DOWN.equals(data)) {
            this.updateMovement();
        }
        super.onSyncedDataUpdated(data);
    }

    // Add these required base entity methods
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            // Position rider 0.5 blocks above drone center using the callback
            double yOffset = this.getY() + 0.5;
            callback.accept(passenger, this.getX(), yOffset, this.getZ());
        }
    }

    @Override
    public void rideTick() {
        super.rideTick();

        if (this.getControllingPassenger() instanceof Player player) {
            // Rotation control
            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * 0.5f);

            // Movement calculations using custom keys
            float forward = 0.0f;
            float strafe = 0.0f;
            float vertical = 0.0f;

            if (isMovingForward()) forward += 1.0f;
            if (isMovingBackward()) forward -= 1.0f;
            //if (isMovingLeft()) strafe += 1.0f;
            //if (isMovingRight()) strafe -= 1.0f;
            //if (isMovingUp()) vertical += 1.0f;
            //if (isMovingDown()) vertical -= 1.0f;

            Vec3 motion = new Vec3(strafe, vertical, forward)
                    .yRot(-this.getYRot() * ((float) Math.PI / 180F))
                    .scale(moveSpeed);

            this.setDeltaMovement(motion);
        }
    }


    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {

        if (!this.level().isClientSide) {

            long currentTick = this.level().getGameTime();

            // Check if the interaction is within the same tick
            if (currentTick == lastInteractionTick) {
                return InteractionResult.PASS; // Skip processing
            }
            lastInteractionTick = currentTick;


            Vec3 oldPos = this.position();
            System.out.println("old position:"+oldPos);
            Vec3 forward = this.getForward().scale(1.0);
            System.out.println("new pos"+forward);
            this.setPos(oldPos.add(forward));


            Vec3 delta = this.position().subtract(oldPos);
            short deltaX = (short) (delta.x * 4096);
            short deltaY = (short) (delta.y * 4096);
            short deltaZ = (short) (delta.z * 4096);

            boolean useTeleportPacket = Math.abs(delta.x) >= 8 || Math.abs(delta.y) >= 8 || Math.abs(delta.z) >= 8;

            byte yRot = (byte) ((this.getYRot() % 360.0F) * 256.0F / 360.0F);
            byte xRot = (byte) ((this.getXRot() % 360.0F) * 256.0F / 360.0F);
            boolean onGround = this.onGround();

            if (this.level() instanceof ServerLevel serverLevel) {
                ServerChunkCache chunkSource = serverLevel.getChunkSource();
                Entity entity = this;

                if (useTeleportPacket) {
                    chunkSource.chunkMap.broadcast(entity, new ClientboundTeleportEntityPacket(entity));
                } else {
                    // Use PosRot subclass for position + rotation updates
                    ClientboundMoveEntityPacket.PosRot packet = new ClientboundMoveEntityPacket.PosRot(
                            entity.getId(),
                            deltaX,
                            deltaY,
                            deltaZ,
                            yRot,
                            xRot,
                            onGround
                    );
                    chunkSource.chunkMap.broadcast(entity, packet);
                }
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }


    public boolean isRideable() {
        //System.out.println("isRideable() called, returning false");
        return false; // Make entity rideable
    }


    public boolean isMovingForward() {
        return this.entityData.get(MOVING_FORWARD);
    }

    public void setMovingForward(boolean moving) {
        this.entityData.set(MOVING_FORWARD, moving);
        System.out.println("MOVING_FORWARD");
    }

    public boolean isMovingBackward() {
        return this.entityData.get(MOVING_BACKWARD);
    }

    public void setMovingBackward(boolean moving) {
        this.entityData.set(MOVING_BACKWARD, moving);
    }



    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) { // Server-side only
            if (this.getControllingPassenger() != null) {
                updateMovement();
            }
        }
        // Update movement/rotation


    }


    private void updateMovement() {
        float forward = 0.0f;
        float strafe = 0.0f;
        float vertical = 0.0f;

        if (this.entityData.get(MOVING_FORWARD)) forward += 1.0f;
        if (this.entityData.get(MOVING_BACKWARD)) forward -= 1.0f;
        if (this.entityData.get(MOVING_LEFT)) strafe += 1.0f;
        if (this.entityData.get(MOVING_RIGHT)) strafe -= 1.0f;
        if (this.entityData.get(MOVING_UP)) vertical += 1.0f;
        if (this.entityData.get(MOVING_DOWN)) vertical -= 1.0f;

        Vec3 motion = new Vec3(strafe, vertical, forward)
                .yRot(-this.getYRot() * ((float) Math.PI / 180F))
                .scale(moveSpeed);

        this.setDeltaMovement(motion);
    }

    private void handleControls(Player player) {
        // WASD movement implementation
        Vec3 movement = new Vec3(
                player.xxa * moveSpeed,
                0,
                player.zza * moveSpeed
        ).yRot(-player.getYRot() * ((float) Math.PI / 180F));

        this.setDeltaMovement(movement);
    }

    @Override
    public boolean isPickable() {
        return true;
    }


}
