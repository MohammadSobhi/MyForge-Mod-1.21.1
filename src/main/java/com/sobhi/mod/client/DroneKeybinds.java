package com.sobhi.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public final class DroneKeybinds {


    public static final DroneKeybinds INSTANCE = new DroneKeybinds();
    private DroneKeybinds(){}


    public final KeyMapping example_key = new KeyMapping(
            "key.sobhimod.example_key",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_F9, -1),
            KeyMapping.CATEGORY_GAMEPLAY
    );




}
