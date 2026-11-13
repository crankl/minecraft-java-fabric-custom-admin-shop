package at.crankl.adminshop;

import java.util.List;
import java.util.Map;

public record ShopConfig(Map<String, Profile> profiles) {
	public record Profile(String title, List<Category> categories) {}
	public record Category(String id, String title, String icon, List<Offer> offers) {}
	public record Offer(String id, String item, int itemCount, String priceItem, int priceCount) {}
}
