package io.github.cottonmc.component.compat.core;

import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.energy.CapacitorComponentHelper;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.TankComponentHelper;
import io.github.cottonmc.component.item.InventoryComponent;
import io.github.cottonmc.component.item.InventoryComponentHelper;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;

public class BlockComponentHook implements InventoryComponentHelper.BlockInventoryHook, TankComponentHelper.BlockTankHook, CapacitorComponentHelper.BlockCapacitorHook {
	public static final BlockComponentHook INSTANCE = new BlockComponentHook();

	public static void initInventory() {
		InventoryComponentHelper.INSTANCE.addBlockHook(INSTANCE);
	}

	public static void initTank() {
		TankComponentHelper.INSTANCE.addBlockHook(INSTANCE);
	}

	public static void initCap() {
		CapacitorComponentHelper.INSTANCE.addBlockHook(INSTANCE);
	}

	@Override
	public boolean hasInvComponent(BlockView world, BlockPos pos, @Nullable Direction dir) {
		return BlockComponentProvider.get(world.getBlockState(pos)).hasComponent(world, pos, UniversalComponents.INVENTORY_COMPONENT, dir);
	}

	@Override
	@Nullable
	public InventoryComponent getInvComponent(BlockView world, BlockPos pos, @Nullable Direction dir) {
		return BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.INVENTORY_COMPONENT, dir);
	}

	@Override
	public boolean hasTankComponent(BlockView world, BlockPos pos, @Nullable Direction dir) {
		return BlockComponentProvider.get(world.getBlockState(pos)).hasComponent(world, pos, UniversalComponents.TANK_COMPONENT, dir);
	}

	@Nullable
	@Override
	public TankComponent getTankComponent(BlockView world, BlockPos pos, @Nullable Direction dir) {
		return BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.TANK_COMPONENT, dir);
	}

	@Override
	public boolean hasCapComponent(BlockView world, BlockPos pos, @Nullable Direction dir) {
		return BlockComponentProvider.get(world.getBlockState(pos)).hasComponent(world, pos, UniversalComponents.CAPACITOR_COMPONENT, dir);
	}

	@Nullable
	@Override
	public CapacitorComponent getCapComponent(BlockView world, BlockPos pos, @Nullable Direction dir) {
		return BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.CAPACITOR_COMPONENT, dir);
	}

	@Override
	public String getId() {
		return "cardinal-components-block";
	}

	private BlockComponentHook() { }
}
