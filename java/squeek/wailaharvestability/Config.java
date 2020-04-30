package squeek.wailaharvestability;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final Main MAIN = new Main(BUILDER);

	public static class Main
	{
		private static final String CATEGORY_MAIN = "main";

		public ForgeConfigSpec.ConfigValue<String> minimalSeparatorString;
		private static final String MINIMAL_SEPARATOR_STRING_NAME = "minimal mode separator";
		private static final String MINIMAL_SEPARATOR_STRING_DEFAULT = " : ";

		public ForgeConfigSpec.ConfigValue<String> currentlyHarvestableString;
		private static final String CURRENTLY_HARVESTABLE_STRING_NAME = "is currently harvestable string";
		private static final String CURRENTLY_HARVESTABLE_STRING_DEFAULT = "\u2714";

		public ForgeConfigSpec.ConfigValue<String> notCurrentlyHarvestableString;
		private static final String NOT_CURRENTLY_HARVESTABLE_STRING_NAME = "not currently harvestable string";
		private static final String NOT_CURRENTLY_HARVESTABLE_STRING_DEFAULT = "\u2718";

		public ForgeConfigSpec.ConfigValue<String> shearabilityString;
		private static final String SHEARABILITY_STRING_NAME = "shearability string";
		private static final String SHEARABILITY_STRING_DEFAULT = "\u2702";

		public ForgeConfigSpec.ConfigValue<String> silkTouchabilityString;
		private static final String SILK_TOUCHABILITY_STRING_NAME = "silk touchability string";
		private static final String SILK_TOUCHABILITY_STRING_DEFAULT = "\u2712";

		public ForgeConfigSpec.BooleanValue harvestLevelTooltip;
		private static final String HARVEST_LEVEL_TOOLTIP_NAME = "harvest level tooltip";

		Main(ForgeConfigSpec.Builder builder)
		{
			builder.push(CATEGORY_MAIN);
			minimalSeparatorString = builder.define(MINIMAL_SEPARATOR_STRING_NAME, MINIMAL_SEPARATOR_STRING_DEFAULT);
			currentlyHarvestableString = builder.define(CURRENTLY_HARVESTABLE_STRING_NAME, CURRENTLY_HARVESTABLE_STRING_DEFAULT);
			notCurrentlyHarvestableString = builder.define(NOT_CURRENTLY_HARVESTABLE_STRING_NAME, NOT_CURRENTLY_HARVESTABLE_STRING_DEFAULT);
			shearabilityString = builder.define(SHEARABILITY_STRING_NAME, SHEARABILITY_STRING_DEFAULT);
			//silkTouchabilityString = builder.define(SILK_TOUCHABILITY_STRING_NAME, SILK_TOUCHABILITY_STRING_DEFAULT);
			harvestLevelTooltip = builder.define(HARVEST_LEVEL_TOOLTIP_NAME, false);
			builder.pop();
		}
	}

	public static final ForgeConfigSpec spec = BUILDER.build();
}