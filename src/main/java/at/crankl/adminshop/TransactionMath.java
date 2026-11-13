package at.crankl.adminshop;

import java.util.List;

/** Game-independent transaction arithmetic, deliberately suitable for JVM unit tests. */
public final class TransactionMath {
	public record Slot(int count, boolean matchesIncoming) { }
	private TransactionMath() { }
	public static boolean hasAtLeast(long available, int required) { return available >= required; }
	public static boolean canFit(int incoming, int maxStackSize, List<Slot> slots) {
		int remaining = incoming;
		for (Slot slot : slots) {
			if (slot.count() == 0) remaining -= maxStackSize;
			else if (slot.matchesIncoming()) remaining -= maxStackSize - slot.count();
			if (remaining <= 0) return true;
		}
		return false;
	}
}
