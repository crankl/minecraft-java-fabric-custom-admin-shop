package at.crankl.adminshop;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;

/** The complete, intentionally fixed set of vanilla villager job-site blocks. */
final class Workstations {
	private Workstations() { }
	static boolean isSupported(BlockState state) {
		return state.is(Blocks.BLAST_FURNACE) || state.is(Blocks.SMOKER) || state.is(Blocks.CARTOGRAPHY_TABLE)
			|| state.is(Blocks.BREWING_STAND) || state.is(Blocks.COMPOSTER) || state.is(Blocks.BARREL)
			|| state.is(Blocks.FLETCHING_TABLE) || state.is(Blocks.CAULDRON) || state.is(Blocks.LECTERN)
			|| state.is(Blocks.LOOM) || state.is(Blocks.STONECUTTER) || state.is(Blocks.GRINDSTONE)
			|| state.is(Blocks.SMITHING_TABLE);
	}
}
