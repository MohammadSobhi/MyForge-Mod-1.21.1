package com.sobhi.mod.entity;

import com.mojang.serialization.Codec;
import com.sobhi.mod.MyMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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




    // Synced entity data
    private static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID =
            SynchedEntityData.defineId(EntityDrone.class, EntityDataSerializers.BYTE);


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

        // Define custom data
        //builder.define(BOOSTING, false);
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

            // Movement calculations - use proper input methods
            float forward = player.zza;  // Forward impulse (W/S keys)
            float strafe = player.xxa;   // Strafe impulse (A/D keys)
            float vertical = 0;


            if (player.isShiftKeyDown()) { // Sneaking state (shift key)
                vertical -= 0.15f;
            }

            Vec3 motion = new Vec3(strafe, vertical, forward)
                    .yRot(-this.getYRot() * ((float) Math.PI / 180F))
                    .scale(0.25f);

            this.setDeltaMovement(motion);
        }
    }


    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (!this.level().isClientSide && player.getVehicle() == null) {

            //System.out.println("started riding");
            return player.startRiding(this) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }


    public boolean isRideable() {
        //System.out.println("isRideable() called, returning false");
        return false; // Make entity rideable
    }






    @Override
    public void tick() {
        super.tick();
        // Update movement/rotation

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
