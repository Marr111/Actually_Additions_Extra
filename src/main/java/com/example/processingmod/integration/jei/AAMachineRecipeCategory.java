package com.example.processingmod.integration.jei;

import com.example.processingmod.ProcessingMod;
import com.example.processingmod.blocks.ModBlocks;
import com.example.processingmod.recipes.AAMachineRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.minecraft.client.gui.GuiGraphics;

public class AAMachineRecipeCategory implements IRecipeCategory<AAMachineRecipe> {

    public static final RecipeType<AAMachineRecipe> TYPE = RecipeType.create(ProcessingMod.MODID, "aa_machine", AAMachineRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable energyBar;
    private final IDrawable arrow;

    public AAMachineRecipeCategory(IGuiHelper helper) {
        // A blank background where we can draw the slots.
        // Dimension: 150 width, 60 height
        this.background = helper.createBlankDrawable(150, 60);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.AA_MACHINE.get()));
        
        // Sfondo grigio e colori che abbiamo usato in AAMachineScreen
        this.energyBar = helper.drawableBuilder(ResourceLocation.fromNamespaceAndPath(ProcessingMod.MODID, "textures/gui/aa_machine.png"), 0, 0, 0, 0).build(); 
        this.arrow = helper.drawableBuilder(ResourceLocation.fromNamespaceAndPath(ProcessingMod.MODID, "textures/gui/aa_machine.png"), 0, 0, 0, 0).build();
    }

    @Override
    public @NotNull RecipeType<AAMachineRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.processingmod.aa_machine");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull AAMachineRecipe recipe, @NotNull IFocusGroup focuses) {
        // Slot Input 1
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 20)
                .addIngredients(recipe.ingredient1());

        // Slot Input 2
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 20)
                .addIngredients(recipe.ingredient2());

        // Slot Output 1
        builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 20)
                .addItemStack(recipe.result1());

        // Slot Output 2 (se presente)
        if (!recipe.result2().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 40)
                    .addItemStack(recipe.result2());
        }
    }

    @Override
    public void draw(AAMachineRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Disegniamo lo sfondo personalizzato manualmente, stile AAMachineScreen
        // Background generale
        guiGraphics.fill(0, 0, 150, 60, 0xFF2B2B2B);

        // Slot Input 1 (x: 29, y: 19 - size 18x18)
        guiGraphics.fill(29, 19, 47, 37, 0xFF1A1A1A);
        // Slot Input 2 (x: 49, y: 19)
        guiGraphics.fill(49, 19, 67, 37, 0xFF1A1A1A);

        // Arrow background
        guiGraphics.fill(75, 23, 99, 35, 0xFF1A1A1A);
        // Arrow fill (full, since it's JEI)
        guiGraphics.fill(75, 23, 99, 35, 0xFF00BFFF);

        // Slot Output 1 (x: 109, y: 19)
        guiGraphics.fill(109, 19, 127, 37, 0xFF1A1A1A);
        // Slot Output 2 (x: 109, y: 39)
        guiGraphics.fill(109, 39, 127, 57, 0xFF1A1A1A);

        // Barra Energia (x: 5, y: 5, w: 10, h: 50)
        guiGraphics.fill(5, 5, 15, 55, 0xFF1A1A1A);
        guiGraphics.fill(5, 5, 15, 55, 0xFFFE3D3D);

        // Scriviamo il costo in FE
        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, 
                recipe.energyCost() + " FE", 70, 5, 0xFFFFFF, false);
        // Scriviamo il tempo
        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, 
                recipe.processingTime() + " t", 70, 45, 0xAAAAAA, false);
    }
}
