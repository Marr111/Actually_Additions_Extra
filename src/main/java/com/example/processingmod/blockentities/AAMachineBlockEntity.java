package com.example.processingmod.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import de.ellpeck.actuallyadditions.mod.crafting.ActuallyRecipes;
import de.ellpeck.actuallyadditions.mod.crafting.EmpowererRecipe;

import java.util.Optional;

public class AAMachineBlockEntity extends BlockEntity implements MenuProvider {

    // Inventario con 7 slot (0-4 input, 5 output, 6 upgrade)
    public final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 6) { // Upgrade
                return stack.is(com.example.processingmod.items.ModItems.IRON_UPGRADE.get()) ||
                       stack.is(com.example.processingmod.items.ModItems.GOLD_UPGRADE.get()) ||
                       stack.is(com.example.processingmod.items.ModItems.DIAMOND_UPGRADE.get()) ||
                       stack.is(com.example.processingmod.items.ModItems.NETHERITE_UPGRADE.get());
            }
            if (slot == 5) { // Output - non si può inserire manualmente
                return false;
            }
            return super.isItemValid(slot, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 5) return 1;  // Slot input: un solo item alla volta
            if (slot == 6) return 1; // Solo un upgrade alla volta
            return super.getSlotLimit(slot); // Slot output (5): stack normale
        }
    };

    // Handler per automazione esterna: blocca estrazione dagli slot input (0-4), consente solo dallo slot output (5).
    // NON usato dalla GUI (che usa itemHandler direttamente per consentire la rimozione manuale).
    private final IItemHandler automationHandler = new net.neoforged.neoforge.items.wrapper.RangedWrapper(itemHandler, 0, 6) {
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            // L'automazione può estrarre solo dallo slot output (5)
            if (slot < 5) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // L'automazione non può inserire nello slot output
            if (slot == 5) return false;
            return super.isItemValid(slot, stack);
        }
    };

    // Buffer Energetico (100.000 FE capacità, 1.000 FE in, 1.000 FE out per consumo interno)
    // maxReceive=1000 FE/t (velocità cavi dall'esterno), maxExtract=100000 (consumo interno illimitato)
    public final net.neoforged.neoforge.energy.EnergyStorage energyStorage = new net.neoforged.neoforge.energy.EnergyStorage(100000, 1000, 100000) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                setChanged();
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (extracted > 0 && !simulate) {
                setChanged();
            }
            return extracted;
        }
    };

    // Variabili di processing
    private int progress = 0;
    private int maxProgress = 0;

    // ContainerData per sincronizzare int verso il client (GUI)
    protected final net.minecraft.world.inventory.ContainerData data = new net.minecraft.world.inventory.ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> energyStorage.getEnergyStored() & 0xFFFF; // low 16 bit
                case 3 -> (energyStorage.getEnergyStored() >> 16) & 0xFFFF; // high 16 bit
                case 4 -> energyStorage.getMaxEnergyStored() & 0xFFFF; // low 16 bit
                case 5 -> (energyStorage.getMaxEnergyStored() >> 16) & 0xFFFF; // high 16 bit
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                // Energia in genere non si imposta dalla GUI al server, quindi skippiamo
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public AAMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AA_MACHINE_BE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory_v4", itemHandler.serializeNBT(registries));
        tag.put("energy", energyStorage.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory_v4")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory_v4"));
        }
        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(registries, tag.get("energy"));
        }
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("blockentity.processingmod.aa_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new com.example.processingmod.menus.AAMachineMenu(containerId, playerInventory, this, this.data);
    }

    // ---------------------------------------------------------------
    // Logica di Processing (Ticker)
    // ---------------------------------------------------------------

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return; // Esegui solo lato server

        boolean isDirty = false;
        boolean isProcessing = false;
        Optional<RecipeHolder<EmpowererRecipe>> recipeOpt = findMatchingRecipe(level);

        if (recipeOpt.isPresent()) {
            EmpowererRecipe recipe = recipeOpt.get().value();

            int multiplier = getSpeedMultiplier();
            int baseProcessingTime = recipe.getTime();
            // Il costo AA è energyPerStand * 4
            int totalEnergyCost = recipe.getEnergyPerStand() * 4;
            int energyPerTick = Math.max(1, totalEnergyCost / baseProcessingTime);
            int actualEnergyConsumption = energyPerTick * multiplier;

            // Aggiorna sempre maxProgress in base alla ricetta corrente
            if (maxProgress != baseProcessingTime) {
                maxProgress = baseProcessingTime;
                isDirty = true;
            }

            if (hasEnoughEnergy(actualEnergyConsumption) && canFitOutput(recipe)) {
                // Energia disponibile: avanza il progresso
                isProcessing = true;
                consumeEnergy(actualEnergyConsumption);
                progress += multiplier;
                isDirty = true;

                if (progress >= maxProgress) {
                    craftItem(recipe);
                    resetProgress();
                }
            }
            // Se manca energia o output pieno: il progresso NON viene azzerato.
            // Il crafting riprende da dove si era fermato appena l'energia torna disponibile.

        } else {
            // Nessuna ricetta valida (ingredienti cambiati/rimossi): azzera il progresso
            if (progress > 0) {
                resetProgress();
                isDirty = true;
            }
        }

        // Aggiorna lo stato LIT del blocco se è cambiato
        if (state.hasProperty(com.example.processingmod.blocks.AAMachineBlock.LIT)) {
            if (state.getValue(com.example.processingmod.blocks.AAMachineBlock.LIT) != isProcessing) {
                level.setBlock(pos, state.setValue(com.example.processingmod.blocks.AAMachineBlock.LIT, isProcessing), 3);
            }
        }

        if (isDirty) {
            setChanged(level, pos, state);
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    private void craftItem(EmpowererRecipe recipe) {
        // Rimuovi un item da ciascuno degli slot di input (0-4)
        for (int i = 0; i < 5; i++) {
            ItemStack s = itemHandler.getStackInSlot(i);
            if (!s.isEmpty()) s.shrink(1);
        }

        // Aggiungi l'output nello slot 5 bypassando isItemValid
        ItemStack output = recipe.getResultItem(this.level.registryAccess()).copy();
        ItemStack existing = itemHandler.getStackInSlot(5);
        if (existing.isEmpty()) {
            itemHandler.setStackInSlot(5, output);
        } else {
            existing.grow(output.getCount());
        }
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 0;
    }

    // Metodo helper chiamato dal RegisterCapabilitiesEvent
    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == null) {
            return itemHandler; // Accesso completo per la GUI
        }
        return automationHandler; // Stesso handler per TUTTI i lati (Sopra, Sotto, Nord, Sud, Est, Ovest)
    }

    // Metodo helper per Capability Energia
    @Nullable
    public net.neoforged.neoforge.energy.IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyStorage; // Accesso all'energia consentito da TUTTI i lati
    }

    // Helper per la logica di processing
    public boolean hasEnoughEnergy(int amount) {
        return energyStorage.getEnergyStored() >= amount;
    }

    public void consumeEnergy(int amount) {
        energyStorage.extractEnergy(amount, false);
    }

    // ---------------------------------------------------------------
    // Ricette
    // ---------------------------------------------------------------

    /**
     * Cerca nel RecipeManager le ricette originali di Actually Additions.
     */
    public Optional<RecipeHolder<EmpowererRecipe>> findMatchingRecipe(Level level) {
        // Raccogliamo i 5 slot di input (skip quelli vuoti per ora)
        ItemStack[] slots = new ItemStack[5];
        for (int i = 0; i < 5; i++) {
            slots[i] = itemHandler.getStackInSlot(i);
        }

        // Dobbiamo avere esattamente 5 item totali (1 base + 4 modifier)
        int filledCount = 0;
        for (ItemStack s : slots) {
            if (!s.isEmpty()) filledCount++;
        }
        if (filledCount != 5) return Optional.empty();

        // Proviamo ciascuno dei 5 slot come possibile base item
        for (int baseSlot = 0; baseSlot < 5; baseSlot++) {
            ItemStack base = slots[baseSlot];
            if (base.isEmpty()) continue;

            final int finalBaseSlot = baseSlot;

            // Raccogliamo i modifier (tutti gli altri 4 slot)
            java.util.List<ItemStack> modifiers = new java.util.ArrayList<>();
            for (int i = 0; i < 5; i++) {
                if (i != finalBaseSlot) modifiers.add(slots[i]);
            }

            Optional<RecipeHolder<EmpowererRecipe>> result = level.getRecipeManager()
                    .getAllRecipesFor(ActuallyRecipes.Types.EMPOWERING.get())
                    .stream()
                    .filter(holder -> {
                        EmpowererRecipe r = holder.value();
                        // Verifica base
                        if (!r.getInput().test(base)) return false;

                        // Verifica i 4 modifier in qualsiasi ordine
                        java.util.List<net.minecraft.world.item.crafting.Ingredient> recipeMods = new java.util.ArrayList<>();
                        recipeMods.add(r.getStandOne());
                        recipeMods.add(r.getStandTwo());
                        recipeMods.add(r.getStandThree());
                        recipeMods.add(r.getStandFour());

                        java.util.List<ItemStack> currentMods = new java.util.ArrayList<>(modifiers);

                        for (net.minecraft.world.item.crafting.Ingredient ing : recipeMods) {
                            boolean found = false;
                            for (int i = 0; i < currentMods.size(); i++) {
                                if (ing.test(currentMods.get(i))) {
                                    currentMods.remove(i);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) return false;
                        }
                        return currentMods.isEmpty();
                    })
                    .findFirst();

            if (result.isPresent()) return result;
        }

        return Optional.empty();
    }

    private boolean canFitOutput(EmpowererRecipe recipe) {
        ItemStack output = recipe.getResultItem(this.level.registryAccess());
        ItemStack existing = itemHandler.getStackInSlot(5);
        if (existing.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(existing, output)) return false;
        return existing.getCount() + output.getCount() <= existing.getMaxStackSize();
    }

    private int getSpeedMultiplier() {
        ItemStack upgrade = itemHandler.getStackInSlot(6);
        if (upgrade.is(com.example.processingmod.items.ModItems.IRON_UPGRADE.get())) return 2;
        if (upgrade.is(com.example.processingmod.items.ModItems.GOLD_UPGRADE.get())) return 5;
        if (upgrade.is(com.example.processingmod.items.ModItems.DIAMOND_UPGRADE.get())) return 10;
        if (upgrade.is(com.example.processingmod.items.ModItems.NETHERITE_UPGRADE.get())) return 20;
        return 1; // Velocità base
    }
}
