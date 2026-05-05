package com.example.processingmod.tabs;

import com.example.processingmod.ModRegistries;
import com.example.processingmod.items.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModCreativeTabs {
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PROCESSING_TAB = ModRegistries.CREATIVE_MODE_TABS.register("processing_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.processingmod.processing_tab")) // Traduzione nel file lang
            .icon(() -> ModItems.AA_MACHINE_ITEM.get().getDefaultInstance()) // Icona del tab
            .displayItems((parameters, output) -> {
                // Qui aggiungiamo gli item al tab
                output.accept(ModItems.AA_MACHINE_ITEM.get());
                output.accept(ModItems.IRON_UPGRADE.get());
                output.accept(ModItems.GOLD_UPGRADE.get());
                output.accept(ModItems.DIAMOND_UPGRADE.get());
                output.accept(ModItems.NETHERITE_UPGRADE.get());
                output.accept(ModItems.ELITE_COIL.get());
            }).build());
}
