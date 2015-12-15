package squeek.wailaharvestability;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.wailaharvestability.setup.MissingHarvestInfo;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION, acceptedMinecraftVersions="[1.8,1.9)", dependencies = "after:TConstruct;after:ExtraTiC;after:TSteelworks;after:Mariculture", acceptableRemoteVersions = "*")
public class ModWailaHarvestability
{
	public static boolean hasIguanaTweaks;

	@SideOnly(Side.CLIENT)
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.init(event.getSuggestedConfigurationFile());

		FMLInterModComms.sendMessage("VersionChecker", "addVersionCheck", "http://www.ryanliptak.com/minecraft/versionchecker/squeek502/WailaHarvestability");
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
	}
}
