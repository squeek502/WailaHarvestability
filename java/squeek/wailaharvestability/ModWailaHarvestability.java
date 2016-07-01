package squeek.wailaharvestability;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import squeek.wailaharvestability.setup.MissingHarvestInfo;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION, acceptedMinecraftVersions="[1.10,1.11)", dependencies = "after:TConstruct;after:ExtraTiC;after:TSteelworks;after:Mariculture", clientSideOnly = true, guiFactory = ModInfo.GUI_FACTORY_CLASS)
public class ModWailaHarvestability
{
	public static boolean hasIguanaTweaks;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.init(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new Config());

		FMLInterModComms.sendMessage("VersionChecker", "addVersionCheck", "http://www.ryanliptak.com/minecraft/versionchecker/squeek502/WailaHarvestability");
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MissingHarvestInfo.init();
		FMLInterModComms.sendMessage("Waila", "register", "squeek.wailaharvestability.WailaHandler.callbackRegister");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new TooltipHandler());
	}
}
