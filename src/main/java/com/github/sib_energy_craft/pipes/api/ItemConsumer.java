package com.github.sib_energy_craft.pipes.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface ItemConsumer {

    /**
     * Can consumer consume all or part of item stack.
     *
     * @param itemStack stack to consume
     * @param direction consuming directions
     * @return true - can consume - false - otherwise
     */
    boolean canConsume(@NotNull ItemStack itemStack, @NotNull Direction direction);

    /**
     * Consume item stack from passed directions
     *
     * @param itemStack stack to consume
     * @param direction consuming directions
     * @return not consumed items
     */
    @NotNull
    ItemStack consume(@NotNull ItemStack itemStack, @NotNull Direction direction);
}
