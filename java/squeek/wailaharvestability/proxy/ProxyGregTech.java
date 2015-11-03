package squeek.wailaharvestability.proxy;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProxyGregTech
{
	public static final String modID = "gregtech";
	public static final String oreBlockID = "gt.blockores";
	public static final String oreBlockUniqueIdentifier = modID + ":" + oreBlockID;
	public static boolean isModLoaded = Loader.isModLoaded(modID);

	public static boolean isOreBlock(Block block)
	{
		return isModLoaded && GameRegistry.findUniqueIdentifierFor(block).toString().equals(oreBlockUniqueIdentifier);
	}
}
