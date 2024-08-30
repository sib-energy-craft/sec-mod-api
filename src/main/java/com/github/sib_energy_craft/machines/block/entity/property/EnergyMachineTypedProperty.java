package com.github.sib_energy_craft.machines.block.entity.property;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

/**
 * @author sibmaks
 * @since 0.0.30
 */
public interface EnergyMachineTypedProperty<B extends ByteBuf, T> {

    /**
     * Get property index
     *
     * @return index
     */
    int getIndex();

    /**
     * Get a screen property codec
     *
     * @return type
     * @see com.github.sib_energy_craft.screen.property.ScreenPropertyCodecs
     */
    PacketCodec<B, T> getCodec();
}
