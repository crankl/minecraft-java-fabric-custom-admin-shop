package at.crankl.adminshop;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

final class ShopTransaction {
	static boolean execute(ServerPlayer player, ItemStack cost, ItemStack result) {
		Inventory inventory = player.getInventory();
		List<ItemStack> before = new ArrayList<>();
		for (int slot = 0; slot < inventory.getContainerSize(); slot++) before.add(inventory.getItem(slot).copy());
		if (!InventoryTransfer.hasAtLeast(before, cost)) { player.sendSystemMessage(Component.literal("You do not have the required items.")); return false; }
		if (!InventoryTransfer.canFit(before, result)) { player.sendSystemMessage(Component.literal("Not enough inventory space.")); return false; }
		int remaining = cost.getCount();
		for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (!ItemStack.isSameItemSameComponents(stack, cost)) continue;
			int removed = Math.min(remaining, stack.getCount());
			stack.shrink(removed);
			remaining -= removed;
		}
		for (int remainingOutput = result.getCount(); remainingOutput > 0;) {
			int chunk = Math.min(remainingOutput, result.getMaxStackSize());
			ItemStack output = result.copyWithCount(chunk);
			if (!inventory.add(output) || !output.isEmpty()) throw new IllegalStateException("Transaction preflight mismatch");
			remainingOutput -= chunk;
		}
		inventory.setChanged();
		player.sendSystemMessage(Component.literal("Transaction complete."));
		return true;
	}
}
