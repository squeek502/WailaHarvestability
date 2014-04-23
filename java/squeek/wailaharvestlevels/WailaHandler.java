package squeek.wailaharvestlevels;

import java.util.ArrayList;
import java.util.List;
import squeek.wailaharvestlevels.helpers.BlockHelper;
import squeek.wailaharvestlevels.helpers.ColorHelper;
import squeek.wailaharvestlevels.helpers.StringHelper;
import squeek.wailaharvestlevels.helpers.ToolHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

public class WailaHandler implements IWailaDataProvider
{
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return toolTip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		if (config.getConfig("harvestlevels.sneakingonly", false) && !accessor.getPlayer().isSneaking())
			return toolTip;
		
		if (config.getConfig("harvestlevels.toolrequiredonly") && accessor.getBlock().blockMaterial.isToolNotRequired())
			return toolTip;
		
		boolean showHarvestLevel = config.getConfig("harvestlevels.harvestlevel");
		boolean showEffectiveTool = config.getConfig("harvestlevels.effectivetool");
		boolean showCurrentlyHarvestable = config.getConfig("harvestlevels.currentlyharvestable");
		boolean showOresOnly = config.getConfig("harvestlevels.oresonly", false);
		boolean minimalLayout = config.getConfig("harvestlevels.minimal", false);
		boolean hideWhileHarvestable = config.getConfig("harvestlevels.unharvestableonly", false);
		
		if (showHarvestLevel || showEffectiveTool || showCurrentlyHarvestable)
		{
			int oreID = -1;
			if (showOresOnly && ((oreID = OreDictionary.getOreID(itemStack)) == -1 || !OreDictionary.getOreName(oreID).startsWith("ore")))
			{
				return toolTip;
			}
			
			String toolClasses[] = new String[] { "pickaxe", "shovel", "axe" };
			int harvestLevels[] = new int[toolClasses.length];
			boolean blockHasEffectiveTools = BlockHelper.getHarvestLevelsOf(accessor.getBlock(), accessor.getMetadata(), toolClasses, harvestLevels);
			
			if (!blockHasEffectiveTools)
				return toolTip;
			
			int harvestLevel = -1;
			String effectiveTool = "";
			int i = 0;
			for (String toolClass : toolClasses)
			{
				if (harvestLevels[i] >= 0)
				{
					harvestLevel = harvestLevels[i];
					effectiveTool = toolClass;
					break;
				}
				i++;
			}
			
			boolean canHarvest = false;
			boolean isEffective = false;
			boolean isAboveMinHarvestLevel = false;
			
			ItemStack itemHeld = accessor.getPlayer().getHeldItem();
			if (itemHeld != null)
			{
				canHarvest = ToolHelper.canToolHarvestBlock(itemHeld, accessor.getBlock(), accessor.getMetadata());
				isAboveMinHarvestLevel = (showCurrentlyHarvestable || showHarvestLevel) && ToolHelper.canToolHarvestLevel(itemHeld, accessor.getBlock(), accessor.getMetadata(), harvestLevel);
				isEffective = showEffectiveTool && ToolHelper.isToolEffectiveAgainst(itemHeld, accessor.getBlock(), accessor.getMetadata(), effectiveTool);
			}
			
			boolean isCurrentlyHarvestable = canHarvest && isAboveMinHarvestLevel;
			
			if (hideWhileHarvestable && isCurrentlyHarvestable)
				return toolTip;
	        
			if (!minimalLayout)
			{
				if (showCurrentlyHarvestable)
					toolTip.add(ColorHelper.getBooleanColor(isCurrentlyHarvestable) + (isCurrentlyHarvestable ? "\u2714" : "\u2718") + EnumChatFormatting.RESET + " Currently Harvestable");
	        	if (harvestLevel != -1 && showEffectiveTool)
	        		toolTip.add("Effective Tool : " + ColorHelper.getBooleanColor(isEffective && canHarvest, isEffective && !canHarvest) + StatCollector.translateToLocal("harvestlevels.toolclass." + effectiveTool));
	        	if (harvestLevel >= 1 && showHarvestLevel)
	        		toolTip.add("Harvest Level : " + ColorHelper.getBooleanColor(isAboveMinHarvestLevel && canHarvest) + StringHelper.getHarvestLevelName(harvestLevel));
			}
			else
			{
				List<String> stringParts = new ArrayList<String>();
				
				if (showCurrentlyHarvestable)
					stringParts.add(ColorHelper.getBooleanColor(isCurrentlyHarvestable) + (isCurrentlyHarvestable ? "\u2714" : "\u2718"));
	        	if (harvestLevel != -1 && showEffectiveTool)
	        		stringParts.add(ColorHelper.getBooleanColor(isEffective && canHarvest, isEffective && !canHarvest) + StatCollector.translateToLocal("harvestlevels.toolclass." + effectiveTool));
	        	if (harvestLevel >= 1 && showHarvestLevel)
	        		stringParts.add(ColorHelper.getBooleanColor(isAboveMinHarvestLevel && canHarvest) + StringHelper.getHarvestLevelName(harvestLevel));
	        	
	        	if (!stringParts.isEmpty())
	        	{
	        		toolTip.add(StringHelper.concatenateStringList(stringParts, EnumChatFormatting.RESET + " : "));
	        	}
			}
		}
        
		return toolTip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return toolTip;
	}

	public static void callbackRegister(IWailaRegistrar registrar)
	{
		WailaHandler instance = new WailaHandler();
		registrar.addConfig("HarvestLevels", "harvestlevels.harvestlevel", "option.harvestlevels.harvestlevel");
		registrar.addConfig("HarvestLevels", "harvestlevels.effectivetool", "option.harvestlevels.effectivetool");
		registrar.addConfig("HarvestLevels", "harvestlevels.currentlyharvestable", "option.harvestlevels.currentlyharvestable");
		registrar.addConfig("HarvestLevels", "harvestlevels.oresonly", "option.harvestlevels.oresonly");
		registrar.addConfig("HarvestLevels", "harvestlevels.sneakingonly", "option.harvestlevels.sneakingonly");
		registrar.addConfig("HarvestLevels", "harvestlevels.minimal", "option.harvestlevels.minimal");
		registrar.addConfig("HarvestLevels", "harvestlevels.unharvestableonly", "option.harvestlevels.unharvestableonly");
		registrar.addConfig("HarvestLevels", "harvestlevels.toolrequiredonly", "option.harvestlevels.toolrequiredonly");
		registrar.registerBodyProvider(instance, Block.class);
	}
}