package at.crankl.adminshop;

import net.minecraft.item.ItemStack;

import java.util.List;

/** Pure inventory arithmetic used to make transaction preflight checks testable. */
public final class InventoryTransfer {
	private InventoryTransfer() { }
	public static boolean hasAtLeast(List<ItemStack> stacks, ItemStack wanted) {
		long found = stacks.stream().filter(stack -> ItemStack.areItemsAndComponentsEqual(stack, wanted)).mapToLong(ItemStack::getCount).sum();
		return found >= wanted.getCount();
	}
	public static boolean canFit(List<ItemStack> stacks, ItemStack incoming) {
		int remaining = incoming.getCount();
		for (ItemStack stack : stacks) {
			if (stack.isEmpty()) remaining -= incoming.getMaxCount();
			else if (ItemStack.areItemsAndComponentsEqual(stack, incoming)) remaining -= stack.getMaxCount() - stack.getCount();
			if (remaining <= 0) return true;
		}
		return false;
	}
}
