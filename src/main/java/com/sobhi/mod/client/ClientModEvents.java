package com.sobhi.mod.client;


import com.sobhi.mod.MyMod;
import com.sobhi.mod.client.model.DroneModel;
import com.sobhi.mod.entity.EntityDrone;
import com.sobhi.mod.entity.ModEntities;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.sobhi.mod.client.render.DroneRenderer;

@Mod.EventBusSubscriber(modid = MyMod.MOD_ID , bus = Mod.EventBusSubscriber.Bus.MOD , value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DroneModel.LAYER_LOCATION, DroneModel::createBodyLayer);
    }



    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(ModEntities.DRONE.get(), DroneRenderer::new);
        });
    }


    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        // Register all keybindings
        for (KeyMapping key : DroneKeybinds.list) {
            event.register(key);
        }}
}
