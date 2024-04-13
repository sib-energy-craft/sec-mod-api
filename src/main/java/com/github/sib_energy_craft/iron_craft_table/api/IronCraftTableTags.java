package com.github.sib_energy_craft.iron_craft_table.api;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.NotNull;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IronCraftTableTags {
    private static final TagKey<Item> IRON_CRAFTING_TABLE_TOOL;

    static {
        IRON_CRAFTING_TABLE_TOOL = TagKey.of(RegistryKeys.ITEM, Identifiers.of("iron_crafting_table_tool"));
    }

    /**
     * Method allow to check item stack on having tag 'iron_crafting_table_tool'.<br/>
     * Validation is performed by item tag, not item type
     *
     * @param itemStack stack to check
     * @return true - item has tag 'iron_crafting_table_tool', false - otherwise
     */
    public static boolean isIronCraftingTableTool(@NotNull ItemStack itemStack) {
        return itemStack.streamTags().anyMatch(it -> it.equals(IRON_CRAFTING_TABLE_TOOL));
    }
}
