package squeek.wailaharvestability.helpers;

import squeek.wailaharvestability.proxy.ProxyIguanaTweaks;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

public class BlockHelper
{

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

			i++;
		}
		return hasEffectiveTools;
	}

}
