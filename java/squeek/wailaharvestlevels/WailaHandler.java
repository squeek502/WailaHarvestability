package squeek.wailaharvestlevels;

import java.util.ArrayList;
import java.util.List;
import squeek.wailaharvestlevels.helpers.StringHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
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
		if (config.getConfig("mining.harvestlevel") || config.getConfig("mining.effectivetool"))
		{
			int oreID = -1;
			if (config.getConfig("mining.oresonly") && ((oreID = OreDictionary.getOreID(itemStack)) == -1 || !OreDictionary.getOreName(oreID).startsWith("ore")))
			{
				return toolTip;
			}
			
			EnumChatFormatting effectiveColor = EnumChatFormatting.DARK_RED;
			EnumChatFormatting harvestColor = EnumChatFormatting.DARK_RED;
			ItemStack itemHeld = accessor.getPlayer().getHeldItem();
			List<String> harvestTypes = new ArrayList<String>();
			int tinkersHarvestLevel = -1;
			if (itemHeld != null)
			{
				/*
				if (itemHeld.getItem() instanceof HarvestTool)
				{
					try
					{
						Method getHarvestType = HarvestTool.class.getDeclaredMethod("getHarvestType");
						getHarvestType.setAccessible(true);
						harvestTypes.add((String) getHarvestType.invoke(itemHeld.getItem()));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					if (itemHeld.getItem() instanceof DualHarvestTool)
					{
						try
						{
							Method getSecondHarvestType = DualHarvestTool.class.getDeclaredMethod("getSecondHarvestType");
							getSecondHarvestType.setAccessible(true);
							harvestTypes.add((String) getSecondHarvestType.invoke(itemHeld.getItem()));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					tinkersHarvestLevel = Math.max(ToolHelper.getPrimaryHarvestLevel(ToolHelper.getToolTag(itemHeld)), ToolHelper.getSecondaryHarvestLevel(ToolHelper.getToolTag(itemHeld)));
				}
				else*/ if (ForgeHooks.isToolEffective(itemHeld, accessor.getBlock(), accessor.getMetadata()))
				{
					effectiveColor = EnumChatFormatting.DARK_GREEN;
					
					if (ForgeHooks.canToolHarvestBlock(accessor.getBlock(), accessor.getMetadata(), itemHeld))
					{
						harvestColor = EnumChatFormatting.DARK_GREEN;
					}
				}
			}
			
	        int hlvl = MinecraftForge.getBlockHarvestLevel(accessor.getBlock(), accessor.getMetadata(), "pickaxe");
	        String effectiveTool = "Pickaxe";
	        int hlvlMin = 0;
	        
	       	if (hlvl == -1)
	        {
	        	hlvlMin = 1;
	        	
	        	hlvl = MinecraftForge.getBlockHarvestLevel(accessor.getBlock(), accessor.getMetadata(), "shovel");
	            effectiveTool = "Shovel";
	            
		       	if (hlvl == -1)
	        	{
	        		hlvl = MinecraftForge.getBlockHarvestLevel(accessor.getBlock(), accessor.getMetadata(), "axe");
	        		effectiveTool = "Axe";
	        	}
	        }
	       	
	       	if (harvestTypes.contains(effectiveTool.toLowerCase()))
	       	{
		       	boolean isEffectiveButCantHarvest = itemHeld != null && !itemHeld.canHarvestBlock(accessor.getBlock());
		       	if (isEffectiveButCantHarvest)
		       		effectiveColor = EnumChatFormatting.RED;
		       	else
		       		effectiveColor = EnumChatFormatting.DARK_GREEN;
	       	}
	       	if (tinkersHarvestLevel >= hlvl)
	       		harvestColor = EnumChatFormatting.DARK_GREEN;
	        
        	if (hlvl >= hlvlMin && config.getConfig("mining.harvestlevel"))
        		toolTip.add("Harvest Level : "+harvestColor+StringHelper.getHarvestLevelName(hlvl));
        	if (hlvl != -1 && config.getConfig("mining.effectivetool"))
        		toolTip.add("Effective Tool : "+effectiveColor+effectiveTool);
        	toolTip.addAll(harvestTypes);
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
		registrar.addConfig("Mining", "mining.harvestlevel", "Show harvest level");
		registrar.addConfig("Mining", "mining.effectivetool", "Show effective tool");
		registrar.addConfig("Mining", "mining.oresonly", "Only show on ore blocks");
		registrar.registerBodyProvider(instance, Block.class);
	}
}