package at.crankl.adminshop;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerPlayer;

public final class CranklAdminShop implements ModInitializer {
	public static final String MOD_ID = "crankl_admin_shop";
	private static ShopService service;

	@Override
	public void onInitialize() {
		service = new ShopService();
		service.reload();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ShopCommands.register(dispatcher, service));
		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			if (world.isClientSide() || !(player instanceof ServerPlayer serverPlayer) || !Workstations.isSupported(world.getBlockState(hit.getBlockPos()))) return InteractionResult.PASS;
			return service.openBoundWorkstation(serverPlayer, world, hit.getBlockPos()) ? InteractionResult.SUCCESS_SERVER : InteractionResult.PASS;
		});
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (!world.isClientSide() && Workstations.isSupported(state)) service.unbind(world, pos);
			return true;
		});
	}
}
