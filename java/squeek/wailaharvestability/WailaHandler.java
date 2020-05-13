package squeek.wailaharvestability;

import mcp.mobius.waila.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.ToolType;
import squeek.wailaharvestability.helpers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WailaPlugin(value = ModInfo.MODID)
public class WailaHandler implements IComponentProvider, IWailaPlugin
{

	@Override
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		BlockState state = accessor.getBlockState();
		ItemStack stack = accessor.getStack();
		PlayerEntity player = accessor.getPlayer();

		boolean minimalLayout = config.get(new ResourceLocation("harvestability", "minimal"), false);

		List<ITextComponent> stringParts = new ArrayList<>();
		try {
			getHarvestability(stringParts, player, state, accessor.getPosition(), config, minimalLayout);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!stringParts.isEmpty())
		{
			if (minimalLayout)
				tooltip.add(StringHelper.concatenateStringList(stringParts, TextFormatting.RESET + Config.MAIN.minimalSeparatorString.get()));
			else
				tooltip.addAll(stringParts);
		}
	}

	public void getHarvestability(List<ITextComponent> stringList, PlayerEntity player, BlockState state, BlockPos pos, IPluginConfig config, boolean minimalLayout)
	{
		boolean isSneaking = player.isSneaking();
		boolean showHarvestLevel = config.get(new ResourceLocation("harvestability", "harvestlevel")) && (!config.get(new ResourceLocation("harvestability", "harvestlevel.sneakingonly")) || isSneaking);
		boolean showHarvestLevelNum = config.get(new ResourceLocation("harvestability", "harvestlevelnum")) && (!config.get(new ResourceLocation("harvestability", "harvestlevelnum.sneakingonly")) || isSneaking);
		boolean showEffectiveTool = config.get(new ResourceLocation("harvestability", "effectivetool")) && (!config.get(new ResourceLocation("harvestability", "effectivetool.sneakingonly")) || isSneaking);
		boolean showCurrentlyHarvestable = config.get(new ResourceLocation("harvestability", "currentlyharvestable")) && (!config.get(new ResourceLocation("harvestability", "currentlyharvestable.sneakingonly")) || isSneaking);
		boolean hideWhileHarvestable = config.get(new ResourceLocation("harvestability", "unharvestableonly"), false);
		boolean showOresOnly = config.get(new ResourceLocation("harvestability", "oresonly"), false);
		boolean toolRequiredOnly = config.get(new ResourceLocation("harvestability", "toolrequiredonly"));

		if (showHarvestLevel || showEffectiveTool || showCurrentlyHarvestable)
		{
			if (showOresOnly && !OreHelper.isBlockAnOre(state.getBlock()))
			{
				return;
			}

			if (BlockHelper.isAdventureModeAndBlockIsUnbreakable(player, pos) || BlockHelper.isBlockUnbreakable(player.world, pos, state))
			{
				ITextComponent unbreakableString = new StringTextComponent(ColorHelper.getBooleanColor(false)).appendText(Config.MAIN.notCurrentlyHarvestableString.get()).appendText(" ").appendSibling(!minimalLayout ? new TranslationTextComponent("wailaharvestability.harvestable").applyTextStyle(TextFormatting.RESET) : new StringTextComponent(""));
				stringList.add(unbreakableString);
				return;
			}

			int harvestLevel = state.getHarvestLevel();
			ToolType effectiveTool = BlockHelper.getEffectiveToolOf(player.world, pos, state);
			if (effectiveTool != null && harvestLevel < 0)
				harvestLevel = 0;

			boolean blockHasEffectiveTools = harvestLevel >= 0 && effectiveTool != null;

			String shearability = getShearabilityString(player, state, pos, config);

			if (toolRequiredOnly && state.getMaterial().isToolNotRequired() && !blockHasEffectiveTools && shearability.isEmpty())
				return;

			boolean canHarvest = false;
			boolean isEffective = false;
			boolean isAboveMinHarvestLevel = false;
			boolean isHoldingTinkersTool = false;

			ItemStack heldStack = player.getHeldItemMainhand();
			if (!heldStack.isEmpty())
			{
				canHarvest = ToolHelper.canToolHarvestBlock(heldStack, state) || (!isHoldingTinkersTool && BlockHelper.canHarvestBlock(state, player));
				isAboveMinHarvestLevel = (showCurrentlyHarvestable || showHarvestLevel) && ToolHelper.canToolHarvestLevel(heldStack, player.world, pos, player, harvestLevel);
				isEffective = showEffectiveTool && ToolHelper.isToolEffectiveAgainst(heldStack, player.world, pos, effectiveTool);
			}

			boolean isCurrentlyHarvestable = (canHarvest && isAboveMinHarvestLevel) || (!isHoldingTinkersTool && BlockHelper.canHarvestBlock(state, player));

			if (hideWhileHarvestable && isCurrentlyHarvestable)
				return;

			String currentlyHarvestable = showCurrentlyHarvestable ? ColorHelper.getBooleanColor(isCurrentlyHarvestable) + (isCurrentlyHarvestable ? Config.MAIN.currentlyHarvestableString.get() : Config.MAIN.notCurrentlyHarvestableString.get()) + " " + (!minimalLayout ? TextFormatting.RESET + I18n.format("wailaharvestability.currentlyharvestable") : "") : "";

			if (!currentlyHarvestable.isEmpty() || !shearability.isEmpty())
			{
				String separator = (!shearability.isEmpty() ? " " : "");
				stringList.add(new StringTextComponent(currentlyHarvestable + separator + shearability));
			}
			if (harvestLevel != -1 && showEffectiveTool && effectiveTool != null)
			{
				String effectiveToolString;
				if (I18n.hasKey("wailaharvestability.toolclass." + effectiveTool)) {
					effectiveToolString = I18n.format("wailaharvestability.toolclass." + effectiveTool);
				}
				else
				{
					String effectiveToolName = effectiveTool.getName();
					effectiveToolString = effectiveToolName.substring(0, 1).toUpperCase() + effectiveToolName.substring(1);
				}
				stringList.add(new TranslationTextComponent(!minimalLayout ? "wailaharvestability.effectivetool" : "").appendText(" ").appendText(ColorHelper.getBooleanColor(isEffective && (!isHoldingTinkersTool || canHarvest), isHoldingTinkersTool && isEffective && !canHarvest) + effectiveToolString));
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

				stringList.add(new TranslationTextComponent(!minimalLayout ? "wailaharvestability.harvestlevel" : "").appendText(" ").appendText(ColorHelper.getBooleanColor(isAboveMinHarvestLevel && canHarvest) + harvestLevelString));
			}
		}
	}

	public String getShearabilityString(PlayerEntity player, BlockState state, BlockPos pos, IPluginConfig config)
	{
		boolean isSneaking = player.isSneaking();
		boolean showShearability = config.get(new ResourceLocation("harvestability", "shearability")) && (!config.get(new ResourceLocation("harvestability", "shearability.sneakingonly")) || isSneaking);

		boolean isDoublePlant = state.getBlock() instanceof DoublePlantBlock; //Special case for DoublePlantBlock, as it does not implement IShearable currently
		boolean canBeSheared = state.getBlock() instanceof IShearable || isDoublePlant;
		if (showShearability && canBeSheared)
		{
			ItemStack heldStack = player.getHeldItemMainhand();
			boolean isHoldingShears = !heldStack.isEmpty() && heldStack.getItem() instanceof ShearsItem;
			boolean isShearable = isHoldingShears && (isDoublePlant || ((IShearable) state.getBlock()).isShearable(heldStack, player.world, pos));
			return ColorHelper.getBooleanColor(isShearable, !isShearable && isHoldingShears) + Config.MAIN.shearabilityString.get();
		}
		return "";
	}

	public static HashMap<ResourceLocation, Boolean> configOptions = new HashMap<>();
	static
	{
		configOptions.put(new ResourceLocation("harvestability", "harvestlevel"), true);
		configOptions.put(new ResourceLocation("harvestability", "harvestlevelnum"), false);
		configOptions.put(new ResourceLocation("harvestability", "effectivetool"), true);
		configOptions.put(new ResourceLocation("harvestability", "currentlyharvestable"), true);
		configOptions.put(new ResourceLocation("harvestability", "harvestlevel.sneakingonly"), false);
		configOptions.put(new ResourceLocation("harvestability", "harvestlevelnum.sneakingonly"), false);
		configOptions.put(new ResourceLocation("harvestability", "effectivetool.sneakingonly"), false);
		configOptions.put(new ResourceLocation("harvestability", "currentlyharvestable.sneakingonly"), false);
		configOptions.put(new ResourceLocation("harvestability", "oresonly"), false);
		configOptions.put(new ResourceLocation("harvestability", "minimal"), false);
		configOptions.put(new ResourceLocation("harvestability", "unharvestableonly"), false);
		configOptions.put(new ResourceLocation("harvestability", "toolrequiredonly"), true);
		configOptions.put(new ResourceLocation("harvestability", "shearability"), true);
		configOptions.put(new ResourceLocation("harvestability", "shearability.sneakingonly"), false);
	}

	@Override
	public void register(IRegistrar registrar)
	{
		for (Map.Entry<ResourceLocation, Boolean> entry : configOptions.entrySet())
		{
			registrar.addConfig(entry.getKey(), entry.getValue());
		}

		WailaHandler instance = new WailaHandler();
		registrar.registerComponentProvider(instance, TooltipPosition.BODY, Block.class);
	}
}