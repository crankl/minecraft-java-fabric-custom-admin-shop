package at.crankl.adminshop;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryTransferTest {
	@Test void countsMatchingStacksAcrossSlots() {
		assertTrue(TransactionMath.hasAtLeast(64, 64));
		assertFalse(TransactionMath.hasAtLeast(63, 64));
	}
	@Test void requiresEnoughSpaceForEntireOutput() {
		assertTrue(TransactionMath.canFit(65, 64, List.of(new TransactionMath.Slot(63, true), new TransactionMath.Slot(0, false))));
		assertFalse(TransactionMath.canFit(2, 64, List.of(new TransactionMath.Slot(63, true))));
	}
	@Test void doesNotTreatDifferentItemsAsPayment() {
		assertFalse(TransactionMath.canFit(1, 64, List.of(new TransactionMath.Slot(64, false))));
	}
}
