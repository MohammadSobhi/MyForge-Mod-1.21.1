package com.sobhi.mod.client;


import com.sobhi.mod.MyMod;
import com.sobhi.mod.client.model.DroneModel;
import com.sobhi.mod.entity.EntityDrone;
import com.sobhi.mod.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MyMod.MOD_ID , bus = Mod.EventBusSubscriber.Bus.MOD , value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DroneModel.LAYER_LOCATION, DroneModel::createBodyLayer);
    }


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DRONE.get(), EntityDrone.createAttributes().build());
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event){
        event.enqueueWork(()-> {

        });
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(DroneKeybinds.INSTANCE.example_key);

    }
}
