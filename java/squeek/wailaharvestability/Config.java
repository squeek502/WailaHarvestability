package squeek.wailaharvestability;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config
{
	public static final String CATEGORY_MAIN = "Main";

	public static String MINIMAL_SEPARATOR_STRING;
	private static final String MINIMAL_SEPARATOR_STRING_NAME = "minimal.mode.separator";
	private static final String MINIMAL_SEPARATOR_STRING_DEFAULT = " : ";

	public static String CURRENTLY_HARVESTABLE_STRING;
	private static final String CURRENTLY_HARVESTABLE_STRING_NAME = "is.currently.harvestable.string";
	private static final String CURRENTLY_HARVESTABLE_STRING_DEFAULT = "\u2714";

	public static String NOT_CURRENTLY_HARVESTABLE_STRING;
	private static final String NOT_CURRENTLY_HARVESTABLE_STRING_NAME = "not.currently.harvestable.string";
	private static final String NOT_CURRENTLY_HARVESTABLE_STRING_DEFAULT = "\u2718";

	public static String SHEARABILITY_STRING;
	private static final String SHEARABILITY_STRING_NAME = "shearability.string";
	private static final String SHEARABILITY_STRING_DEFAULT = "\u2702";

	public static String SILK_TOUCHABILITY_STRING;
	private static final String SILK_TOUCHABILITY_STRING_NAME = "silk.touchability.string";
	private static final String SILK_TOUCHABILITY_STRING_DEFAULT = "\u2712";

	public static Configuration config;

	public static void init(File file)
	{
		if (config == null)
		{
			config = new Configuration(file);
			load();
		}
	}

	private static void load()
	{
		MINIMAL_SEPARATOR_STRING = config.get(CATEGORY_MAIN, MINIMAL_SEPARATOR_STRING_NAME, MINIMAL_SEPARATOR_STRING_DEFAULT).getString();
		CURRENTLY_HARVESTABLE_STRING = config.get(CATEGORY_MAIN, CURRENTLY_HARVESTABLE_STRING_NAME, CURRENTLY_HARVESTABLE_STRING_DEFAULT).getString();
		NOT_CURRENTLY_HARVESTABLE_STRING = config.get(CATEGORY_MAIN, NOT_CURRENTLY_HARVESTABLE_STRING_NAME, NOT_CURRENTLY_HARVESTABLE_STRING_DEFAULT).getString();
		SHEARABILITY_STRING = config.get(CATEGORY_MAIN, SHEARABILITY_STRING_NAME, SHEARABILITY_STRING_DEFAULT).getString();
		SILK_TOUCHABILITY_STRING = config.get(CATEGORY_MAIN, SILK_TOUCHABILITY_STRING_NAME, SILK_TOUCHABILITY_STRING_DEFAULT).getString();

		save();
	}

	public static void save()
	{
		if (config.hasChanged())
		{
			config.save();
		}
	}

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(ModInfo.MODID)) {
            load();
        }
    }
}