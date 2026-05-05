package com.example.processingmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRegistries {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ProcessingMod.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ProcessingMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ProcessingMod.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ProcessingMod.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ProcessingMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ProcessingMod.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProcessingMod.MODID);

    public static void register(IEventBus modEventBus) {
        // Forza il caricamento delle classi per inizializzare i campi statici (DeferredRegister)
        // Questo garantisce che i blocchi, gli item e i tab vengano registrati sul bus.
        Object dummyBlocks = com.example.processingmod.blocks.ModBlocks.AA_MACHINE;
        Object dummyItems = com.example.processingmod.items.ModItems.AA_MACHINE_ITEM;
        Object dummyTabs = com.example.processingmod.tabs.ModCreativeTabs.PROCESSING_TAB;
        Object dummyRecipes = com.example.processingmod.recipes.ModRecipes.AA_MACHINE_TYPE;
        Object dummyMenus = com.example.processingmod.menus.ModMenus.AA_MACHINE_MENU;

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENUS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
