package com.example.processingmod.items;

import com.example.processingmod.ModRegistries;
import com.example.processingmod.blocks.ModBlocks;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    // Registriamo il BlockItem per AA_MACHINE.
    // registerSimpleBlockItem è un helper di NeoForge che crea automaticamente un BlockItem per il blocco passato.
    public static final DeferredItem<net.minecraft.world.item.BlockItem> AA_MACHINE_ITEM = ModRegistries.ITEMS.registerSimpleBlockItem("aa_machine", ModBlocks.AA_MACHINE);

    public static final DeferredItem<Item> IRON_UPGRADE = ModRegistries.ITEMS.register("iron_upgrade", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_UPGRADE = ModRegistries.ITEMS.register("gold_upgrade", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_UPGRADE = ModRegistries.ITEMS.register("diamond_upgrade", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_UPGRADE = ModRegistries.ITEMS.register("netherite_upgrade", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> DIAMOND_NUGGET = ModRegistries.ITEMS.register("diamond_nugget", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ELITE_COIL = ModRegistries.ITEMS.register("elite_coil", () -> new Item(new Item.Properties()));
}
