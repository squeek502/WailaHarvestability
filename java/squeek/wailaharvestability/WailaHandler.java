package squeek.wailaharvestability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import squeek.wailaharvestability.helpers.BlockHelper;
import squeek.wailaharvestability.helpers.ColorHelper;
import squeek.wailaharvestability.helpers.OreHelper;
import squeek.wailaharvestability.helpers.StringHelper;
import squeek.wailaharvestability.helpers.ToolHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeHooks;

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
		if (config.getConfig("harvestlevels.toolrequiredonly") && accessor.getBlock().blockMaterial.isToolNotRequired())
			return toolTip;
		
		boolean isSneaking = accessor.getPlayer().isSneaking();		
		boolean showHarvestLevel = config.getConfig("harvestlevels.harvestlevel") && (!config.getConfig("harvestlevels.harvestlevel.sneakingonly") || isSneaking);
		boolean showEffectiveTool = config.getConfig("harvestlevels.effectivetool") && (!config.getConfig("harvestlevels.effectivetool.sneakingonly") || isSneaking);
		boolean showCurrentlyHarvestable = config.getConfig("harvestlevels.currentlyharvestable") && (!config.getConfig("harvestlevels.currentlyharvestable.sneakingonly") || isSneaking);
		boolean showOresOnly = config.getConfig("harvestlevels.oresonly", false);
		boolean minimalLayout = config.getConfig("harvestlevels.minimal", false);
		boolean hideWhileHarvestable = config.getConfig("harvestlevels.unharvestableonly", false);
		
		if (showHarvestLevel || showEffectiveTool || showCurrentlyHarvestable)
		{
			if (showOresOnly && !OreHelper.isItemAnOre(itemStack))
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
			boolean isHoldingTinkersTool = false;
			
			ItemStack itemHeld = accessor.getPlayer().getHeldItem();
			if (itemHeld != null)
			{
				isHoldingTinkersTool = ToolHelper.hasToolTag(itemHeld);
				canHarvest = ToolHelper.canToolHarvestBlock(itemHeld, accessor.getBlock(), accessor.getMetadata()) || (!isHoldingTinkersTool && accessor.getBlock().canHarvestBlock(accessor.getPlayer(), accessor.getMetadata()));
				isAboveMinHarvestLevel = (showCurrentlyHarvestable || showHarvestLevel) && ToolHelper.canToolHarvestLevel(itemHeld, accessor.getBlock(), accessor.getMetadata(), harvestLevel);
				isEffective = showEffectiveTool && ToolHelper.isToolEffectiveAgainst(itemHeld, accessor.getBlock(), accessor.getMetadata(), effectiveTool);
			}
			
			boolean isCurrentlyHarvestable = (canHarvest && isAboveMinHarvestLevel) || (!isHoldingTinkersTool && ForgeHooks.canHarvestBlock(accessor.getBlock(), accessor.getPlayer(), accessor.getMetadata()));
			
			if (hideWhileHarvestable && isCurrentlyHarvestable)
				return toolTip;
	        
			List<String> stringParts = new ArrayList<String>();
			
			if (showCurrentlyHarvestable)
				stringParts.add(ColorHelper.getBooleanColor(isCurrentlyHarvestable) + (isCurrentlyHarvestable ? Config.CURRENTLY_HARVESTABLE_STRING : Config.NOT_CURRENTLY_HARVESTABLE_STRING) + (!minimalLayout ?  EnumChatFormatting.RESET + " Currently Harvestable" : ""));
        	if (harvestLevel != -1 && showEffectiveTool)
        		stringParts.add((!minimalLayout ? "Effective Tool : " : "") + ColorHelper.getBooleanColor(isEffective && (!isHoldingTinkersTool || canHarvest), isHoldingTinkersTool && isEffective && !canHarvest) + StatCollector.translateToLocal("harvestlevels.toolclass." + effectiveTool));
        	if (harvestLevel >= 1 && showHarvestLevel)
        		stringParts.add((!minimalLayout ? "Harvest Level : " : "") + ColorHelper.getBooleanColor(isAboveMinHarvestLevel && canHarvest) + StringHelper.getHarvestLevelName(harvestLevel));
        	
        	if (!stringParts.isEmpty())
        	{
        		if (minimalLayout)
        			toolTip.add(StringHelper.concatenateStringList(stringParts, EnumChatFormatting.RESET + Config.MINIMAL_SEPARATOR_STRING));
        		else
        			toolTip.addAll(stringParts);
        	}
		}
        
		return toolTip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return toolTip;
	}

	public static HashMap<String, Boolean> configOptions = new HashMap<String, Boolean>();
	static
	{
		configOptions.put("harvestlevels.harvestlevel", true);
		configOptions.put("harvestlevels.effectivetool", true);
		configOptions.put("harvestlevels.currentlyharvestable", true);
		configOptions.put("harvestlevels.harvestlevel.sneakingonly", false);
		configOptions.put("harvestlevels.effectivetool.sneakingonly", false);
		configOptions.put("harvestlevels.currentlyharvestable.sneakingonly", false);
		configOptions.put("harvestlevels.oresonly", false);
		configOptions.put("harvestlevels.minimal", false);
		configOptions.put("harvestlevels.unharvestableonly", false);
		configOptions.put("harvestlevels.toolrequiredonly", true);
	}

	public static void callbackRegister(IWailaRegistrar registrar)
	{
		WailaHandler instance = new WailaHandler();
		
		for (Map.Entry<String, Boolean> entry : configOptions.entrySet())
		{
			// hacky way to set default values to anything but true
			ConfigHandler.instance().getConfig(entry.getKey(), entry.getValue());
			registrar.addConfig("HarvestLevels", entry.getKey(), "option."+entry.getKey());
		}
		
		registrar.registerBodyProvider(instance, Block.class);
	}
}