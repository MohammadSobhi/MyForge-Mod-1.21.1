package com.sobhi.mod.client;


import com.sobhi.mod.MyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MyMod.MOD_ID , bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        if(DroneKeybinds.forward.consumeClick() && minecraft.player != null){
            minecraft.player.displayClientMessage(Component.literal("Key Pressed Successfully"),false);
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new DroneInputHandler());
    }


    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (DroneCamera.isPlayerInDroneMode()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (DroneCamera.isPlayerInDroneMode()) {
            event.setCanceled(true);
        }
    }


    // Cancel right-click block interactions
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (DroneCamera.isPlayerInDroneMode()) {
            event.setCanceled(true);
        }
    }

    // Cancel left-click attacks (hitting entities)
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (DroneCamera.isPlayerInDroneMode()) {
            event.setCanceled(true);
        }
    }



}
