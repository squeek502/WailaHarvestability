package squeek.wailaharvestlevels;

import squeek.wailaharvestlevels.helpers.ToolHelper;
import squeek.wailaharvestlevels.proxy.ProxyIguanaTweaks;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = ModWailaHarvestLevels.MODID, version = ModWailaHarvestLevels.VERSION, dependencies = "after:TConstruct;after:ExtraTiC;after:TSteelworks;after:Mariculture")
public class ModWailaHarvestLevels
{
	public static final String MODID = "WailaHarvestLevels";
	public static final String VERSION = "${version}";
	
	public static boolean hasIguanaTweaks;

	@SideOnly(Side.CLIENT)
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.init(event.getSuggestedConfigurationFile());
	}
	
	@SideOnly(Side.CLIENT)
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		FMLInterModComms.sendMessage("Waila", "register", "squeek.wailaharvestlevels.WailaHandler.callbackRegister");
	}

	@SideOnly(Side.CLIENT)
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		if (Loader.isModLoaded("IguanaTweaksTConstruct"))
		{
			hasIguanaTweaks = true;
			ProxyIguanaTweaks.init();
		}
		
		ToolHelper.init();
	}
}
