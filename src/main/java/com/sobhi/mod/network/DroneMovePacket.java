package com.sobhi.mod.network;

import com.sobhi.mod.entity.EntityDrone;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class DroneMovePacket {
    private final int droneId;
    private final double moveX, moveY, moveZ;

    public DroneMovePacket(int droneId, double moveX, double moveY, double moveZ) {
        this.droneId = droneId;
        this.moveX = moveX;
        this.moveY = moveY;
        this.moveZ = moveZ;
    }

    public static void encode(DroneMovePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.droneId);
        buf.writeDouble(msg.moveX);
        buf.writeDouble(msg.moveY);
        buf.writeDouble(msg.moveZ);
    }

    public static DroneMovePacket decode(FriendlyByteBuf buf) {
        return new DroneMovePacket(buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handle(DroneMovePacket msg, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            Entity entity = player.level().getEntity(msg.droneId);
            if (entity instanceof EntityDrone drone) {
                drone.setPos(drone.getX() + msg.moveX, drone.getY() + msg.moveY, drone.getZ() + msg.moveZ);
            }
        });
        ctx.setPacketHandled(true);
    }
}

