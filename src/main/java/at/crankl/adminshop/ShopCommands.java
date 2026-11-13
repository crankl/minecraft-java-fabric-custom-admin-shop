package at.crankl.adminshop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

final class ShopCommands {
	static void register(CommandDispatcher<ServerCommandSource> dispatcher, ShopService service) {
		dispatcher.register(CommandManager.literal("shop")
			.executes(context -> { service.open(context.getSource().getPlayerOrThrow(), "main"); return 1; })
			.then(CommandManager.argument("profile", StringArgumentType.word()).executes(context -> {
				service.open(context.getSource().getPlayerOrThrow(), StringArgumentType.getString(context, "profile")); return 1;
			}))
			.then(CommandManager.literal("admin").requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.literal("bind").then(CommandManager.argument("profile", StringArgumentType.word()).executes(context -> {
					var player = context.getSource().getPlayerOrThrow();
					String profile = StringArgumentType.getString(context, "profile");
					if (!service.hasProfile(profile)) { context.getSource().sendError(Text.literal("Unknown shop profile: " + profile)); return 0; }
					var hit = player.raycast(6.0, 0.0f, false);
					if (!(hit instanceof net.minecraft.util.hit.BlockHitResult blockHit) || !player.getWorld().getBlockState(blockHit.getBlockPos()).isIn(net.minecraft.registry.tag.BlockTags.ACQUIRABLE_JOB_SITE)) {
						context.getSource().sendError(Text.literal("Look at a vanilla villager workstation within 6 blocks.")); return 0;
					}
					service.bind(player.getWorld(), blockHit.getBlockPos(), profile);
					context.getSource().sendFeedback(() -> Text.literal("Workstation bound to shop profile '" + profile + "'."), true); return 1;
				})))
				.then(CommandManager.literal("remove").then(CommandManager.argument("position", BlockPosArgumentType.blockPos()).executes(context -> {
					var pos = BlockPosArgumentType.getBlockPos(context, "position"); service.unbind(context.getSource().getWorld(), pos);
					context.getSource().sendFeedback(() -> Text.literal("Workstation binding removed."), true); return 1;
				})))
				.then(CommandManager.literal("reload").executes(context -> { service.reload(); context.getSource().sendFeedback(() -> Text.literal("Admin shop configuration reloaded."), true); return 1; }))));
	}
}
