package com.example.processingmod.integration.jei;

import com.example.processingmod.ProcessingMod;
import com.example.processingmod.blocks.ModBlocks;
import de.ellpeck.actuallyadditions.mod.crafting.EmpowererRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {

    // ID esatto confermato dal bytecode di JEIActuallyAdditionsPlugin: RecipeType.create("actuallyadditions", "empowerer", ...)
    public static final RecipeType<EmpowererRecipe> AA_EMPOWERING_TYPE = RecipeType.create("actuallyadditions", "empowerer", EmpowererRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ProcessingMod.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.AA_MACHINE.get()), AA_EMPOWERING_TYPE);
    }
}
