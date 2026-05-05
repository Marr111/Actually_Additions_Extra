package com.example.processingmod.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Wrapper che fornisce al RecipeManager NeoForge 1.21.1 i due slot di input
 * della AA Machine come RecipeInput standard.
 */
public record AAMachineRecipeInput(ItemStack slot0, ItemStack slot1, ItemStack slot2, ItemStack slot3, ItemStack slot4) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> slot0;
            case 1 -> slot1;
            case 2 -> slot2;
            case 3 -> slot3;
            case 4 -> slot4;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 5;
    }

    public static AAMachineRecipeInput of(net.neoforged.neoforge.items.ItemStackHandler handler) {
        return new AAMachineRecipeInput(
                handler.getStackInSlot(0),
                handler.getStackInSlot(1),
                handler.getStackInSlot(2),
                handler.getStackInSlot(3),
                handler.getStackInSlot(4)
        );
    }
}
