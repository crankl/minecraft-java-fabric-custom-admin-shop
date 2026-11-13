package at.crankl.adminshop;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

final class ShopScreens {
	private static final int PREVIOUS = 45, MODE_BUY = 46, BACK = 49, MODE_SELL = 50, NEXT = 53;
	private ShopScreens() { }
	static void openCategories(ServerPlayer player, String profileId, ShopConfig.Profile profile, ShopService service) {
		ReadOnlyInventory menu = new ReadOnlyInventory(54);
		for (int index = 0; index < Math.min(45, profile.categories().size()); index++) {
			ShopConfig.Category category = profile.categories().get(index);
			menu.display(index, named(stack(category.icon(), 1), category.title()));
		}
		player.openMenu(new SimpleMenuProvider((syncId, inventory, ignored) ->
			new ShopScreenHandler(syncId, inventory, menu, slot -> {
				if (slot < profile.categories().size()) openOffers(player, profileId, profile, profile.categories().get(slot), 0, false, service);
			}), Component.literal(profile.title())));
	}
	static void openOffers(ServerPlayer player, String profileId, ShopConfig.Profile profile, ShopConfig.Category category, int page, boolean selling, ShopService service) {
		int pages = Math.max(1, (category.offers().size() + 35) / 36);
		page = Math.clamp(page, 0, pages - 1);
		final int selectedPage = page;
		ReadOnlyInventory menu = new ReadOnlyInventory(54);
		int start = page * 36;
		for (int index = 0; index < 36 && start + index < category.offers().size(); index++) {
			ShopConfig.Offer offer = category.offers().get(start + index);
			ItemStack shown = stack(selling ? offer.priceItem() : offer.item(), selling ? offer.priceCount() : offer.itemCount());
			menu.display(index, named(shown, (selling ? "Sell: " : "Buy: ") + offer.id()));
		}
		menu.display(MODE_BUY, named(stack("minecraft:emerald", 1), "Buy mode"));
		menu.display(MODE_SELL, named(stack("minecraft:redstone", 1), "Sell mode"));
		if (page > 0) menu.display(PREVIOUS, named(stack("minecraft:arrow", 1), "Previous page"));
		if (page + 1 < pages) menu.display(NEXT, named(stack("minecraft:arrow", 1), "Next page"));
		menu.display(BACK, named(stack("minecraft:barrier", 1), "Categories"));
		player.openMenu(new SimpleMenuProvider((syncId, inventory, ignored) -> new ShopScreenHandler(syncId, inventory, menu, slot -> {
			if (slot < 36 && start + slot < category.offers().size()) openQuantity(player, profileId, profile, category, category.offers().get(start + slot), selectedPage, selling, 1, service);
			else if (slot == MODE_BUY) openOffers(player, profileId, profile, category, selectedPage, false, service);
			else if (slot == MODE_SELL) openOffers(player, profileId, profile, category, selectedPage, true, service);
			else if (slot == PREVIOUS) openOffers(player, profileId, profile, category, selectedPage - 1, selling, service);
			else if (slot == NEXT) openOffers(player, profileId, profile, category, selectedPage + 1, selling, service);
			else if (slot == BACK) openCategories(player, profileId, profile, service);
		}), Component.literal(category.title() + " - " + (selling ? "Sell" : "Buy"))));
	}
	static void openQuantity(ServerPlayer player, String profileId, ShopConfig.Profile profile, ShopConfig.Category category, ShopConfig.Offer offer, int page, boolean selling, int amount, ShopService service) {
		int max = maximum(player, offer, selling);
		amount = Math.clamp(amount, 1, Math.max(1, max));
		final int selected = amount;
		ReadOnlyInventory menu = new ReadOnlyInventory(54);
		menu.display(20, named(stack("minecraft:yellow_stained_glass_pane", 1), "-10"));
		menu.display(21, named(stack("minecraft:orange_stained_glass_pane", 1), "-1"));
		menu.display(22, named(stack(selling ? offer.priceItem() : offer.item(), Math.min(64, selling ? offer.priceCount() * amount : offer.itemCount() * amount)), "Quantity: " + amount));
		menu.display(23, named(stack("minecraft:lime_stained_glass_pane", 1), "+1"));
		menu.display(24, named(stack("minecraft:green_stained_glass_pane", 1), "+10"));
		menu.display(30, named(stack("minecraft:nether_star", 1), "Maximum: " + max));
		menu.display(32, named(stack("minecraft:emerald_block", 1), "Confirm"));
		menu.display(BACK, named(stack("minecraft:barrier", 1), "Back"));
		player.openMenu(new SimpleMenuProvider((syncId, inventory, ignored) -> new ShopScreenHandler(syncId, inventory, menu, slot -> {
			if (slot == 20) openQuantity(player, profileId, profile, category, offer, page, selling, selected - 10, service);
			else if (slot == 21) openQuantity(player, profileId, profile, category, offer, page, selling, selected - 1, service);
			else if (slot == 23) openQuantity(player, profileId, profile, category, offer, page, selling, selected + 1, service);
			else if (slot == 24) openQuantity(player, profileId, profile, category, offer, page, selling, selected + 10, service);
			else if (slot == 30) openQuantity(player, profileId, profile, category, offer, page, selling, Math.max(1, maximum(player, offer, selling)), service);
			else if (slot == 32) { if (execute(player, offer, selling, selected)) openOffers(player, profileId, profile, category, page, selling, service); }
			else if (slot == BACK) openOffers(player, profileId, profile, category, page, selling, service);
		}), Component.literal("Quantity")));
	}
	private static int maximum(ServerPlayer player, ShopConfig.Offer offer, boolean selling) {
		ItemStack required = stack(selling ? offer.item() : offer.priceItem(), selling ? offer.itemCount() : offer.priceCount());
		int total = 0; for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) { ItemStack current = player.getInventory().getItem(slot); if (ItemStack.isSameItemSameComponents(current, required)) total += current.getCount(); }
		return total / required.getCount();
	}
	private static boolean execute(ServerPlayer player, ShopConfig.Offer offer, boolean selling, int amount) {
		ItemStack cost = stack(selling ? offer.item() : offer.priceItem(), Math.multiplyExact(selling ? offer.itemCount() : offer.priceCount(), amount));
		ItemStack output = stack(selling ? offer.priceItem() : offer.item(), Math.multiplyExact(selling ? offer.priceCount() : offer.itemCount(), amount));
		return ShopTransaction.execute(player, cost, output);
	}
	private static Item item(String id) { return BuiltInRegistries.ITEM.getValue(Identifier.parse(id)); }
	private static ItemStack stack(String id, int count) { return new ItemStack(item(id), count); }
	private static ItemStack named(ItemStack stack, String name) { stack.set(DataComponents.CUSTOM_NAME, Component.literal(name)); return stack; }
}
