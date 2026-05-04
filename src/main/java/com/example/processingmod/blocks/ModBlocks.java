package com.example.processingmod.blocks;

import com.example.processingmod.ModRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlocks {
    public static final DeferredBlock<Block> AA_MACHINE = ModRegistries.BLOCKS.register("aa_machine", 
            () -> new AAMachineBlock(BlockBehaviour.Properties.of()
                    .strength(3.5f)
                    .requiresCorrectToolForDrops()));
}
