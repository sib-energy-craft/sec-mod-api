package com.github.sib_energy_craft.machines.cooking.block.entity.property;

import com.github.sib_energy_craft.machines.block.entity.property.EnergyMachineTypedProperties;
import com.github.sib_energy_craft.machines.block.entity.property.EnergyMachineTypedProperty;
import com.github.sib_energy_craft.screen.property.ScreenPropertyCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

/**
 * @author sibmaks
 * @since 0.0.36
 */
public enum CookingEnergyMachineTypedProperties implements EnergyMachineTypedProperty<ByteBuf, Integer> {
    COOKING_TIME,
    COOKING_TIME_TOTAL;

    private static final int OFFSET = EnergyMachineTypedProperties.values().length;
    public static final int PROPERTIES_SIZE = OFFSET + values().length;

    @Override
    public int getIndex() {
        return OFFSET + ordinal();
    }

    @Override
    public PacketCodec<ByteBuf, Integer> getCodec() {
        return ScreenPropertyCodecs.INTEGER;
    }
}
