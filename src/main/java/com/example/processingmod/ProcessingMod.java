package com.example.processingmod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(ProcessingMod.MODID)
public class ProcessingMod {
    public static final String MODID = "processingmod";

    public ProcessingMod(IEventBus modEventBus, ModContainer modContainer) {
        // Forza il caricamento delle classi per registrare i blocchi/blockentities prima dell'avvio
        Object dummyBlocks = com.example.processingmod.blocks.ModBlocks.AA_MACHINE;
        Object dummyBEs = com.example.processingmod.blockentities.ModBlockEntities.AA_MACHINE_BE;

        ModRegistries.register(modEventBus);
        
        modEventBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                com.example.processingmod.blockentities.ModBlockEntities.AA_MACHINE_BE.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side)
        );
        
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                com.example.processingmod.blockentities.ModBlockEntities.AA_MACHINE_BE.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage(side)
        );
    }

    @net.neoforged.fml.common.EventBusSubscriber(modid = MODID, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD, value = net.neoforged.api.distmarker.Dist.CLIENT)
    public static class ClientModEvents {
        @net.neoforged.bus.api.SubscribeEvent
        public static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
            event.register(com.example.processingmod.menus.ModMenus.AA_MACHINE_MENU.get(), com.example.processingmod.screens.AAMachineScreen::new);
        }
    }
}
