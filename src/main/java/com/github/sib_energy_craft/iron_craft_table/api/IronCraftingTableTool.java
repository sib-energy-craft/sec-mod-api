package com.github.sib_energy_craft.iron_craft_table.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface IronCraftingTableTool {

    /**
     * Called each time when tool used in iron crafting table
     *
     * @param player       player - crafter
     * @param toolStack    tool stack
     * @param craftedStack crafted stack
     */
    void onUse(PlayerEntity player, ItemStack toolStack, ItemStack craftedStack);

}
