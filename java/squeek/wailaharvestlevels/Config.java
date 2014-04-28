package squeek.wailaharvestlevels;

import java.io.File;
import net.minecraftforge.common.Configuration;

public class Config
{
	private static final String CATEGORY_MAIN = "Main";
	
	public static String MINIMAL_SEPARATOR_STRING;
	private static final String MINIMAL_SEPARATOR_STRING_NAME = "minimalModeSeparator";
	private static final String MINIMAL_SEPARATOR_STRING_DEFAULT = " : ";
	
	private static Configuration config;
	
	public static void init( File file )
	{
		config = new Configuration( file );

		load();

		MINIMAL_SEPARATOR_STRING = config.get(CATEGORY_MAIN, MINIMAL_SEPARATOR_STRING_NAME, MINIMAL_SEPARATOR_STRING_DEFAULT).getString();

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
