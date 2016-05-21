package squeek.wailaharvestability;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import squeek.wailaharvestability.helpers.*;
import squeek.wailaharvestability.proxy.ProxyCreativeBlocks;
import squeek.wailaharvestability.proxy.ProxyGregTech;

import java.util.*;

public class WailaHandler implements IWailaDataProvider
{
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		Block block = accessor.getBlock();
		IBlockState blockState = accessor.getBlockState();

		if (ProxyCreativeBlocks.isCreativeBlock(block, accessor.getMetadata()))
			return toolTip;

		EntityPlayer player = accessor.getPlayer();

		// for disguised blocks
		if (!ProxyGregTech.isOreBlock(block) && itemStack.getItem() instanceof ItemBlock)
		{
			block = Block.getBlockFromItem(itemStack.getItem());
			blockState = block.getStateFromMeta(itemStack.getItemDamage());
		}

		boolean minimalLayout = config.getConfig("harvestability.minimal", false);

		List<String> stringParts = new ArrayList<String>();
		getHarvestability(stringParts, player, block, blockState, accessor.getPosition(), config, minimalLayout);

		if (!stringParts.isEmpty())
		{
			if (minimalLayout)
				toolTip.add(StringHelper.concatenateStringList(stringParts, TextFormatting.RESET + Config.MINIMAL_SEPARATOR_STRING));
			else
				toolTip.addAll(stringParts);
		}

		return toolTip;
	}

	public void getHarvestability(List<String> stringList, EntityPlayer player, Block block, IBlockState blockState, BlockPos position, IWailaConfigHandler config, boolean minimalLayout)
	{
		boolean isSneaking = player.isSneaking();
		boolean showHarvestLevel = config.getConfig("harvestability.harvestlevel") && (!config.getConfig("harvestability.harvestlevel.sneakingonly") || isSneaking);
		boolean showHarvestLevelNum = config.getConfig("harvestability.harvestlevelnum") && (!config.getConfig("harvestability.harvestlevelnum.sneakingonly") || isSneaking);
		boolean showEffectiveTool = config.getConfig("harvestability.effectivetool") && (!config.getConfig("harvestability.effectivetool.sneakingonly") || isSneaking);
		boolean showCurrentlyHarvestable = config.getConfig("harvestability.currentlyharvestable") && (!config.getConfig("harvestability.currentlyharvestable.sneakingonly") || isSneaking);
		boolean hideWhileHarvestable = config.getConfig("harvestability.unharvestableonly", false);
		boolean showOresOnly = config.getConfig("harvestability.oresonly", false);
		boolean toolRequiredOnly = config.getConfig("harvestability.toolrequiredonly");

		if (showHarvestLevel || showEffectiveTool || showCurrentlyHarvestable)
		{
			if (showOresOnly && !OreHelper.isBlockAnOre(block, block.getMetaFromState(blockState)))
			{
				return;
			}

			if (BlockHelper.isAdventureModeAndBlockIsUnbreakable(player, block) || BlockHelper.isBlockUnbreakable(block, player.worldObj, position, blockState))
			{
				String unbreakableString = ColorHelper.getBooleanColor(false) + Config.NOT_CURRENTLY_HARVESTABLE_STRING + (!minimalLayout ? TextFormatting.RESET + I18n.format("wailaharvestability.harvestable") : "");
				stringList.add(unbreakableString);
				return;
			}

			int harvestLevel = block.getHarvestLevel(blockState);
			String effectiveTool = BlockHelper.getEffectiveToolOf(player.worldObj, position, block, blockState);
			if (effectiveTool != null && harvestLevel < 0)
				harvestLevel = 0;
			boolean blockHasEffectiveTools = harvestLevel >= 0 && effectiveTool != null;

			String shearability = getShearabilityString(player, block, blockState, position, config);
			String silkTouchability = getSilkTouchabilityString(player, block, blockState, position, config);

			if (toolRequiredOnly && blockState.getMaterial().isToolNotRequired() && !blockHasEffectiveTools && shearability.isEmpty() && silkTouchability.isEmpty())
				return;

			boolean canHarvest = false;
			boolean isEffective = false;
			boolean isAboveMinHarvestLevel = false;
			boolean isHoldingTinkersTool = false;

			ItemStack itemHeld = player.getHeldItemMainhand();
			if (itemHeld != null)
			{
				canHarvest = ToolHelper.canToolHarvestBlock(itemHeld, block, blockState) || (!isHoldingTinkersTool && BlockHelper.canHarvestBlock(block, player, blockState));
				isAboveMinHarvestLevel = (showCurrentlyHarvestable || showHarvestLevel) && ToolHelper.canToolHarvestLevel(itemHeld, player.worldObj, position, harvestLevel);
				isEffective = showEffectiveTool && ToolHelper.isToolEffectiveAgainst(itemHeld, player.worldObj, position, block, effectiveTool);
			}

			boolean isCurrentlyHarvestable = (canHarvest && isAboveMinHarvestLevel) || (!isHoldingTinkersTool && BlockHelper.canHarvestBlock(block, player, blockState));

			if (hideWhileHarvestable && isCurrentlyHarvestable)
				return;

			String currentlyHarvestable = showCurrentlyHarvestable ? ColorHelper.getBooleanColor(isCurrentlyHarvestable) + (isCurrentlyHarvestable ? Config.CURRENTLY_HARVESTABLE_STRING : Config.NOT_CURRENTLY_HARVESTABLE_STRING) + (!minimalLayout ? TextFormatting.RESET + I18n.format("wailaharvestability.currentlyharvestable") : "") : "";

			if (!currentlyHarvestable.isEmpty() || !shearability.isEmpty() || !silkTouchability.isEmpty())
			{
				String separator = (!shearability.isEmpty() || !silkTouchability.isEmpty() ? " " : "");
				stringList.add(currentlyHarvestable + separator + silkTouchability + (!silkTouchability.isEmpty() ? separator : "") + shearability);
			}
			if (harvestLevel != -1 && showEffectiveTool && effectiveTool != null)
			{
				String effectiveToolString;
				if (I18n.hasKey("wailaharvestability.toolclass." + effectiveTool))
					effectiveToolString = I18n.format("wailaharvestability.toolclass." + effectiveTool);
				else
					effectiveToolString = effectiveTool.substring(0, 1).toUpperCase() + effectiveTool.substring(1);
				stringList.add((!minimalLayout ? I18n.format("wailaharvestability.effectivetool") : "") + ColorHelper.getBooleanColor(isEffective && (!isHoldingTinkersTool || canHarvest), isHoldingTinkersTool && isEffective && !canHarvest) + effectiveToolString);
			}
			if (harvestLevel >= 1 && (showHarvestLevel || showHarvestLevelNum))
			{
				String harvestLevelString = "";
				String harvestLevelName = StringHelper.stripFormatting(StringHelper.getHarvestLevelName(harvestLevel));
				String harvestLevelNum = String.valueOf(harvestLevel);

				// only show harvest level number and name if they are different
				showHarvestLevelNum = showHarvestLevelNum && (!showHarvestLevel || !harvestLevelName.equals(harvestLevelNum));

				if (showHarvestLevel)
					harvestLevelString = harvestLevelName + (showHarvestLevelNum ? String.format(" (%d)", harvestLevel) : "");
				else if (showHarvestLevelNum)
					harvestLevelString = harvestLevelNum;

				stringList.add((!minimalLayout ? I18n.format("wailaharvestability.harvestlevel") : "") + ColorHelper.getBooleanColor(isAboveMinHarvestLevel && canHarvest) + harvestLevelString);
			}
		}
	}

	public String getShearabilityString(EntityPlayer player, Block block, IBlockState blockState, BlockPos position, IWailaConfigHandler config)
	{
		boolean isSneaking = player.isSneaking();
		boolean showShearability = config.getConfig("harvestability.shearability") && (!config.getConfig("harvestability.shearability.sneakingonly") || isSneaking);

		if (showShearability && (block instanceof IShearable || block == Blocks.DEADBUSH || (block == Blocks.DOUBLE_PLANT && block.getItemDropped(blockState, new Random(), 0) == null)))
		{
			ItemStack itemHeld = player.getHeldItemMainhand();
			boolean isHoldingShears = itemHeld != null && itemHeld.getItem() instanceof ItemShears;
			boolean isShearable = isHoldingShears && ((IShearable) block).isShearable(itemHeld, player.worldObj, position);
			return ColorHelper.getBooleanColor(isShearable, !isShearable && isHoldingShears) + Config.SHEARABILITY_STRING;
		}
		return "";
	}

	public String getSilkTouchabilityString(EntityPlayer player, Block block, IBlockState blockState, BlockPos position, IWailaConfigHandler config)
	{
		boolean isSneaking = player.isSneaking();
		boolean showSilkTouchability = config.getConfig("harvestability.silktouchability") && (!config.getConfig("harvestability.silktouchability.sneakingonly") || isSneaking);

		if (showSilkTouchability && block.canSilkHarvest(player.worldObj, position, blockState, player))
		{
			Item itemDropped = block.getItemDropped(blockState, new Random(), 0);
			boolean silkTouchMatters = (itemDropped instanceof ItemBlock && itemDropped != Item.getItemFromBlock(block)) || block.quantityDropped(new Random()) <= 0;
			if (silkTouchMatters)
			{
				boolean hasSilkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand()) > 0;
				return ColorHelper.getBooleanColor(hasSilkTouch) + Config.SILK_TOUCHABILITY_STRING;
			}
		}
		return "";
	}

	public static HashMap<String, Boolean> configOptions = new HashMap<String, Boolean>();
	static
	{
		configOptions.put("harvestability.harvestlevel", true);
		configOptions.put("harvestability.harvestlevelnum", false);
		configOptions.put("harvestability.effectivetool", true);
		configOptions.put("harvestability.currentlyharvestable", true);
		configOptions.put("harvestability.harvestlevel.sneakingonly", false);
		configOptions.put("harvestability.harvestlevelnum.sneakingonly", false);
		configOptions.put("harvestability.effectivetool.sneakingonly", false);
		configOptions.put("harvestability.currentlyharvestable.sneakingonly", false);
		configOptions.put("harvestability.oresonly", false);
		configOptions.put("harvestability.minimal", false);
		configOptions.put("harvestability.unharvestableonly", false);
		configOptions.put("harvestability.toolrequiredonly", true);
		configOptions.put("harvestability.shearability", true);
		configOptions.put("harvestability.shearability.sneakingonly", false);
		configOptions.put("harvestability.silktouchability", true);
		configOptions.put("harvestability.silktouchability.sneakingonly", false);
	}

	public static void callbackRegister(IWailaRegistrar registrar)
	{
		for (Map.Entry<String, Boolean> entry : configOptions.entrySet())
		{
			registrar.addConfig("Harvestability", entry.getKey(), entry.getValue());
		}

		WailaHandler instance = new WailaHandler();
		registrar.registerBodyProvider(instance, Block.class);
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
	{
		return null;
	}
}