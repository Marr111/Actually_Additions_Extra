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

        // Barra energia (Sfondo) — termina a y+70, 2px sopra la label "Inventory" a y+72
        guiGraphics.fill(x + 8, y + 8, x + 20, y + 70, 0xFF1A1A1A);
        // Disegna energia attiva
        int energy = menu.getScaledEnergy();
        if (energy > 0) {
            guiGraphics.fill(x + 8, y + 70 - energy, x + 20, y + 70, 0xFFFE3D3D);
        }

        // Sfondi slot macchina (5 Input)
        renderSlot(guiGraphics, x + 28, y + 26);
        renderSlot(guiGraphics, x + 46, y + 26);
        renderSlot(guiGraphics, x + 64, y + 26);
        renderSlot(guiGraphics, x + 82, y + 26);
        renderSlot(guiGraphics, x + 100, y + 26);

        // Sfondo slot macchina (Output)
        renderSlot(guiGraphics, x + 148, y + 26);

        // Sfondo slot macchina (Upgrade)
        renderSlot(guiGraphics, x + 64, y + 48);

        // Barra del progresso (Sfondo)
        guiGraphics.fill(x + 122, y + 26, x + 144, y + 42, 0xFF1A1A1A);
        // Disegna progresso attivo
        if (menu.isCrafting()) {
            int progress = menu.getScaledProgress();
            guiGraphics.fill(x + 122, y + 26, x + 122 + progress, y + 42, 0xFF00BFFF); // Azzurro/Ciano
        }
    }

    private void renderSlot(GuiGraphics guiGraphics, int itemX, int itemY) {
        int bgX = itemX - 1;
        int bgY = itemY - 1;
        // Bordo sottile (grigio scuro)
        guiGraphics.fill(bgX, bgY, bgX + 18, bgY + 18, 0xFF444444);
        // Interno scuro
        guiGraphics.fill(bgX + 1, bgY + 1, bgX + 17, bgY + 17, 0xFF1A1A1A);
    }

    private void renderEnergyTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (mouseX >= x + 8 && mouseX <= x + 20 && mouseY >= y + 8 && mouseY <= y + 70) {
            Component text = Component.literal("Energy: " + menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE");
            guiGraphics.renderTooltip(this.font, text, mouseX, mouseY);
        }
    }
}
