package com.example.processingmod.screens;

import com.example.processingmod.ProcessingMod;
import com.example.processingmod.menus.AAMachineMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class AAMachineScreen extends AbstractContainerScreen<AAMachineMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ProcessingMod.MODID, "textures/gui/aa_machine.png");

    public AAMachineScreen(AAMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        renderEnergyTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        // Sfondo principale (Grigio scuro con bordo più chiaro)
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF555555); // Bordo esterno
        guiGraphics.fill(x + 1, y + 1, x + this.imageWidth - 1, y + this.imageHeight - 1, 0xFF2B2B2B); // Interno scuro

        // Sfondi inventario giocatore (Grid)
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                int slotX = x + 8 + l * 18 - 1;
                int slotY = y + 84 + i * 18 - 1;
                // Bordo sottile (scuro)
                guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF444444);
                // Fondo scuro
                guiGraphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF1A1A1A);
            }
        }
        // Sfondi hotbar giocatore (Grid)
        for (int i = 0; i < 9; ++i) {
            int slotX = x + 8 + i * 18 - 1;
            int slotY = y + 142 - 1;
            // Bordo sottile (scuro)
            guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF444444);
            // Fondo scuro
            guiGraphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF1A1A1A);
        }

        // Sfondi slot macchina (Input)
        renderSlot(guiGraphics, x + 43, y + 29);
        renderSlot(guiGraphics, x + 61, y + 29);
        // Sfondo slot macchina (Output) - Allineato
        renderSlot(guiGraphics, x + 115, y + 29);

        // Sfondo slot macchina (Upgrade) - Sotto la freccia
        renderSlot(guiGraphics, x + 84, y + 51);

        // Barra del progresso (Sfondo)
        guiGraphics.fill(x + 85, y + 31, x + 109, y + 47, 0xFF1A1A1A);
        // Disegna progresso attivo
        if (menu.isCrafting()) {
            int progress = menu.getScaledProgress();
            guiGraphics.fill(x + 85, y + 31, x + 85 + progress, y + 47, 0xFF00BFFF); // Azzurro/Ciano
        }

        // Barra energia (Sfondo)
        guiGraphics.fill(x + 10, y + 6, x + 20, y + 70, 0xFF1A1A1A);
        // Disegna energia attiva
        int energy = menu.getScaledEnergy();
        if (energy > 0) {
            guiGraphics.fill(x + 10, y + 70 - energy, x + 20, y + 70, 0xFFFE3D3D); // Rosso scuro energia
        }
    }

    private void renderSlot(GuiGraphics guiGraphics, int x, int y) {
        // Bordo sottile (grigio scuro)
        guiGraphics.fill(x, y, x + 18, y + 18, 0xFF444444);
        // Interno scuro
        guiGraphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF1A1A1A);
    }

    private void renderEnergyTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (mouseX >= x + 10 && mouseX <= x + 20 && mouseY >= y + 6 && mouseY <= y + 70) {
            Component text = Component.literal("Energy: " + menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE");
            guiGraphics.renderTooltip(this.font, text, mouseX, mouseY);
        }
    }
}
