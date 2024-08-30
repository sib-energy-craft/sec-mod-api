package com.github.sib_energy_craft.machines.block.entity.property;

import com.github.sib_energy_craft.screen.property.ScreenPropertyCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public enum EnergyMachineTypedProperties implements EnergyMachineTypedProperty<ByteBuf, Integer> {
    CHARGE,
    MAX_CHARGE;

    public static final int PROPERTIES_SIZE = values().length;

    @Override
    public int getIndex() {
        return ordinal();
    }

    @Override
    public PacketCodec<ByteBuf, Integer> getCodec() {
        return ScreenPropertyCodecs.INTEGER;
    }

}
