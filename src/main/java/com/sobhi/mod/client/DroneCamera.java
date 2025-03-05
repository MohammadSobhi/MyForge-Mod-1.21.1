package com.sobhi.mod.client;

import com.sobhi.mod.entity.EntityDrone;
import com.sobhi.mod.network.ModNetworking;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MouseHandler;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DroneCamera {
    private static boolean active = false;
    private static Entity originalCameraEntity = null;
    private static double lastMouseX = 0;
    private static double lastMouseY = 0;
    private static float storedPlayerYaw = 0;
    private static float storedPlayerPitch = 0;


    public static boolean isPlayerInDroneMode() {
        return active;
    }

    public static void setDroneMode(boolean arg) {
        active = arg;
    }

    public static void toggleDroneCamera(EntityDrone drone) {
        Minecraft mc = Minecraft.getInstance();

        if (active) {
            // Switch back to player view and restore player's rotation.
            if (originalCameraEntity != null) {
                mc.setCameraEntity(originalCameraEntity);
            }
            if (mc.player != null) {
                mc.player.setYRot(storedPlayerYaw);
                mc.player.setXRot(storedPlayerPitch);
                mc.player.yHeadRot = storedPlayerYaw;
                mc.player.yBodyRot = storedPlayerYaw;
            }
            active = false;
            setDroneMode(false);
        } else {
            // Save the player's original camera entity and rotation.
            originalCameraEntity = mc.getCameraEntity();
            if (mc.player != null) {
                storedPlayerYaw = mc.player.getYRot();
                storedPlayerPitch = mc.player.getXRot();
            }
            if (originalCameraEntity != null && originalCameraEntity != drone) {
                mc.setCameraEntity(drone); // Switch to drone camera
                active = true;
                setDroneMode(true);
            }
        }
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
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

            // Rotate drone body using mouse movement.
            MouseHandler mouseHandler = mc.mouseHandler;

            double deltaX = mouseHandler.accumulatedDX;
            double deltaY = mouseHandler.accumulatedDY;

            if (deltaX != 0 || deltaY != 0) {
                float sensitivity = 0.15f;
                float newYaw = drone.getYRot() + (float) deltaX * sensitivity;
                float newPitch = Mth.clamp(drone.getXRot() - (float) deltaY * sensitivity, -90, 90);

                drone.setYRot(newYaw);
                drone.setXRot(newPitch);
                // Optionally, send the rotation packet to the server:
                // ModNetworking.sendRotationPacket(drone, newYaw, newPitch);

                // Reset accumulated mouse deltas.
                mouseHandler.accumulatedDX = 0.0;
                mouseHandler.accumulatedDY = 0.0;
            }

            // Prevent the player model from rotating by resetting the player's rotation.
            if (mc.player != null) {
                mc.player.setYRot(storedPlayerYaw);
                mc.player.setXRot(storedPlayerPitch);
                mc.player.yHeadRot = storedPlayerYaw;
                mc.player.yBodyRot = storedPlayerYaw;
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

