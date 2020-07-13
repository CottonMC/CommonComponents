package io.github.cottonmc.component.compat.lba;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.FixedItemInv.CopyingFixedItemInv;
import alexiil.mc.lib.attributes.item.FixedItemInvView;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.item.ItemInvUtil;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.item.InventoryComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import java.util.*;

public class WrappedInvAttributeComponent implements InventoryComponent {
	private FixedItemInv inv;
	private FixedItemInvView view;
	private ItemExtractable extractable;
	private ItemInsertable insertable;

	//TODO: other wrappers for extractable-only, insertable-only, etc?
	public WrappedInvAttributeComponent(FixedItemInv inv) {
		this.inv = inv;
		this.view = inv;
		this.extractable = inv.getExtractable();
		this.insertable = inv.getInsertable();
	}

	@Override
	public int getSize() {
		return view.getSlotCount();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : view.stackIterable()) {
			if (!stack.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public List<ItemStack> getStacks() {
		Iterable<ItemStack> stacks = view.stackIterable();
		List<ItemStack> ret = new ArrayList<>();
		stacks.forEach(stack -> ret.add(stack.copy()));
		return ret;
	}

	@Override
	public DefaultedList<ItemStack> getMutableStacks() {
		throw new UnsupportedOperationException("getMutableStacks only exists for use in asInventory, it should never be called on a WrappedInvAttributeComponent!");
	}

	@Override
	public ItemStack getStack(int slot) {
		if (view instanceof CopyingFixedItemInv) {
			return view.getInvStack(slot);
		}
		return view.getInvStack(slot).copy();
	}

	@Override
	public boolean canInsert(int slot) {
		return true; //TODO: insertable-only
	}

	@Override
	public boolean canExtract(int slot) {
		return true; //TODO: extractable-only
	}

	@Override
	public ItemStack takeStack(int slot, int amount, ActionType action) {
		return inv.getSlot(slot).attemptAnyExtraction(amount, simForAction(action));
	}

	@Override
	public ItemStack removeStack(int slot, ActionType action) {
		return takeStack(slot, Integer.MAX_VALUE, action);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inv.setInvStack(slot, stack, Simulation.ACTION);
	}

	@Override
	public ItemStack insertStack(int slot, ItemStack stack, ActionType action) {
		return ItemInvUtil.insertSingle(inv, slot, stack, simForAction(action));
	}

	@Override
	public ItemStack insertStack(ItemStack stack, ActionType action) {
		return insertable.attemptInsertion(stack, simForAction(action));
	}

	@Override
	public void clear() {

	}

	@Override
	public int getMaxStackSize(int slot) {
		return view.getMaxAmount(slot, inv.getInvStack(slot));
	}

	@Override
	public boolean isAcceptableStack(int slot, ItemStack stack) {
		return view.isItemValidForSlot(slot, stack);
	}

	@Override
	public int amountOf(Item item) {
		return view.getGroupedInv().getAmount(new ExactItemFilter(item));
	}

	@Override
	public int amountOf(Set<Item> items) {
		return view.getGroupedInv().getAmount(ExactItemFilter.anyOf(items));
	}

	@Override
	public boolean contains(Item item) {
		for (ItemStack stack : view.stackIterable()) {
			if (stack.getItem() == item) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(Set<Item> items) {
		for (ItemStack stack : view.stackIterable()) {
			if (items.contains(stack.getItem())) return true;
		}
		return false;
	}

	@Override
	public Inventory asInventory() {
		return new InventoryFixedWrapper(inv) {
			@Override
			public boolean canPlayerUse(PlayerEntity player) {
				return true;
			}
		};
	}

	@Override
	public List<Runnable> getListeners() {
		return new ArrayList<>();
	}

	private ItemFilter createFilterForSlot(int slot) {
		ItemStack stack = getStack(slot);
		return target -> ItemStack.areEqual(target, stack);
	}

	private Simulation simForAction(ActionType type) {
		if (type.shouldPerform()) return Simulation.ACTION;
		else return Simulation.SIMULATE;
	}
}
