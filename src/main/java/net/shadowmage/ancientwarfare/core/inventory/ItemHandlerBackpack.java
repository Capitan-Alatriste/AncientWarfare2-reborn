package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
// TODO Phase 9: import net.shadowmage.ancientwarfare.core.init.AWCoreItems;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;

public class ItemHandlerBackpack implements IItemHandlerModifiable {
	private static final String BACKPACK_ITEMS_TAG = "backpackItems";
	private final ItemStackHandler backpackInventory;
	private final ItemStack backpackStack;

	public ItemHandlerBackpack(ItemStack backpackStack) {
		backpackInventory = getHandler(backpackStack);
		this.backpackStack = backpackStack;
	}

	@Override
	public int getSlots() {
		return backpackInventory.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return backpackInventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		ItemStack ret = stack;
		if (!(stack.getItem() instanceof ItemBackpack)) { // Prevent backpacks inside backpacks
			ret = backpackInventory.insertItem(slot, stack, simulate);
			if (ret.getCount() < stack.getCount()) {
				saveToStack(backpackInventory);
			}
		}
		return ret;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack ret = backpackInventory.extractItem(slot, amount, simulate);
		if (!ret.isEmpty()) {
			saveToStack(backpackInventory);
		}
		return ret;
	}

	@Override
	public int getSlotLimit(int slot) {
		return backpackInventory.getSlotLimit(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		backpackInventory.setStackInSlot(slot, stack);
		saveToStack(backpackInventory);
	}

	private ItemStackHandler getHandler(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemBackpack) {
			ItemStackHandler handler = new ItemStackHandler(9); // Size fallback, handle metadata resizing elsewhere if needed
			//noinspection ConstantConditions
			if (stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA) && stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).contains(BACKPACK_ITEMS_TAG)) {
				handler.deserializeNBT(/* TODO Phase 9: registry lookup */ null, stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag().getCompound(BACKPACK_ITEMS_TAG));
			}
			return handler;
		}
		return new ItemStackHandler();
	}

	private void saveToStack(ItemStackHandler handler) {
		CompoundTag invTag = handler.serializeNBT(/* TODO Phase 9: registry lookup */ null);
		net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, backpackStack, (t) -> t.put(BACKPACK_ITEMS_TAG, invTag));
	}
}
