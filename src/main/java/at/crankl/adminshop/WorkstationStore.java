package at.crankl.adminshop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

final class WorkstationStore {
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("crankl-admin-shop-workstations.json");
	private final Gson gson = new Gson();
	private final Map<String, String> bindings = new HashMap<>();

	WorkstationStore() { load(); }
	String get(World world, BlockPos pos) { return bindings.get(key(world, pos)); }
	void put(World world, BlockPos pos, String profile) { bindings.put(key(world, pos), profile); save(); }
	void remove(World world, BlockPos pos) { if (bindings.remove(key(world, pos)) != null) save(); }
	private String key(World world, BlockPos pos) { return world.getRegistryKey().getValue() + "@" + pos.asLong(); }
	private void load() {
		if (Files.notExists(FILE)) return;
		try (var reader = Files.newBufferedReader(FILE)) {
			Map<String, String> stored = gson.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
			if (stored != null) bindings.putAll(stored);
		} catch (IOException ignored) { }
	}
	private void save() {
		try { Files.writeString(FILE, gson.toJson(bindings)); } catch (IOException exception) { throw new IllegalStateException("Could not save workstation bindings", exception); }
	}
}
