package at.crankl.adminshop;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Inventory backing a shop screen. It never yields or accepts an item stack. */
final class ReadOnlyInventory implements Container {
	private final List<ItemStack> stacks = new ArrayList<>();
	ReadOnlyInventory(int size) { for (int index = 0; index < size; index++) stacks.add(ItemStack.EMPTY); }
	void display(int slot, ItemStack stack) { stacks.set(slot, stack.copy()); }
	@Override public int getContainerSize() { return stacks.size(); }
	@Override public boolean isEmpty() { return false; }
	@Override public ItemStack getItem(int slot) { return stacks.get(slot); }
	@Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
	@Override public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }
	@Override public void setItem(int slot, ItemStack stack) { }
	@Override public void setChanged() { }
	@Override public boolean stillValid(Player player) { return true; }
	@Override public void clearContent() { }
}
