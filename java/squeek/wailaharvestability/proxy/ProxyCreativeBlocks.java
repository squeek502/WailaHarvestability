package squeek.wailaharvestability.proxy;

import net.minecraft.block.Block;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;

public class ProxyCreativeBlocks
{
	private static Class<?> CreativeBlocks = null;
	private static Method isCreativeBlock = null;

	static
	{
		if (ModList.get().isLoaded("creativeblocks"))
		{
			try
			{
				CreativeBlocks = Class.forName("squeek.creativeblocks.CreativeBlocks");
				isCreativeBlock = CreativeBlocks.getDeclaredMethod("isCreativeBlock", Block.class);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static boolean isCreativeBlock(Block block)
	{
		if (isCreativeBlock != null)
		{
			try
			{
				return (Boolean) isCreativeBlock.invoke(null, block);
			}
			catch (Exception ignored)
			{
			}
		}
		return false;
	}
}
