package squeek.wailaharvestability.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.HashMap;
import java.util.Map;

public class BlockHelper
{
	private static final HashMap<ToolType, ItemStack> testTools = new HashMap<>();
	static
	{
		testTools.put(ToolType.PICKAXE, new ItemStack(Items.WOODEN_PICKAXE));
		testTools.put(ToolType.SHOVEL, new ItemStack(Items.WOODEN_SHOVEL));
		testTools.put(ToolType.AXE, new ItemStack(Items.WOODEN_AXE));
	}

	public static ToolType getEffectiveToolOf(World world, BlockPos blockPos, BlockState state)
	{
		ToolType effectiveTool = state.getHarvestTool();
		if (effectiveTool == null)
		{
			float hardness = state.getBlockHardness(world, blockPos);
			if (hardness > 0f)
			{
				for (Map.Entry<ToolType, ItemStack> testToolEntry : testTools.entrySet())
				{
					ItemStack testTool = testToolEntry.getValue();
					if (testTool != null && testTool.getItem() instanceof ToolItem && testTool.getDestroySpeed(state) >= ItemTier.WOOD.getEfficiency())
					{
						effectiveTool = testToolEntry.getKey();
						break;
					}
				}
			}
		}
		return effectiveTool;
	}

	public static boolean isBlockUnbreakable(World world, BlockPos blockPos, BlockState state)
	{
		return state.getBlockHardness(world, blockPos) == -1.0f;
	}

	public static boolean isAdventureModeAndBlockIsUnbreakable(PlayerEntity player, BlockPos pos)
	{
		ClientPlayNetHandler netHandler = Minecraft.getInstance().getConnection();
		if (netHandler == null)
			return false;

		NetworkPlayerInfo networkplayerinfo = netHandler.getPlayerInfo(player.getGameProfile().getId());
		GameType gameType = networkplayerinfo.getGameType();

		if (gameType != GameType.ADVENTURE)
			return false;

		if (player.isAllowEdit())
			return false;

		ItemStack heldItem = player.getHeldItemMainhand();
		World world = player.world;

		return gameType == GameType.SPECTATOR || heldItem.isEmpty() || !heldItem.canDestroy(world.getTags(), new CachedBlockInfo(world, pos, false));
	}

	/**
	 * A copy+paste of ForgeHooks.canHarvestBlock, modified to be position-agnostic
	 * See https://github.com/MinecraftForge/MinecraftForge/pull/2769
	 */
	public static boolean canHarvestBlock(Block block, PlayerEntity player, BlockState state)
	{
		if (state.getMaterial().isToolNotRequired())
		{
			return true;
		}

		ItemStack stack = player.inventory.getCurrentItem();
		ToolType tool = block.getHarvestTool(state);
		if (stack.isEmpty() || tool == null)
		{
			return player.canHarvestBlock(state);
		}

		int toolLevel = stack.getItem().getHarvestLevel(stack, tool, player, state);
		if (toolLevel < 0)
		{
			return player.canHarvestBlock(state);
		}

		return toolLevel >= block.getHarvestLevel(state);
	}
}
