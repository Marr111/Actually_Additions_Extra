package com.example.processingmod.blockentities;

import com.example.processingmod.ModRegistries;
import com.example.processingmod.blocks.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AAMachineBlockEntity>> AA_MACHINE_BE =
            ModRegistries.BLOCK_ENTITIES.register("aa_machine", () ->
                    BlockEntityType.Builder.of(AAMachineBlockEntity::new, ModBlocks.AA_MACHINE.get()).build(null));
}
