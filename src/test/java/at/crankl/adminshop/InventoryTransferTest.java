package at.crankl.adminshop;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryTransferTest {
	@Test void countsMatchingStacksAcrossSlots() {
		assertTrue(InventoryTransfer.hasAtLeast(List.of(new ItemStack(Items.IRON_INGOT, 32), new ItemStack(Items.IRON_INGOT, 32)), new ItemStack(Items.IRON_INGOT, 64)));
		assertFalse(InventoryTransfer.hasAtLeast(List.of(new ItemStack(Items.IRON_INGOT, 63)), new ItemStack(Items.IRON_INGOT, 64)));
	}
	@Test void requiresEnoughSpaceForEntireOutput() {
		assertTrue(InventoryTransfer.canFit(List.of(new ItemStack(Items.DIAMOND, 63), ItemStack.EMPTY), new ItemStack(Items.DIAMOND, 65)));
		assertFalse(InventoryTransfer.canFit(List.of(new ItemStack(Items.DIAMOND, 63)), new ItemStack(Items.DIAMOND, 2)));
	}
	@Test void doesNotTreatDifferentItemsAsPayment() {
		assertFalse(InventoryTransfer.hasAtLeast(List.of(new ItemStack(Items.GOLD_INGOT, 64)), new ItemStack(Items.IRON_INGOT, 1)));
	}
}
