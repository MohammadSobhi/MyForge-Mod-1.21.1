package com.sobhi.mod.network;

import com.sobhi.mod.MyMod;
import com.sobhi.mod.entity.EntityDrone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class ModNetworking {

    public static final SimpleChannel CHANNEL = ChannelBuilder.named(
            ResourceLocation.fromNamespaceAndPath(MyMod.MOD_ID, "main"))
            .serverAcceptedVersions((status, version) -> true)
            .clientAcceptedVersions((status, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();


    public static void register() {
        // Packet registration will go here
        int id = 0;
        CHANNEL.messageBuilder(DroneMovePacket.class,id ++)
                .encoder(DroneMovePacket::encode)
                .decoder(DroneMovePacket::decode)
                .consumerMainThread(DroneMovePacket::handle)
                .add();
    }


    public static void sendMovePacket(EntityDrone drone, double moveX, double moveY, double moveZ) {
        CHANNEL.send(new DroneMovePacket(drone.getId(), moveX, moveY, moveZ), PacketDistributor.SERVER.noArg());

    }



    public static void sendToServer(Object msg){
        CHANNEL.send(msg, PacketDistributor.SERVER.noArg());
    }

    public static void sendToPlayer(Object msg, ServerPlayer player){
        CHANNEL.send(msg, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToAllClients(Object msg){
        CHANNEL.send(msg, PacketDistributor.ALL.noArg());
    }



}
