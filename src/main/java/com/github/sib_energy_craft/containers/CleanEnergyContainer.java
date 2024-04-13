package com.github.sib_energy_craft.containers;

import com.github.sib_energy_craft.containers.api.ChargeableEnergyContainer;
import com.github.sib_energy_craft.containers.api.EnergyContainer;
import com.github.sib_energy_craft.energy_api.Energy;
import com.github.sib_energy_craft.energy_api.EnergyOffer;
import com.github.sib_energy_craft.energy_api.items.ChargeableItem;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Getter
public class CleanEnergyContainer implements EnergyContainer, ChargeableEnergyContainer {
    private Energy charge;
    private final Energy maxCharge;

    public CleanEnergyContainer(@NotNull Energy charge, int maxCharge) {
        this.maxCharge = new Energy(maxCharge);
        this.charge = charge.min(this.maxCharge);
    }

    public CleanEnergyContainer(@NotNull Energy charge, Energy maxCharge) {
        this.maxCharge = maxCharge;
        this.charge = charge.min(this.maxCharge);
    }

    @Override
    public synchronized void charge(@NotNull ItemStack itemStack, @NotNull Energy max) {
        var item = itemStack.getItem();
        if (itemStack.isEmpty() ||
                itemStack.getCount() != 1 ||
                !(item instanceof ChargeableItem chargeableItem) ||
                !chargeableItem.hasFreeSpace(itemStack)) {
            return;
        }
        var itemFreeSpace = chargeableItem.getFreeSpace(itemStack);
        var transferringEnergy = max.min(charge).min(itemFreeSpace);

        charge = charge.subtract(transferringEnergy);
        var notUsed = chargeableItem.charge(itemStack, transferringEnergy);
        charge = charge.add(notUsed).min(maxCharge);
    }

    @Override
    public synchronized void discharge(@NotNull ItemStack itemStack, @NotNull Energy max) {
        var item = itemStack.getItem();
        if (maxCharge.compareTo(charge) <= 0 ||
                itemStack.isEmpty() ||
                !(item instanceof ChargeableItem chargeableItem) ||
                !chargeableItem.hasEnergy(itemStack)) {
            return;
        }
        var freeSpace = maxCharge.subtract(charge);
        var itemCharge = chargeableItem.getCharge(itemStack);
        var transferringEnergy = max.min(freeSpace).min(itemCharge);
        if (chargeableItem.discharge(itemStack, transferringEnergy)) {
            charge = charge.add(transferringEnergy);
        }
    }

    /**
     * Deserialize container from NBT
     *
     * @param nbt block NBT
     * @return instance of CleanEnergyContainer
     */
    @NotNull
    public static CleanEnergyContainer readNbt(@NotNull NbtCompound nbt) {
        var energyAmount = new BigDecimal(nbt.getString("Charge"));
        var charge = new Energy(energyAmount);
        var maxCharge = Energy.readNbt("MaxCharge", nbt);
        return new CleanEnergyContainer(charge, maxCharge);
    }

    /**
     * Serialize container in NBT
     *
     * @param nbt block NBT
     */
    public synchronized void writeNbt(@NotNull NbtCompound nbt) {
        var energyAmount = charge.getAmount();
        nbt.putString("Charge", energyAmount.toPlainString());
        maxCharge.writeNbt("MaxCharge", nbt);
    }

    @Override
    public boolean hasEnergy() {
        return hasAtLeast(Energy.ZERO);
    }

    /**
     * Check is container has required amount of energy
     *
     * @param amount required amount
     * @return true - required energy exists, false - otherwise
     */
    public boolean hasAtLeast(Energy amount) {
        return charge.compareTo(amount) > 0;
    }

    @Override
    public boolean hasSpace() {
        return getFreeSpace().compareTo(Energy.ZERO) > 0;
    }

    @Override
    public synchronized void add(@NotNull Energy energy) {
        this.charge = this.charge.add(energy).min(maxCharge);
    }

    @Override
    public synchronized boolean subtract(@NotNull Energy energy) {
        if (this.charge.compareTo(energy) >= 0) {
            this.charge = this.charge.subtract(energy);
            return true;
        }
        return false;
    }

    /**
     * Receive energy offer<br/>
     * In case if container has space for offered energy it accepts offer and increase internal capacity.
     *
     * @param offer energy offer
     */
    public synchronized void receiveOffer(@NotNull EnergyOffer offer) {
        var freeSpace = maxCharge.subtract(charge);
        if (freeSpace.compareTo(Energy.ZERO) <= 0) {
            return;
        }
        var energyAmount = offer.getEnergyAmount();
        if (freeSpace.compareTo(energyAmount) < 0) {
            return;
        }
        if (offer.acceptOffer()) {
            this.charge = this.charge.add(energyAmount).min(this.maxCharge);
        }
    }

    /**
     * Get amount of free space
     *
     * @return amount of space for energy
     */
    public @NotNull Energy getFreeSpace() {
        return maxCharge.subtract(charge);
    }
}