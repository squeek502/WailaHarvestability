package squeek.wailaharvestability;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = ModInfo.MODID)
public class ModWailaHarvestability
{
	public static boolean hasIguanaTweaks;

	public ModWailaHarvestability()
	{
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupClient);
		modBus.addListener(this::setupCommon);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.spec);
	}

	private void setupClient(FMLClientSetupEvent event)
	{

	}

	private void setupCommon(FMLCommonSetupEvent event)
	{

	}
}