package com.example.processingmod.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

// In NeoForge 1.21.1 le ricette implementano Recipe<RecipeInput>
// ma per ricette custom di macchine si usa un wrapper semplice
public record AAMachineRecipe(
        Ingredient ingredient1,
        Ingredient ingredient2,
        ItemStack result1,
        ItemStack result2,
        int energyCost,
        int processingTime
) implements Recipe<AAMachineRecipeInput> {

    // ---------------------------------------------------------------
    // Codec (NeoForge 1.21.1 usa Codec al posto di fromJson obsoleto)
    // ---------------------------------------------------------------
    public static final MapCodec<AAMachineRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient1").forGetter(AAMachineRecipe::ingredient1),
            Ingredient.CODEC.fieldOf("ingredient2").forGetter(AAMachineRecipe::ingredient2),
            ItemStack.OPTIONAL_CODEC.fieldOf("result1").forGetter(AAMachineRecipe::result1),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("result2", ItemStack.EMPTY).forGetter(AAMachineRecipe::result2),
            Codec.INT.fieldOf("energy_cost").forGetter(AAMachineRecipe::energyCost),
            Codec.INT.fieldOf("processing_time").forGetter(AAMachineRecipe::processingTime)
    ).apply(instance, AAMachineRecipe::new));

    // StreamCodec per la sincronizzazione client-server via rete
    public static final StreamCodec<RegistryFriendlyByteBuf, AAMachineRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, AAMachineRecipe::ingredient1,
                    Ingredient.CONTENTS_STREAM_CODEC, AAMachineRecipe::ingredient2,
                    ItemStack.OPTIONAL_STREAM_CODEC, AAMachineRecipe::result1,
                    ItemStack.OPTIONAL_STREAM_CODEC, AAMachineRecipe::result2,
                    net.minecraft.network.codec.ByteBufCodecs.INT, AAMachineRecipe::energyCost,
                    net.minecraft.network.codec.ByteBufCodecs.INT, AAMachineRecipe::processingTime,
                    AAMachineRecipe::new
            );

    // ---------------------------------------------------------------
    // Implementazione Recipe<AAMachineRecipeInput>
    // ---------------------------------------------------------------

    @Override
    public boolean matches(@NotNull AAMachineRecipeInput input, @NotNull Level level) {
        ItemStack slot0 = input.getItem(0);
        ItemStack slot1 = input.getItem(1);

        // Accetta gli ingredienti in qualsiasi ordine tra slot 0 e slot 1
        boolean directMatch  = ingredient1.test(slot0) && ingredient2.test(slot1);
        boolean reverseMatch = ingredient1.test(slot1) && ingredient2.test(slot0);
        return directMatch || reverseMatch;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull AAMachineRecipeInput input, HolderLookup.@NotNull Provider registries) {
        return result1.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return result1.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.AA_MACHINE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.AA_MACHINE_TYPE.get();
    }

    // ---------------------------------------------------------------
    // Helper: verifica spazio negli slot output dell'inventario
    // ---------------------------------------------------------------
    public boolean canOutput(ItemStackHandler inventory) {
        // Slot 2: result1 (obbligatorio)
        if (!canFitInSlot(inventory, 2, result1)) return false;
        // Slot 3: result2 (opzionale)
        if (!result2.isEmpty() && !canFitInSlot(inventory, 3, result2)) return false;
        return true;
    }

    private boolean canFitInSlot(ItemStackHandler inventory, int slot, ItemStack stack) {
        ItemStack existing = inventory.getStackInSlot(slot);
        if (existing.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(existing, stack)) return false;
        return existing.getCount() + stack.getCount() <= existing.getMaxStackSize();
    }
}
