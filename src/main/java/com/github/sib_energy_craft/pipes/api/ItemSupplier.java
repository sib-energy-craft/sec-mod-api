package com.github.sib_energy_craft.pipes.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface ItemSupplier {

    /**
     * Get a list of items that supplier can supply from the requested side
     *
     * @param direction supplying directions
     * @return list of item stacks or empty list
     */
    @NotNull
    List<ItemStack> canSupply(@NotNull Direction direction);

    /**
     * Supply item stack from a passed direction
     *
     * @param requested requested item stack for supply
     * @param direction supplying directions
     * @return true - item successfully supplied, false - otherwise
     */
    boolean supply(@NotNull ItemStack requested, @NotNull Direction direction);

    /**
     * Return supplied stack into supplier, in case if item can't be consumed
     *
     * @param requested requested item stack for rollback supply
     * @param direction supplying directions
     */
    void returnStack(@NotNull ItemStack requested, @NotNull Direction direction);
}
