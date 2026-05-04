package com.example.processingmod.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Serializer per AAMachineRecipe.
 * In NeoForge 1.21.1 il Serializer espone un MapCodec e uno StreamCodec:
 * - MapCodec: usato per leggere/scrivere il file JSON della ricetta
 * - StreamCodec: usato per la sincronizzazione client-server via rete
 * Non c'è più fromJson() né toNetwork() espliciti.
 */
public class AAMachineRecipeSerializer implements RecipeSerializer<AAMachineRecipe> {

    @Override
    public @NotNull MapCodec<AAMachineRecipe> codec() {
        return AAMachineRecipe.CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, AAMachineRecipe> streamCodec() {
        return AAMachineRecipe.STREAM_CODEC;
    }
}
