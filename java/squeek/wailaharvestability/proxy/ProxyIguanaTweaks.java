package squeek.wailaharvestability.proxy;

import java.lang.reflect.Method;
import net.minecraft.block.Block;

public class ProxyIguanaTweaks
{
	private static Class<?> IguanaTweaksTConstruct = null;
	private static Method proxyGetHarvestLevelName;
	private static Block oreGravel = null;

	public static void init()
	{
		try
		{
			IguanaTweaksTConstruct = Class.forName("iguanaman.iguanatweakstconstruct.IguanaTweaksTConstruct");
			proxyGetHarvestLevelName = IguanaTweaksTConstruct.getDeclaredMethod("getHarvestLevelName", int.class);
			
			Class<?> TContent = Class.forName("tconstruct.common.TContent");
			oreGravel = (Block) TContent.getDeclaredField("oreGravel").get(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String getHarvestLevelName(int num)
	{
		String harvestLevelName = "<Unknown>";

		try
		{
			harvestLevelName = (String) proxyGetHarvestLevelName.invoke(IguanaTweaksTConstruct, num);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return harvestLevelName;
	}
	
	public static boolean isGravelOre(Block block)
	{
		return oreGravel != null && oreGravel.blockID == block.blockID;
	}
}
