package squeek.wailaharvestability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import net.minecraft.item.ItemBlock;
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
		Block block = accessor.getBlock();
		int meta = accessor.getMetadata();

		// for disguised blocks
		if (itemStack.getItem() instanceof ItemBlock)
		{
			block = Block.getBlockFromItem(itemStack.getItem());
			meta = itemStack.getItemDamage();
		}
		
		if (config.getConfig("harvestability.toolrequiredonly") && block.getMaterial().isToolNotRequired())
			return toolTip;

		boolean isSneaking = accessor.getPlayer().isSneaking();
		boolean showHarvestLevel = config.getConfig("harvestability.harvestlevel") && (!config.getConfig("harvestability.harvestlevel.sneakingonly") || isSneaking);
		boolean showEffectiveTool = config.getConfig("harvestability.effectivetool") && (!config.getConfig("harvestability.effectivetool.sneakingonly") || isSneaking);
		boolean showCurrentlyHarvestable = config.getConfig("harvestability.currentlyharvestable") && (!config.getConfig("harvestability.currentlyharvestable.sneakingonly") || isSneaking);
		boolean showOresOnly = config.getConfig("harvestability.oresonly", false);
		boolean minimalLayout = config.getConfig("harvestability.minimal", false);
		boolean hideWhileHarvestable = config.getConfig("harvestability.unharvestableonly", false);

		if (showHarvestLevel || showEffectiveTool || showCurrentlyHarvestable)
		{
			if (showOresOnly && !OreHelper.isItemAnOre(itemStack))
			{
				return toolTip;
			}

			int harvestLevel = block.getHarvestLevel(meta);
			String effectiveTool = block.getHarvestTool(meta);
			boolean blockHasEffectiveTools = harvestLevel >= 0 && effectiveTool != null;

			if (!blockHasEffectiveTools)
				return toolTip;

			boolean canHarvest = false;
			boolean isEffective = false;
			boolean isAboveMinHarvestLevel = false;
			boolean isHoldingTinkersTool = false;

			ItemStack itemHeld = accessor.getPlayer().getHeldItem();
			if (itemHeld != null)
			{
				isHoldingTinkersTool = ToolHelper.hasToolTag(itemHeld);
				canHarvest = ToolHelper.canToolHarvestBlock(itemHeld, block, meta) || (!isHoldingTinkersTool && block.canHarvestBlock(accessor.getPlayer(), meta));
				isAboveMinHarvestLevel = (showCurrentlyHarvestable || showHarvestLevel) && ToolHelper.canToolHarvestLevel(itemHeld, block, meta, harvestLevel);
				isEffective = showEffectiveTool && ToolHelper.isToolEffectiveAgainst(itemHeld, block, meta, effectiveTool);
			}

			boolean isCurrentlyHarvestable = (canHarvest && isAboveMinHarvestLevel) || (!isHoldingTinkersTool && ForgeHooks.canHarvestBlock(block, accessor.getPlayer(), meta));

			if (hideWhileHarvestable && isCurrentlyHarvestable)
				return toolTip;

			List<String> stringParts = new ArrayList<String>();

			if (showCurrentlyHarvestable)
				stringParts.add(ColorHelper.getBooleanColor(isCurrentlyHarvestable) + (isCurrentlyHarvestable ? Config.CURRENTLY_HARVESTABLE_STRING : Config.NOT_CURRENTLY_HARVESTABLE_STRING) + (!minimalLayout ? EnumChatFormatting.RESET + StatCollector.translateToLocal("wailaharvestability.currentlyharvestable") : ""));
			if (harvestLevel != -1 && showEffectiveTool)
				stringParts.add((!minimalLayout ? StatCollector.translateToLocal("wailaharvestability.effectivetool") : "") + ColorHelper.getBooleanColor(isEffective && (!isHoldingTinkersTool || canHarvest), isHoldingTinkersTool && isEffective && !canHarvest) + StatCollector.translateToLocal("wailaharvestability.toolclass." + effectiveTool));
			if (harvestLevel >= 1 && showHarvestLevel)
				stringParts.add((!minimalLayout ? StatCollector.translateToLocal("wailaharvestability.harvestlevel") : "") + ColorHelper.getBooleanColor(isAboveMinHarvestLevel && canHarvest) + StringHelper.stripFormatting(StringHelper.getHarvestLevelName(harvestLevel)));

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
		configOptions.put("harvestability.harvestlevel", true);
		configOptions.put("harvestability.effectivetool", true);
		configOptions.put("harvestability.currentlyharvestable", true);
		configOptions.put("harvestability.harvestlevel.sneakingonly", false);
		configOptions.put("harvestability.effectivetool.sneakingonly", false);
		configOptions.put("harvestability.currentlyharvestable.sneakingonly", false);
		configOptions.put("harvestability.oresonly", false);
		configOptions.put("harvestability.minimal", false);
		configOptions.put("harvestability.unharvestableonly", false);
		configOptions.put("harvestability.toolrequiredonly", true);
	}

	public static void callbackRegister(IWailaRegistrar registrar)
	{
		WailaHandler instance = new WailaHandler();

		for (Map.Entry<String, Boolean> entry : configOptions.entrySet())
		{
			// hacky way to set default values to anything but true
			ConfigHandler.instance().getConfig(entry.getKey(), entry.getValue());
			registrar.addConfig("Harvestability", entry.getKey(), "option." + entry.getKey());
		}

		registrar.registerBodyProvider(instance, Block.class);
	}
}