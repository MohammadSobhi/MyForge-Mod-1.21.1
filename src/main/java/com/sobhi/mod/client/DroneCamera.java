package com.sobhi.mod.client;

import com.sobhi.mod.entity.EntityDrone;
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

    public static void toggleDroneCamera(EntityDrone drone) {
        Minecraft mc = Minecraft.getInstance();

        if (active) {
            // Switch back to player view
            if (originalCameraEntity != null) {
                mc.setCameraEntity(originalCameraEntity);
            }
            active = false;
        } else {
            // Save the player's original camera entity (the player)
            originalCameraEntity = mc.getCameraEntity();

            if (originalCameraEntity != null && originalCameraEntity != drone) {
                mc.setCameraEntity(drone); // Switch to drone camera
                active = true;
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
            Vec3 movement = Vec3.ZERO;

            if (DroneKeybinds.forward.isDown()) {
                movement = movement.add(drone.getLookAngle().scale(0.2)); // Move forward
                System.out.println("Moving forward!");
            }
            if (DroneKeybinds.backward.isDown()) {
                movement = movement.add(drone.getLookAngle().scale(-0.2)); // Move backward
                System.out.println("Moving backward!");
            }
            if (DroneKeybinds.left.isDown()) {
                movement = movement.add(drone.getLookAngle().yRot((float) Math.toRadians(90)).scale(0.2)); // Strafe left
                System.out.println("Moving left!");
            }
            if (DroneKeybinds.right.isDown()) {
                movement = movement.add(drone.getLookAngle().yRot((float) Math.toRadians(-90)).scale(0.2)); // Strafe right
                System.out.println("Moving right!");
            }
            if (DroneKeybinds.up.isDown()) {
                movement = movement.add(0, 0.2, 0); // Move up
                System.out.println("Moving up!");
            }
            if (DroneKeybinds.down.isDown()) {
                movement = movement.add(0, -0.2, 0); // Move down
                System.out.println("Moving down!");
            }

            // Update drone position manually
            if (!movement.equals(Vec3.ZERO)) {
                drone.setPos(drone.getX() + movement.x, drone.getY() + movement.y, drone.getZ() + movement.z);
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