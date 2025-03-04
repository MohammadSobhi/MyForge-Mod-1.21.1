package com.sobhi.mod.client.render;


import com.sobhi.mod.client.DroneCamera;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DroneCameraHandler {

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {

        // Check if the player is in drone mode (replace this with your actual condition)
        if (DroneCamera.isPlayerInDroneMode()) {
            event.setCanceled(true); // Prevents the hand from rendering
        }
    }

}
