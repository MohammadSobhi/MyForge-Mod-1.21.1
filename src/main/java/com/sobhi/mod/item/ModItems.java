package com.sobhi.mod.item;

import com.sobhi.mod.MyMod;
import com.sobhi.mod.item.custom.CustomItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MyMod.MOD_ID);


    // all things I want to register will be public static final, convention
    // name shouldn't have spaces and only low case characters
    public static final RegistryObject<Item> example_item = ITEMS.register("example_item" ,
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> custom_item = ITEMS.register("custom_item" ,
            () -> new CustomItem(new Item.Properties().durability(32)));

    public static void register (IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
