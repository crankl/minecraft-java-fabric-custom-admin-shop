package at.crankl.adminshop;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

final class ShopTransaction {
	static boolean execute(ServerPlayerEntity player, ItemStack cost, ItemStack result) {
		PlayerInventory inventory = player.getInventory();
		List<ItemStack> before = new ArrayList<>();
		for (int slot = 0; slot < inventory.size(); slot++) before.add(inventory.getStack(slot).copy());
		if (!InventoryTransfer.hasAtLeast(before, cost)) { player.sendMessage(Text.literal("You do not have the required items."), true); return false; }
		if (!InventoryTransfer.canFit(before, result)) { player.sendMessage(Text.literal("Not enough inventory space."), true); return false; }
		int remaining = cost.getCount();
		for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
			ItemStack stack = inventory.getStack(slot);
			if (!ItemStack.areItemsAndComponentsEqual(stack, cost)) continue;
			int removed = Math.min(remaining, stack.getCount());
			stack.decrement(removed);
			remaining -= removed;
		}
		for (int remainingOutput = result.getCount(); remainingOutput > 0;) {
			int chunk = Math.min(remainingOutput, result.getMaxCount());
			ItemStack output = result.copyWithCount(chunk);
			if (!inventory.insertStack(output) || !output.isEmpty()) throw new IllegalStateException("Transaction preflight mismatch");
			remainingOutput -= chunk;
		}
		inventory.markDirty();
		player.sendMessage(Text.literal("Transaction complete."), true);
		return true;
	}
}
