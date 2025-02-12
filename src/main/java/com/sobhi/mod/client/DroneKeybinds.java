package com.sobhi.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.List;

public final class DroneKeybinds {



    public static final List<KeyMapping> list = new LinkedList<>();
    public static final KeyMapping left, right, forward, backward, up, down ;
    public static final KeyMapping dismount, use;

    static {
        // Create keybindings
        left = newKey("left", GLFW.GLFW_KEY_A);
        right = newKey("right", GLFW.GLFW_KEY_D);
        forward = newKey("forward", GLFW.GLFW_KEY_W);
        backward = newKey("backward", GLFW.GLFW_KEY_S);
        up = newKey("up", GLFW.GLFW_KEY_SPACE);
        down = newKey("down", GLFW.GLFW_KEY_LEFT_SHIFT);


        use = newKey("use", GLFW.GLFW_MOUSE_BUTTON_2, InputConstants.Type.MOUSE);

        dismount = newKey("dismount", GLFW.GLFW_KEY_R);

    }

    private static KeyMapping newKey(String name, int keyCode) {
        return newKey(name, keyCode, InputConstants.Type.KEYSYM);
    }

    // Helper method to create a keybinding with a specific input type (e.g., mouse or keyboard)
    private static KeyMapping newKey(String name, int keyCode, InputConstants.Type type) {
        KeyMapping key = new KeyMapping(
                "key.sobhimod." + name, // Translation key for the keybinding name
                KeyConflictContext.IN_GAME, // Context in which the keybinding is active
                KeyModifier.NONE, // No modifier (e.g., Ctrl, Shift)
                type, // Input type (KEY for keyboard, MOUSE for mouse)
                keyCode, // Default key code
                "category.sobhimod.drone_tab" // Category for the keybinding in the controls menu
        );
        list.add(key);
        return key;
    }



}
