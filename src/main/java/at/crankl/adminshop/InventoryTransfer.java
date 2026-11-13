package at.crankl.adminshop;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Pure inventory arithmetic used to make transaction preflight checks testable. */
public final class InventoryTransfer {
	private InventoryTransfer() { }
	public static boolean hasAtLeast(List<ItemStack> stacks, ItemStack wanted) {
		long found = stacks.stream().filter(stack -> ItemStack.isSameItemSameComponents(stack, wanted)).mapToLong(ItemStack::getCount).sum();
		return TransactionMath.hasAtLeast(found, wanted.getCount());
	}
	public static boolean canFit(List<ItemStack> stacks, ItemStack incoming) {
		return TransactionMath.canFit(incoming.getCount(), incoming.getMaxStackSize(), stacks.stream()
			.map(stack -> new TransactionMath.Slot(stack.getCount(), ItemStack.isSameItemSameComponents(stack, incoming))).toList());
	}
}
