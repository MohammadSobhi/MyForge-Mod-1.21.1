package com.sobhi.mod.item;

import com.sobhi.mod.MyMod;
import com.sobhi.mod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MyMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> example_tab = CREATIVE_MODE_TABS.register("example_tab",
            ()-> CreativeModeTab.builder().icon(()-> new ItemStack(ModItems.example_item.get()))
                    .title(Component.translatable("creative.tab.items"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.example_item.get());
                        pOutput.accept(ModBlocks.example_block.get());
                        pOutput.accept(ModItems.custom_item.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
