package com.github.sib_energy_craft.machines.utils;

import com.github.sib_energy_craft.inventory.CombinedInventory;
import com.github.sib_energy_craft.machines.block.entity.EnergyMachineInventoryType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * @author sibmaks
 * @since 0.0.18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnergyMachineUtils {

    /**
     * Calculate cooking time for an abstract cooking recipe
     *
     * @param world      game world
     * @param recipeType recipe type
     * @param inventory  cooking inventory
     * @param <T>        type of recipe
     * @return time of cooking
     */
    public static <T extends AbstractCookingRecipe> int getCookTimeTotal(@NotNull World world,
                                                                         @NotNull RecipeType<T> recipeType,
                                                                         @NotNull SingleStackRecipeInput inventory) {
        return world.getRecipeManager()
                .getFirstMatch(recipeType, inventory, world)
                .map(RecipeEntry::value)
                .map(AbstractCookingRecipe::getCookingTime)
                .orElse(200);
    }

    /**
     * Method check can recipe output be applied.<br/>
     * Slot chose current slot index to cooking<br/>
     * Assuming that machine works with a process in mode 1 input to 1 output
     *
     * @param slot              slot index
     * @param combinedInventory machine inventory
     * @param world             game world
     * @param recipe            cooking recipe
     * @param count             max count
     * @return true - output can be accepted, false - otherwise
     */
    public static boolean canAcceptRecipeOutput(int slot,
                                                @NotNull CombinedInventory<EnergyMachineInventoryType> combinedInventory,
                                                @NotNull World world,
                                                @NotNull Recipe<RecipeInput> recipe,
                                                int count) {
        var sourceStack = combinedInventory.getStack(EnergyMachineInventoryType.SOURCE, slot);
        if (sourceStack.isEmpty()) {
            return false;
        }
        var outputStack = recipe.getResult(world.getRegistryManager());
        if (outputStack.isEmpty()) {
            return false;
        }
        var outputSlotStack = combinedInventory.getStack(EnergyMachineInventoryType.OUTPUT, slot);
        if (outputSlotStack.isEmpty()) {
            return true;
        }
        if (!ItemStack.areItemsEqual(outputSlotStack, outputStack)) {
            return false;
        }
        int outputSlotStackCount = outputSlotStack.getCount();
        if (outputSlotStackCount < count && outputSlotStackCount < outputSlotStack.getMaxCount()) {
            return true;
        }
        return outputSlotStackCount < outputStack.getMaxCount();
    }

    /**
     * Method try to craft items, using passed inventory and recipe
     *
     * @param slot              slot index
     * @param combinedInventory machine inventory
     * @param world             game world
     * @param recipe            cooking recipe
     * @param decrement         amount of source stack to decrement
     * @param maxCount          max amount of output
     * @return true - recipe crafted, false - otherwise
     */
    public static boolean craftRecipe(int slot,
                                      @NotNull CombinedInventory<EnergyMachineInventoryType> combinedInventory,
                                      @NotNull World world,
                                      @NotNull Recipe<RecipeInput> recipe,
                                      int decrement,
                                      int maxCount) {
        if (!canAcceptRecipeOutput(slot, combinedInventory, world, recipe, maxCount)) {
            return false;
        }
        var sourceStack = combinedInventory.getStack(EnergyMachineInventoryType.SOURCE, slot);
        var registryManager = world.getRegistryManager();
        var recipeStack = recipe.getResult(registryManager);
        var outputStack = combinedInventory.getStack(EnergyMachineInventoryType.OUTPUT, slot);
        if (outputStack.isEmpty()) {
            combinedInventory.setStack(EnergyMachineInventoryType.OUTPUT, slot, recipeStack.copy());
        } else if (outputStack.isOf(recipeStack.getItem())) {
            outputStack.increment(recipeStack.getCount());
        }
        sourceStack.decrement(decrement);
        return true;
    }
}
