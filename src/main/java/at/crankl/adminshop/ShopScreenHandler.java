package at.crankl.adminshop;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntConsumer;

final class ShopScreenHandler extends ChestMenu {
	private final IntConsumer topSlotClick;
	ShopScreenHandler(int syncId, Inventory inventory, Container display, IntConsumer topSlotClick) {
		super(MenuType.GENERIC_9x6, syncId, inventory, display, 6);
		this.topSlotClick = topSlotClick;
	}
	@Override public void clicked(int slotIndex, int button, ContainerInput actionType, Player player) {
		if (slotIndex >= 0 && slotIndex < 54) { topSlotClick.accept(slotIndex); return; }
		super.clicked(slotIndex, button, actionType, player);
	}
	@Override public ItemStack quickMoveStack(Player player, int slot) {
		// Prevent shift-clicking a player stack into the immutable display inventory.
		return ItemStack.EMPTY;
	}
}
