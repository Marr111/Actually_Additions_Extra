package com.example.processingmod.items;

import com.example.processingmod.ModRegistries;
import com.example.processingmod.blocks.ModBlocks;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    // Registriamo il BlockItem per AA_MACHINE.
    // registerSimpleBlockItem è un helper di NeoForge che crea automaticamente un BlockItem per il blocco passato.
    public static final DeferredItem<net.minecraft.world.item.BlockItem> AA_MACHINE_ITEM = ModRegistries.ITEMS.registerSimpleBlockItem("aa_machine", ModBlocks.AA_MACHINE);
}
