package com.example.processingmod.menus;

import com.example.processingmod.blockentities.AAMachineBlockEntity;
import com.example.processingmod.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class AAMachineMenu extends AbstractContainerMenu {

    private final AAMachineBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final ContainerData data;
    private final IItemHandler playerInventory;

    // Costruttore Client (usato dalla registrazione menu)
    public AAMachineMenu(int containerId, Inventory inv, net.minecraft.network.RegistryFriendlyByteBuf buf) {
        this(containerId, inv, inv.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(4));
    }

    // Costruttore Server (chiamato dalla BlockEntity)
    public AAMachineMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenus.AA_MACHINE_MENU.get(), containerId);
        checkContainerSize(inv, 4);
        blockEntity = (AAMachineBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data;
        this.playerInventory = new InvWrapper(inv);

        // Aggiungi gli slot del macchinario
        addMachineSlots();
        // Aggiungi l'inventario del giocatore
        addPlayerInventory();
        // Sincronizza i dati (progressi, energia)
        addDataSlots(data);
    }

    private void addMachineSlots() {
        IItemHandler handler = blockEntity.getItemHandler(null);
        // Slot Input (0 e 1)
        this.addSlot(new SlotItemHandler(handler, 0, 44, 30));
        this.addSlot(new SlotItemHandler(handler, 1, 62, 30));
        // Slot Output (2) - Allineato
        this.addSlot(new SlotItemHandler(handler, 2, 116, 30) {
            @Override public boolean mayPlace(ItemStack stack) { return false; } // Solo output
        });
        // Slot Upgrade (3) - Sotto la freccia
        this.addSlot(new SlotItemHandler(handler, 3, 85, 52));
    }

    private void addPlayerInventory() {
        // Player Inventory
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new SlotItemHandler(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
        // Player Hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new SlotItemHandler(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        int arrowPixelSize = 24; // Dimensione freccia GUI

        if (maxProgress == 0 || progress == 0) return 0;
        return progress * arrowPixelSize / maxProgress;
    }

    public int getScaledEnergy() {
        int energy = data.get(2);
        int maxEnergy = data.get(3);
        int barPixelSize = 64; // Dimensione barra GUI

        if (maxEnergy == 0 || energy == 0) return 0;
        return (int) (((float) energy / maxEnergy) * barPixelSize);
    }

    public int getEnergy() {
        return data.get(2);
    }

    public int getMaxEnergy() {
        return data.get(3);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // 4 slot macchina (0,1,2,3), gli altri 36 sono inv player (4-39)
        if (index < 4) {
            // Dalla macchina al giocatore
            if (!moveItemStackTo(sourceStack, 4, 40, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Dal giocatore alla macchina
            // Se è un upgrade, prova a metterlo nello slot 3
            if (isUpgrade(sourceStack)) {
                if (!moveItemStackTo(sourceStack, 3, 4, false)) {
                    // Se lo slot upgrade è pieno, prova gli input (opzionale)
                    if (!moveItemStackTo(sourceStack, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // Altrimenti prova gli slot input
                if (!moveItemStackTo(sourceStack, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    private boolean isUpgrade(ItemStack stack) {
        return stack.is(com.example.processingmod.items.ModItems.IRON_UPGRADE.get()) ||
               stack.is(com.example.processingmod.items.ModItems.GOLD_UPGRADE.get()) ||
               stack.is(com.example.processingmod.items.ModItems.DIAMOND_UPGRADE.get()) ||
               stack.is(com.example.processingmod.items.ModItems.NETHERITE_UPGRADE.get());
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, ModBlocks.AA_MACHINE.get());
    }
}
