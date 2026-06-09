package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
// TODO Phase 4: import net.shadowmage.ancientwarfare.core.init.AWCoreItems;
// TODO Phase 4: import net.shadowmage.ancientwarfare.core.item.ItemBackpack;

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
		// TODO Phase 4: if (stack.getItem() != AWCoreItems.BACKPACK) {
			ret = backpackInventory.insertItem(slot, stack, simulate);
			if (ret.getCount() < stack.getCount()) {
				saveToStack(backpackInventory);
			}
		// TODO Phase 4: }
		return ret;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack ret = backpackInventory.extractItem(slot, amount, simulate);
		if (!ret.isEmpty()) {
			saveToStack(backpackInventory);
		// TODO Phase 4: }
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
		// TODO Phase 4: if (!stack.isEmpty() && stack.getItem() instanceof ItemBackpack) {
			ItemStackHandler handler = new ItemStackHandler(/* TODO Phase 4: (stack.getDamageValue() + 1) * */ 9); // Fallback size for now
			//noinspection ConstantConditions
			if (stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA) && stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).contains(BACKPACK_ITEMS_TAG)) {
				handler.deserializeNBT(stack.registryAccess(), stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA).copyTag().getCompound(BACKPACK_ITEMS_TAG));
			}
			return handler;
		// TODO Phase 4: }
		// TODO Phase 4: return new ItemStackHandler();
	}

	private void saveToStack(ItemStackHandler handler) {
		CompoundTag invTag = handler.serializeNBT(backpackStack.registryAccess());
		net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, backpackStack, (t) -> t.put(BACKPACK_ITEMS_TAG, invTag));
	}
}
