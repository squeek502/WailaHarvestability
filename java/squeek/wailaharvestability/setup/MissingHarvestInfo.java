package squeek.wailaharvestability.setup;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

public class MissingHarvestInfo
{
	public static void init()
	{
		vanilla();
	}

	public static void vanilla()
	{
		MinecraftForge.setBlockHarvestLevel(Block.web, "sword", 0);
	}
}
