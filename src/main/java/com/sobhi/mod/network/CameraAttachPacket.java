package com.sobhi.mod.network;

import com.sobhi.mod.entity.EntityDrone;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

public class CameraAttachPacket {
    private final int entityId;
    private final boolean attach;

    public CameraAttachPacket(int entityId, boolean attach) {
        this.entityId = entityId;
        this.attach = attach;
    }

    public static void encode(CameraAttachPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entityId);
        buffer.writeBoolean(packet.attach);
    }

    public static CameraAttachPacket decode(FriendlyByteBuf buffer) {
        return new CameraAttachPacket(buffer.readInt(), buffer.readBoolean());
    }

    public static void handle(CameraAttachPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            // Use context's built-in client check
            if (ctx.isClientSide()) {
                Minecraft minecraft = Minecraft.getInstance();
                Entity entity = minecraft.level.getEntity(packet.entityId);
                if (entity instanceof EntityDrone) {
                    // Use Minecraft's built-in camera system
                    minecraft.setCameraEntity(packet.attach ? entity : minecraft.player);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
