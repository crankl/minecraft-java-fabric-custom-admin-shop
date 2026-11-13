package at.crankl.adminshop;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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
	public void open(ServerPlayer player, String profile) {
		ShopConfig.Profile selected = config.profiles().get(profile);
		if (selected == null) { player.sendSystemMessage(Component.literal("Unknown shop profile: " + profile)); return; }
		ShopScreens.openCategories(player, profile, selected, this);
	}
	public boolean openBoundWorkstation(ServerPlayer player, Level world, BlockPos pos) {
		String profile = workstations.get(world, pos);
		if (profile == null) return false;
		open(player, profile);
		return true;
	}
	public void bind(Level world, BlockPos pos, String profile) { workstations.put(world, pos, profile); }
	public void unbind(Level world, BlockPos pos) { workstations.remove(world, pos); }
}
