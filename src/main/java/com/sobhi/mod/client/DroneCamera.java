package com.sobhi.mod.client;

import com.sobhi.mod.entity.EntityDrone;
import com.sobhi.mod.network.ModNetworking;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DroneCamera {
    private static boolean active = false;
    private static Entity originalCameraEntity = null;


    public static boolean isPlayerInDroneMode() {
        return active;
    }

    public static void setDroneMode(boolean arg) {
        active = arg;
    }

    public static void toggleDroneCamera(EntityDrone drone) {
        Minecraft mc = Minecraft.getInstance();

        if (active) {
            // Switch back to player view
            if (originalCameraEntity != null) {
                mc.setCameraEntity(originalCameraEntity);
            }
            active = false;
            setDroneMode(false);
        } else {
            // Save the player's original camera entity (the player)
            originalCameraEntity = mc.getCameraEntity();

            if (originalCameraEntity != null && originalCameraEntity != drone) {
                mc.setCameraEntity(drone); // Switch to drone camera
                active = true;
                setDroneMode(true);
            }
        }
    }

    @SubscribeEvent
    public static void onCameraSetup(net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (active && mc.getCameraEntity() instanceof EntityDrone drone) {
            event.setYaw(drone.getYRot());
            event.setPitch(drone.getXRot());
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END || !active) return;

        if (mc.getCameraEntity() instanceof EntityDrone drone) {
            double moveX = 0, moveY = 0, moveZ = 0;

            if (DroneKeybinds.forward.isDown()) {
                moveZ += 0.2; // Move forward
            }
            if (DroneKeybinds.backward.isDown()) {
                moveZ -= 0.2; // Move backward
            }
            if (DroneKeybinds.left.isDown()) {
                moveX -= 0.2; // Move left
            }
            if (DroneKeybinds.right.isDown()) {
                moveX += 0.2; // Move right
            }
            if (DroneKeybinds.up.isDown()) {
                moveY += 0.2; // Move up
            }
            if (DroneKeybinds.down.isDown()) {
                moveY -= 0.2; // Move down
            }

            if (moveX != 0 || moveY != 0 || moveZ != 0) {
                ModNetworking.sendMovePacket(drone, moveX, moveY, moveZ);
            }
        }
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();

        if (active && DroneKeybinds.dismount.isDown()) {
            toggleDroneCamera((EntityDrone) mc.getCameraEntity()); // Switch back to player
        }
    }
}