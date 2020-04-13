package squeek.wailaharvestability;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import squeek.wailaharvestability.setup.MissingHarvestInfo;

@Mod(value = ModInfo.MODID)
public class ModWailaHarvestability
{
	public static boolean hasIguanaTweaks;

	public ModWailaHarvestability()
	{
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupCommon);
		modBus.addListener(this::setupClient);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.spec);
	}

	private void setupClient(FMLClientSetupEvent event)
	{

	}

	private void setupCommon(FMLCommonSetupEvent event)
	{
		MissingHarvestInfo.init();
	}


	/*public void preInit(FMLPreInitializationEvent event) //TODO
	{
		FMLInterModComms.sendMessage("VersionChecker", "addVersionCheck", "http://www.ryanliptak.com/minecraft/versionchecker/squeek502/WailaHarvestability");
	}

	public void init(FMLInitializationEvent event) //TODO
	{
		FMLInterModComms.sendMessage("waila", "register", "squeek.wailaharvestability.WailaHandler.callbackRegister");
	}*/
}