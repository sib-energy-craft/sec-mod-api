package com.github.sib_energy_craft.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class SimpleInventoryRecipeInput extends SimpleInventory implements RecipeInput {
    public SimpleInventoryRecipeInput(int size) {
        super(size);
    }

    public SimpleInventoryRecipeInput(ItemStack... items) {
        super(items);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return getStack(slot);
    }

    @Override
    public int getSize() {
        return size();
    }
}
