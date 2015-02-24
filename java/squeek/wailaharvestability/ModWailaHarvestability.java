package squeek.wailaharvestability;

import squeek.wailaharvestability.helpers.ToolHelper;
import squeek.wailaharvestability.proxy.ProxyIguanaTweaks;
import squeek.wailaharvestability.setup.MissingHarvestInfo;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION, dependencies = "after:TConstruct;after:ExtraTiC;after:TSteelworks;after:Mariculture", acceptableRemoteVersions="*")
public class ModWailaHarvestability
{
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
		MissingHarvestInfo.init();
		FMLInterModComms.sendMessage("Waila", "register", "squeek.wailaharvestability.WailaHandler.callbackRegister");
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

		FMLInterModComms.sendRuntimeMessage(ModInfo.MODID, "VersionChecker", "addVersionCheck", "http://www.ryanliptak.com/minecraft/versionchecker/squeek502/WailaHarvestability");
	}
}
