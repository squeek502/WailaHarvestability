package squeek.wailaharvestability;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class Config
{
	private static final String CATEGORY_MAIN = "Main";

	public static String MINIMAL_SEPARATOR_STRING;
	private static final String MINIMAL_SEPARATOR_STRING_NAME = "minimal.mode.separator";
	private static final String MINIMAL_SEPARATOR_STRING_DEFAULT = " : ";

	public static String CURRENTLY_HARVESTABLE_STRING;
	private static final String CURRENTLY_HARVESTABLE_STRING_NAME = "is.currently.harvestable.string";
	// for some reason \u2714 was getting translated as ? when compiled with gradle; no clue why but this seems to work
	private static final String CURRENTLY_HARVESTABLE_STRING_DEFAULT = String.valueOf(Character.toChars(0x2714));

	public static String NOT_CURRENTLY_HARVESTABLE_STRING;
	private static final String NOT_CURRENTLY_HARVESTABLE_STRING_NAME = "not.currently.harvestable.string";
	// for some reason \u2718 was getting translated as ? when compiled with gradle; no clue why but this seems to work
	private static final String NOT_CURRENTLY_HARVESTABLE_STRING_DEFAULT = String.valueOf(Character.toChars(0x2718));

	private static Configuration config;

	public static void init(File file)
	{
		config = new Configuration(file);

		load();

		MINIMAL_SEPARATOR_STRING = config.get(CATEGORY_MAIN, MINIMAL_SEPARATOR_STRING_NAME, MINIMAL_SEPARATOR_STRING_DEFAULT).getString();
		CURRENTLY_HARVESTABLE_STRING = config.get(CATEGORY_MAIN, CURRENTLY_HARVESTABLE_STRING_NAME, CURRENTLY_HARVESTABLE_STRING_DEFAULT).getString();
		NOT_CURRENTLY_HARVESTABLE_STRING = config.get(CATEGORY_MAIN, NOT_CURRENTLY_HARVESTABLE_STRING_NAME, NOT_CURRENTLY_HARVESTABLE_STRING_DEFAULT).getString();

		save();
	}

	public static void save()
	{
		config.save();
	}

	public static void load()
	{
		config.load();
	}
}
