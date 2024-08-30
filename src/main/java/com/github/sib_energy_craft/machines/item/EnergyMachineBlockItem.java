package com.github.sib_energy_craft.machines.item;

import com.github.sib_energy_craft.machines.block.AbstractEnergyMachineBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

/**
 * @author sibmaks
 * @since 0.0.6
 */
public class EnergyMachineBlockItem<T extends AbstractEnergyMachineBlock> extends BlockItem {

    public EnergyMachineBlockItem(@NotNull T block, @NotNull Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(@NotNull ItemStack stack,
                              @NotNull TooltipContext context,
                              @NotNull List<Text> tooltip,
                              @NotNull TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        var block = getBlock();
        var maxCharge = block.getMaxCharge()
                .toPlainString();
        var maxInput = block.getEnergyLevel().to
                .toPlainString();
        var textColor = Color.GRAY.getRGB();
        var textStyle = Style.EMPTY.withColor(textColor);
        tooltip.add(Text.translatable("attribute.name.sib_energy_craft.max_input_eu", maxInput)
                .setStyle(textStyle));
        tooltip.add(Text.translatable("attribute.name.sib_energy_craft.max_charge", maxCharge)
                .setStyle(textStyle));
    }

    @Override
    public AbstractEnergyMachineBlock getBlock() {
        return (AbstractEnergyMachineBlock) super.getBlock();
    }
}
