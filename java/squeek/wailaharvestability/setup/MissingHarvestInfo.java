package squeek.wailaharvestability.setup;

import net.minecraftforge.common.ToolType;

public class MissingHarvestInfo
{
	public static final ToolType SWORD = ToolType.get("sword");

	public static void init()
	{
		vanilla();
	}

	public static void vanilla()
	{
		/*ForgeHooks.setBlockToolSetter((block, tool, level) -> { //TODO
			Blocks.COBWEB.harvestTool = tool;
			Blocks.COBWEB.harvestLevel = level;
		});
		Blocks.COBWEB.setHarvestLevel("sword", 0);*/
	}
}