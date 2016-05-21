package squeek.wailaharvestability.setup;

import net.minecraft.init.Blocks;

public class MissingHarvestInfo
{
	public static void init()
	{
		vanilla();
	}

	public static void vanilla()
	{
		Blocks.WEB.setHarvestLevel("sword", 0);
	}
}
