package com.sobhi.mod.entity;

import com.sobhi.mod.MyMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MyMod.MOD_ID);

    public static final RegistryObject<EntityType<EntityDrone>> DRONE = ENTITIES.register(
            "drone",
            () -> EntityType.Builder.of(EntityDrone::new, MobCategory.MISC)
                    .sized(0.8f, 0.6f) //
                    .build("drone")
    );
}
