3package com.example.processingmod.recipes;

import com.example.processingmod.ModRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModRecipes {

    // RecipeType
    public static final DeferredHolder<RecipeType<?>, RecipeType<AAMachineRecipe>> AA_MACHINE_TYPE =
            ModRegistries.RECIPE_TYPES.register("aa_machine", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("processingmod", "aa_machine")
            ));

    // RecipeSerializer
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AAMachineRecipe>> AA_MACHINE_SERIALIZER =
            ModRegistries.RECIPE_SERIALIZERS.register("aa_machine", () ->
                    new net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<>(null) {
                        // Placeholder — sovrascriviamo con AAMachineRecipeSerializer
                        @Override public com.mojang.serialization.MapCodec<AAMachineRecipe> codec() { return AAMachineRecipe.CODEC; }
                        @Override public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AAMachineRecipe> streamCodec() { return AAMachineRecipe.STREAM_CODEC; }
                    }
            );
}
