package at.crankl.adminshop;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Inventory backing a shop screen. It never yields or accepts an item stack. */
final class ReadOnlyInventory implements Inventory {
	private final List<ItemStack> stacks = new ArrayList<>();
	ReadOnlyInventory(int size) { for (int index = 0; index < size; index++) stacks.add(ItemStack.EMPTY); }
	void display(int slot, ItemStack stack) { stacks.set(slot, stack.copy()); }
	@Override public int size() { return stacks.size(); }
	@Override public boolean isEmpty() { return false; }
	@Override public ItemStack getStack(int slot) { return stacks.get(slot); }
	@Override public ItemStack removeStack(int slot, int amount) { return ItemStack.EMPTY; }
	@Override public ItemStack removeStack(int slot) { return ItemStack.EMPTY; }
	@Override public void setStack(int slot, ItemStack stack) { }
	@Override public void markDirty() { }
	@Override public boolean canPlayerUse(PlayerEntity player) { return true; }
	@Override public void clear() { }
}
