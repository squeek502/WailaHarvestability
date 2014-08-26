package squeek.wailaharvestability.helpers;

import java.util.HashMap;
import squeek.wailaharvestability.proxy.ProxyIguanaTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockHelper
{
	private static final HashMap<String, ItemStack> testTools = new HashMap<String, ItemStack>();
	static
	{
		testTools.put("pickaxe", new ItemStack(Item.pickaxeWood));
		testTools.put("shovel", new ItemStack(Item.shovelWood));
		testTools.put("axe", new ItemStack(Item.axeWood));
	}

	public static boolean getHarvestLevelsOf(Block block, int metadata, String[] toolClasses, int[] harvestLevels)
	{
		int i = 0;
		boolean hasEffectiveTools = false;
		boolean isGravelOre = ProxyIguanaTweaks.isGravelOre(block);
		for (String toolClass : toolClasses)
		{
			harvestLevels[i] = isGravelOre && toolClass.equals("pickaxe") ? -1 : MinecraftForge.getBlockHarvestLevel(block, metadata, toolClass);

			if (harvestLevels[i] != -1)
				hasEffectiveTools = true;
			else
			{
				float hardness = 0f;
				try
				{
					hardness = block.getBlockHardness(null, 0, 0, 0);
				}
				catch (Exception e)
				{
				}
				if (hardness > 0f)
				{
					ItemStack testTool = testTools.get(toolClass);
					if (testTool != null && testTool.getItem() instanceof ItemTool && testTool.getStrVsBlock(block) >= ((ItemTool) testTool.getItem()).efficiencyOnProperMaterial)
					{
						harvestLevels[i] = 0;
						hasEffectiveTools = true;
					}
				}
			}

			i++;
		}
		return hasEffectiveTools;
	}
	
	public static boolean isBlockUnbreakable(Block block, World world, int x, int y, int z)
	{
		return block.getBlockHardness(world, x, y, z) == -1.0f;
	}

}
