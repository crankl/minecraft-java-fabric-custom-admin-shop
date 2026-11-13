package at.crankl.adminshop;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;

public final class ShopService {
	private final ConfigRepository configs = new ConfigRepository();
	private final WorkstationStore workstations = new WorkstationStore();
	private ShopConfig config;

	public void reload() {
		try { config = configs.load(); }
		catch (IOException exception) { throw new IllegalStateException("Could not load crankl-admin-shop.json", exception); }
	}
	public boolean hasProfile(String profile) { return config.profiles().containsKey(profile); }
	public void open(ServerPlayerEntity player, String profile) {
		ShopConfig.Profile selected = config.profiles().get(profile);
		if (selected == null) { player.sendMessage(Text.literal("Unknown shop profile: " + profile), false); return; }
		ShopScreens.openCategories(player, profile, selected, this);
	}
	public boolean openBoundWorkstation(ServerPlayerEntity player, World world, BlockPos pos) {
		String profile = workstations.get(world, pos);
		if (profile == null) return false;
		open(player, profile);
		return true;
	}
	public void bind(World world, BlockPos pos, String profile) { workstations.put(world, pos, profile); }
	public void unbind(World world, BlockPos pos) { workstations.remove(world, pos); }
}
