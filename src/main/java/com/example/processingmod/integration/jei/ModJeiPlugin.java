package com.example.processingmod.integration.jei;

import com.example.processingmod.ProcessingMod;
import com.example.processingmod.blocks.ModBlocks;
import com.example.processingmod.recipes.AAMachineRecipe;
import com.example.processingmod.recipes.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ProcessingMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AAMachineRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level != null) {
            List<AAMachineRecipe> recipes = Minecraft.getInstance().level.getRecipeManager()
                    .getAllRecipesFor(ModRecipes.AA_MACHINE_TYPE.get())
                    .stream()
                    .map(RecipeHolder::value)
                    .toList();

            registration.addRecipes(AAMachineRecipeCategory.TYPE, recipes);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.AA_MACHINE.get()), AAMachineRecipeCategory.TYPE);
    }
}
