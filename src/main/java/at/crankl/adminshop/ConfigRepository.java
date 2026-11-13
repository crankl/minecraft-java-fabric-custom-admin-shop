package at.crankl.adminshop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

final class ConfigRepository {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("crankl-admin-shop.json");

	ShopConfig load() throws IOException {
		if (Files.notExists(FILE)) Files.writeString(FILE, GSON.toJson(defaultConfig()));
		try (var reader = Files.newBufferedReader(FILE)) {
			ShopConfig config = GSON.fromJson(reader, ShopConfig.class);
			if (config == null || config.profiles() == null || config.profiles().isEmpty()) throw new IOException("profiles must not be empty");
			validate(config);
			return config;
		}
	}
	private void validate(ShopConfig config) throws IOException {
		for (var profile : config.profiles().entrySet()) {
			if (profile.getValue() == null || profile.getValue().categories() == null) throw new IOException("profile '" + profile.getKey() + "' has no categories");
			for (ShopConfig.Category category : profile.getValue().categories()) {
				if (category == null || category.offers() == null) throw new IOException("a category has no offers");
				validateItem(category.icon());
				for (ShopConfig.Offer offer : category.offers()) {
					if (offer == null || offer.itemCount() <= 0 || offer.priceCount() <= 0) throw new IOException("offer counts must be positive");
					validateItem(offer.item()); validateItem(offer.priceItem());
				}
			}
		}
	}
	private void validateItem(String value) throws IOException {
		Identifier id = Identifier.tryParse(value);
		if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) throw new IOException("unknown item identifier: " + value);
	}

	private ShopConfig defaultConfig() {
		var offers = java.util.List.of(
			new ShopConfig.Offer("iron-for-diamond", "minecraft:diamond", 1, "minecraft:iron_ingot", 64),
			new ShopConfig.Offer("diamond-for-stone", "minecraft:stone", 64, "minecraft:diamond", 1),
			new ShopConfig.Offer("apples-for-diamond", "minecraft:diamond", 1, "minecraft:apple", 16));
		return new ShopConfig(Map.of("main", new ShopConfig.Profile("Admin Shop", java.util.List.of(
			new ShopConfig.Category("resources", "Resources", "minecraft:chest", offers)))));
	}
}
