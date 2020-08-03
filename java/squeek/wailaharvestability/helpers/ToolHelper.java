package squeek.wailaharvestability.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import java.util.Set;

public class ToolHelper
{
	public static Set<ToolType> getToolTypesOf(@Nonnull ItemStack tool)
	{
		return tool.getItem().getToolTypes(tool);
	}

	public static boolean isToolOfClass(@Nonnull ItemStack tool, ToolType toolType)
	{
		return getToolTypesOf(tool).contains(toolType);
	}

	public static boolean toolHasAnyToolClass(@Nonnull ItemStack tool)
	{
		return !getToolTypesOf(tool).isEmpty();
	}

	public static boolean isToolEffectiveAgainst(@Nonnull ItemStack tool, IWorldReader worldReader, BlockPos blockPos, ToolType effectiveToolType)
	{
		return ForgeHooks.isToolEffective(worldReader, blockPos, tool) || (toolHasAnyToolClass(tool) ? isToolOfClass(tool, effectiveToolType) : tool.getItem().getDestroySpeed(tool, worldReader.getBlockState(blockPos)) > 1.5f);
	}

	public static boolean canToolHarvestLevel(@Nonnull ItemStack tool, IWorldReader worldReader, BlockPos blockPos, PlayerEntity player, int harvestLevel)
	{
		BlockState state = worldReader.getBlockState(blockPos);
		ToolType harvestTool = state.getBlock().getHarvestTool(state);

		return !tool.isEmpty() && harvestTool != null && tool.getItem().getHarvestLevel(tool, harvestTool, player, state) >= harvestLevel;
	}

	public static boolean canToolHarvestBlock(@Nonnull ItemStack tool, BlockState blockState)
	{
		return blockState.func_235783_q_() || tool.canHarvestBlock(blockState);
	}

	public static int getToolHarvestLevel(ToolItem tool, @Nonnull ItemStack toolStack)
	{
		Set<ToolType> toolClasses = ToolHelper.getToolTypesOf(toolStack);
		if (toolClasses.isEmpty())
			return 0;

		ToolType toolClass = toolClasses.iterator().next();
		return tool.getHarvestLevel(toolStack, toolClass, null, null);
	}
}