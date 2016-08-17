package squeek.wailaharvestability.helpers;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.lang.reflect.Method;
import java.util.List;

public class StringHelper
{

	public static Class<?> HarvestLevels = null;
	public static Method getHarvestLevelName = null;
	static
	{
		try
		{
			HarvestLevels = Class.forName("slimeknights.tconstruct.library.utils.HarvestLevels");
			getHarvestLevelName = HarvestLevels.getDeclaredMethod("getHarvestLevelName", int.class);
		}
		catch (Exception e)
		{
		}
	}

	public static String getHarvestLevelName(int num)
	{
		if (getHarvestLevelName != null)
		{
			try
			{
				return (String) getHarvestLevelName.invoke(null, num);
			}
			catch (Exception e)
			{
			}
		}

		String unlocalized = "wailaharvestability.harvestlevel" + (num + 1);

		if (I18n.hasKey(unlocalized))
			return I18n.format(unlocalized);

		return String.valueOf(num);
	}

	public static String concatenateStringList(List<String> strings, String separator)
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (String s : strings)
		{
			sb.append(sep).append(s);
			sep = separator;
		}
		return sb.toString();
	}

	public static String stripFormatting(String str)
	{
		return TextFormatting.getTextWithoutFormattingCodes(str);
	}
}
