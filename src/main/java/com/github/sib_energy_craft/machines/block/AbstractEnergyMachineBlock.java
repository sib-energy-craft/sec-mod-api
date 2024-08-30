package com.github.sib_energy_craft.machines.block;

import com.github.sib_energy_craft.energy_api.Energy;
import com.github.sib_energy_craft.energy_api.EnergyLevel;
import com.github.sib_energy_craft.energy_api.items.ChargeableItem;
import com.github.sib_energy_craft.machines.block.entity.AbstractEnergyMachineBlockEntity;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Getter
public abstract class AbstractEnergyMachineBlock extends BlockWithEntity {
    /**
     * Abstract energy machine working state
     */
    public static final BooleanProperty WORKING = BooleanProperty.of("working");

    private final EnergyLevel energyLevel;
    private final Energy maxCharge;

    protected AbstractEnergyMachineBlock(@NotNull Settings settings,
                                         @NotNull EnergyLevel energyLevel,
                                         Energy maxCharge) {
        super(settings);
        this.energyLevel = energyLevel;
        this.maxCharge = maxCharge;
        this.setDefaultState(this.stateManager.getDefaultState().with(WORKING, false));
    }

    @Override
    public void onPlaced(@NotNull World world,
                         @NotNull BlockPos pos,
                         @NotNull BlockState state,
                         @Nullable LivingEntity placer,
                         @NotNull ItemStack itemStack) {
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractEnergyMachineBlockEntity<?> machineBlockEntity) {
            final var item = itemStack.getItem();
            if (!itemStack.isEmpty() && (item instanceof ChargeableItem chargeableItem)) {
                machineBlockEntity.onPlaced(chargeableItem.getCharge(itemStack));
            }
        }
    }

    @Override
    protected ActionResult onUse(@NotNull BlockState state,
                                 @NotNull World world,
                                 @NotNull BlockPos pos,
                                 @NotNull PlayerEntity player,
                                 @NotNull BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        this.openScreen(world, pos, player);
        return ActionResult.CONSUME;
    }

    @Override
    public void onStateReplaced(@NotNull BlockState state,
                                @NotNull World world,
                                @NotNull BlockPos pos,
                                @NotNull BlockState newState,
                                boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractEnergyMachineBlockEntity<?> machineBlockEntity) {
            if (world instanceof ServerWorld serverWorld) {
                machineBlockEntity.onStateReplaced(state, serverWorld, pos, newState, moved);
            }
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public boolean hasComparatorOutput(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(@NotNull BlockState state,
                                   @NotNull World world,
                                   @NotNull BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public @NotNull BlockRenderType getRenderType(@NotNull BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(@NotNull StateManager.Builder<Block, BlockState> builder) {
        builder.add(WORKING);
    }

    /**
     * Method called when player interacts with block and needs to open machine screen
     *
     * @param world  game world
     * @param pos    block position
     * @param player player entity
     */
    protected abstract void openScreen(@NotNull World world,
                                       @NotNull BlockPos pos,
                                       @NotNull PlayerEntity player);


    @Override
    public void afterBreak(@NotNull World world,
                           @NotNull PlayerEntity player,
                           @NotNull BlockPos pos,
                           @NotNull BlockState state,
                           @Nullable BlockEntity blockEntity,
                           @NotNull ItemStack hand) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        getDroppedStacks(state, serverWorld, pos, blockEntity, player, hand).forEach(stackx -> {
            dropStack(world, pos, stackx);
            if (!(blockEntity instanceof AbstractEnergyMachineBlockEntity<?> abstractEnergyMachineBlockEntity)) {
                return;
            }
            var item = stackx.getItem();
            var charge = abstractEnergyMachineBlockEntity.getCharge();
            if (item instanceof ChargeableItem chargeableItem) {
                chargeableItem.charge(stackx, charge);
            }
        });
        state.onStacksDropped(serverWorld, pos, hand, true);
    }
}
