package squeek.wailaharvestability.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeHooks;

import java.util.Set;

public class ToolHelper
{
	public static Set<String> getToolClassesOf(ItemStack tool)
	{
		return tool.getItem().getToolClasses(tool);
	}

	public static boolean isToolOfClass(ItemStack tool, String toolClass)
	{
		return getToolClassesOf(tool).contains(toolClass);
	}

	public static boolean toolHasAnyToolClass(ItemStack tool)
	{
		return !getToolClassesOf(tool).isEmpty();
	}

	public static boolean isToolEffectiveAgainst(ItemStack tool, IBlockAccess blockAccess, BlockPos blockPos, Block block, String effectiveToolClass)
	{
		return ForgeHooks.isToolEffective(blockAccess, blockPos, tool) || (toolHasAnyToolClass(tool) ? isToolOfClass(tool, effectiveToolClass) : tool.getItem().getStrVsBlock(tool, blockAccess.getBlockState(blockPos)) > 1.5f);
	}

	public static boolean canToolHarvestLevel(ItemStack tool, IBlockAccess blockAccess, BlockPos blockPos, int harvestLevel)
	{
		return ForgeHooks.canToolHarvestBlock(blockAccess, blockPos, tool);
	}

	public static boolean canToolHarvestBlock(ItemStack tool, Block block, IBlockState blockState)
	{
		return block.getMaterial(blockState).isToolNotRequired() || tool.canHarvestBlock(blockState);
	}

	public static int getToolHarvestLevel(ItemTool tool, ItemStack toolStack)
	{
		String toolClass = ToolHelper.getToolClassesOf(toolStack).iterator().next();
		return tool.getHarvestLevel(toolStack, toolClass, null, null);
	}
}