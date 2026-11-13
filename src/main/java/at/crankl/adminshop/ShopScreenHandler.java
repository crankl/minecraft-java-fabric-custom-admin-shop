package at.crankl.adminshop;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;

import java.util.function.IntConsumer;

final class ShopScreenHandler extends GenericContainerScreenHandler {
	private final IntConsumer topSlotClick;
	ShopScreenHandler(int syncId, PlayerInventory inventory, Inventory display, IntConsumer topSlotClick) {
		super(ScreenHandlerType.GENERIC_9X6, syncId, inventory, display, 6);
		this.topSlotClick = topSlotClick;
	}
	@Override public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		if (slotIndex >= 0 && slotIndex < 54) { topSlotClick.accept(slotIndex); return; }
		super.onSlotClick(slotIndex, button, actionType, player);
	}
	@Override public ItemStack quickMove(PlayerEntity player, int slot) {
		// Prevent shift-clicking a player stack into the immutable display inventory.
		return ItemStack.EMPTY;
	}
}
