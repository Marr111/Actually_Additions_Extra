package com.example.processingmod.menus;

import com.example.processingmod.ModRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModMenus {
    public static final DeferredHolder<MenuType<?>, MenuType<AAMachineMenu>> AA_MACHINE_MENU = 
            ModRegistries.MENUS.register("aa_machine", () -> IMenuTypeExtension.create(AAMachineMenu::new));
}
