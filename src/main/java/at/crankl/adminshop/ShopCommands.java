package at.crankl.adminshop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

final class ShopCommands {
	static void register(CommandDispatcher<CommandSourceStack> dispatcher, ShopService service) {
		dispatcher.register(Commands.literal("shop")
			.executes(context -> { service.open(context.getSource().getPlayerOrException(), "main"); return 1; })
			.then(Commands.argument("profile", StringArgumentType.word()).executes(context -> {
				service.open(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "profile")); return 1;
			}))
			.then(Commands.literal("admin").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
				.then(Commands.literal("bind").then(Commands.argument("profile", StringArgumentType.word()).executes(context -> {
					var player = context.getSource().getPlayerOrException();
					String profile = StringArgumentType.getString(context, "profile");
					if (!service.hasProfile(profile)) { context.getSource().sendFailure(Component.literal("Unknown shop profile: " + profile)); return 0; }
					var hit = player.pick(6.0, 0.0f, false);
					if (!(hit instanceof net.minecraft.world.phys.BlockHitResult blockHit) || !Workstations.isSupported(player.level().getBlockState(blockHit.getBlockPos()))) {
						context.getSource().sendFailure(Component.literal("Look at a vanilla villager workstation within 6 blocks.")); return 0;
					}
					service.bind(player.level(), blockHit.getBlockPos(), profile);
					context.getSource().sendSuccess(() -> Component.literal("Workstation bound to shop profile '" + profile + "'."), true); return 1;
				})))
				.then(Commands.literal("remove").then(Commands.argument("position", BlockPosArgument.blockPos()).executes(context -> {
					var pos = BlockPosArgument.getLoadedBlockPos(context, "position"); service.unbind(context.getSource().getLevel(), pos);
					context.getSource().sendSuccess(() -> Component.literal("Workstation binding removed."), true); return 1;
				})))
				.then(Commands.literal("reload").executes(context -> { service.reload(); context.getSource().sendSuccess(() -> Component.literal("Admin shop configuration reloaded."), true); return 1; }))));
	}
}
