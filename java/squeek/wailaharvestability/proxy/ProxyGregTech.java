package squeek.wailaharvestability.proxy;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.Loader;

public class ProxyGregTech
{
	public static final String MOD_ID = "gregtech";
	public static final String ORE_BLOCK_ID = "gt.blockores";
	public static final String ORE_BLOCK_UNIQUE_IDENTIFIER = MOD_ID + ":" + ORE_BLOCK_ID;
	public static boolean isModLoaded = Loader.isModLoaded(MOD_ID);

	public static boolean isOreBlock(Block block)
	{
		return isModLoaded && String.valueOf(block.getRegistryName()).equals(ORE_BLOCK_UNIQUE_IDENTIFIER);
	}
}
