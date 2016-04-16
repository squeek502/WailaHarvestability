package squeek.wailaharvestability.helpers;

import net.minecraft.util.text.TextFormatting;

public class ColorHelper
{
	private static final TextFormatting colorRange[] = {
		TextFormatting.DARK_RED,
		TextFormatting.RED,
		TextFormatting.GOLD,
		TextFormatting.YELLOW,
		TextFormatting.DARK_GREEN,
		TextFormatting.GREEN,
		TextFormatting.AQUA
	};

	private static final TextFormatting booleanColorRange[] = {
		TextFormatting.DARK_RED,
		TextFormatting.RED,
		TextFormatting.DARK_GREEN,
		TextFormatting.GREEN
	};

	public static String getRelativeColor(double val, double min, double max)
	{
		if (min == max)
			return TextFormatting.RESET.toString();
		else if ((max > min && val > max) || (min > max && val < max))
			return TextFormatting.WHITE.toString() + TextFormatting.BOLD;
		else if ((max > min && val < min) || (min > max && val > min))
			return colorRange[0].toString() + TextFormatting.BOLD;

		int index = (int) (((val - min) / (max - min)) * (colorRange.length - 1));
		return colorRange[Math.max(0, Math.min(colorRange.length - 1, index))].toString();
	}

	public static String getBooleanColor(boolean val)
	{
		return getBooleanColor(val, false);
	}

	public static String getBooleanColor(boolean val, boolean modified)
	{
		return booleanColorRange[(val ? 2 : 0) + (modified ? 1 : 0)].toString();
	}
}