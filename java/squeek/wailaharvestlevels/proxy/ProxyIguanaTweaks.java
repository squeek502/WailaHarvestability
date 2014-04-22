package squeek.wailaharvestlevels.proxy;

import java.lang.reflect.Method;

public class ProxyIguanaTweaks
{
	private static Class<?> IguanaTweaksTConstruct = null;
	private static Method proxyGetHarvestLevelName;
	
	public static void init()
	{
		try
		{
			IguanaTweaksTConstruct = Class.forName("iguanaman.iguanatweakstconstruct.IguanaTweaksTConstruct");
			proxyGetHarvestLevelName = IguanaTweaksTConstruct.getDeclaredMethod("getHarvestLevelName", int.class);
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
}
