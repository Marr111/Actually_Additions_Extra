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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.example.processingmod.recipes.AAMachineRecipe;
import com.example.processingmod.recipes.AAMachineRecipeInput;
import com.example.processingmod.recipes.ModRecipes;

import java.util.Optional;

public class AAMachineBlockEntity extends BlockEntity implements MenuProvider {

    // Inventario con 4 slot (0 e 1 input, 2 e 3 output)
    public final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    // Wrappers per limitare l'accesso in base al lato
    private final IItemHandler inputHandler = new RangedWrapper(itemHandler, 0, 2);
    private final IItemHandler outputHandler = new RangedWrapper(itemHandler, 2, 4);

    // Buffer Energetico (100.000 FE capacità, 1.000 FE in, 0 FE out)
    public final net.neoforged.neoforge.energy.EnergyStorage energyStorage = new net.neoforged.neoforge.energy.EnergyStorage(100000, 1000, 0) {
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

    public AAMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AA_MACHINE_BE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.put("energy", energyStorage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(registries, tag.get("energy"));
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("blockentity.processingmod.aa_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        // Ritornerà il Menu nella fase successiva
        return null;
    }

    // Metodo helper chiamato dal RegisterCapabilitiesEvent
    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == null) {
            return itemHandler; // Accesso completo dall'interno (opzionale)
        }
        
        if (side == Direction.DOWN) {
            return outputHandler; // Accesso solo agli slot di output (2, 3) dal basso
        } else {
            return inputHandler; // Accesso solo agli slot di input (0, 1) da top e lati
        }
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
     * Cerca nel RecipeManager una ricetta compatibile con gli slot 0 e 1 dell'inventario.
     * Chiamato dal ticker nella fase successiva.
     */
    public Optional<AAMachineRecipe> findMatchingRecipe(Level level) {
        AAMachineRecipeInput input = AAMachineRecipeInput.of(itemHandler);

        Optional<AAMachineRecipe> found = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.AA_MACHINE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.matches(input, level))
                .findFirst();

        // --- DEBUG temporaneo: rimuovere in produzione ---
        if (found.isPresent()) {
            System.out.println("[processingmod] Ricetta trovata per AA Machine @ " + getBlockPos()
                    + " → result: " + found.get().result1().getDisplayName().getString());
        }

        return found;
    }
}
