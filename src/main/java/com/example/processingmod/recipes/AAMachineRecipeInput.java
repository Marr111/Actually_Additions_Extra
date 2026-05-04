package com.example.processingmod.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Wrapper che fornisce al RecipeManager NeoForge 1.21.1 i due slot di input
 * della AA Machine come RecipeInput standard.
 */
public record AAMachineRecipeInput(ItemStack slot0, ItemStack slot1) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> slot0;
            case 1 -> slot1;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }

    public static AAMachineRecipeInput of(net.neoforged.neoforge.items.ItemStackHandler handler) {
        return new AAMachineRecipeInput(
                handler.getStackInSlot(0),
                handler.getStackInSlot(1)
        );
    }
}
