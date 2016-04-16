package squeek.wailaharvestability.helpers;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

public class BlockHelper
{
	private static final HashMap<String, ItemStack> testTools = new HashMap<String, ItemStack>();
	static
	{
		testTools.put("pickaxe", new ItemStack(Items.wooden_pickaxe));
		testTools.put("shovel", new ItemStack(Items.wooden_shovel));
		testTools.put("axe", new ItemStack(Items.wooden_axe));
	}

	public static String getEffectiveToolOf(World world, BlockPos blockPos, Block block, IBlockState blockState)
	{
		String effectiveTool = block.getHarvestTool(blockState);
		if (effectiveTool == null)
		{
			float hardness = block.getBlockHardness(world, blockPos);
			if (hardness > 0f)
			{
				for (Map.Entry<String, ItemStack> testToolEntry : testTools.entrySet())
				{
					ItemStack testTool = testToolEntry.getValue();
					if (testTool != null && testTool.getItem() instanceof ItemTool && testTool.getStrVsBlock(block) >= ((ItemTool) testTool.getItem()).getToolMaterial().getEfficiencyOnProperMaterial())
					{
						effectiveTool = testToolEntry.getKey();
						break;
					}
				}
			}
		}
		return effectiveTool;
	}

	public static boolean isBlockUnbreakable(Block block, World world, BlockPos blockPos)
	{
		return block.getBlockHardness(world, blockPos) == -1.0f;
	}

	public static boolean isAdventureModeAndBlockIsUnbreakable(EntityPlayer player, Block block)
	{
		NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(player.getGameProfile().getId());
		GameType gameType = networkplayerinfo.getGameType();

		if (!gameType.isAdventure())
			return false;

		if (player.isAllowEdit())
			return false;

		ItemStack heldItem = player.getCurrentEquippedItem();

		return gameType == GameType.SPECTATOR || heldItem == null || !heldItem.canDestroy(block);
	}

	/**
	 * A copy+paste of ForgeHooks.canHarvestBlock, modified to be position-agnostic
	 * See https://github.com/MinecraftForge/MinecraftForge/pull/2769
	 */
	public static boolean canHarvestBlock(Block block, EntityPlayer player, IBlockState state)
	{
		if (block.getMaterial().isToolNotRequired())
		{
			return true;
		}

		ItemStack stack = player.inventory.getCurrentItem();
		String tool = block.getHarvestTool(state);
		if (stack == null || tool == null)
		{
			return player.canHarvestBlock(block);
		}

		int toolLevel = stack.getItem().getHarvestLevel(stack, tool);
		if (toolLevel < 0)
		{
			return player.canHarvestBlock(block);
		}

		return toolLevel >= block.getHarvestLevel(state);
	}
}
