package squeek.wailaharvestability;

import mcp.mobius.waila.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.ToolType;
import squeek.wailaharvestability.helpers.*;
import squeek.wailaharvestability.proxy.ProxyCreativeBlocks;
import squeek.wailaharvestability.proxy.ProxyGregTech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WailaPlugin(value = ModInfo.MODID)
public class WailaHandler implements IComponentProvider, IWailaPlugin
{

	@Override
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		Block block = accessor.getBlock();
		BlockState state = accessor.getBlockState();
		ItemStack stack = accessor.getStack();

		if (! ProxyCreativeBlocks.isCreativeBlock(block))
		{
			PlayerEntity player = accessor.getPlayer();

			// for disguised blocks
			if (!ProxyGregTech.isOreBlock(block) && stack.getItem() instanceof BlockItem)
			{
				block = Block.getBlockFromItem(stack.getItem());
			}

			boolean minimalLayout = config.get(new ResourceLocation("harvestability", "minimal"), false);

			List<ITextComponent> stringParts = new ArrayList<>();
			getHarvestability(stringParts, player, block, state, accessor.getPosition(), config, minimalLayout);

			if (!stringParts.isEmpty())
			{
				if (minimalLayout)
					tooltip.add(StringHelper.concatenateStringList(stringParts, TextFormatting.RESET + Config.MAIN.minimalSeparatorString.get()));
				else
					tooltip.addAll(stringParts);
			}
		}
	}

	public void getHarvestability(List<ITextComponent> stringList, PlayerEntity player, Block block, BlockState state, BlockPos pos, IPluginConfig config, boolean minimalLayout)
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
			if (showOresOnly && !OreHelper.isBlockAnOre(block))
			{
				return;
			}

			if (BlockHelper.isAdventureModeAndBlockIsUnbreakable(player, pos) || BlockHelper.isBlockUnbreakable(player.world, pos, state))
			{
				ITextComponent unbreakableString = new StringTextComponent(ColorHelper.getBooleanColor(false)).appendText(Config.MAIN.notCurrentlyHarvestableString.get()).appendSibling(!minimalLayout ? new TranslationTextComponent("wailaharvestability.harvestable").applyTextStyle(TextFormatting.RESET) : new StringTextComponent(""));
				stringList.add(unbreakableString);
				return;
			}

			int harvestLevel = block.getHarvestLevel(state);
			ToolType effectiveTool = BlockHelper.getEffectiveToolOf(player.world, pos, state);
			String effectiveToolName = effectiveTool.getName();
			if (effectiveTool != null && harvestLevel < 0)
				harvestLevel = 0;
			boolean blockHasEffectiveTools = harvestLevel >= 0 && effectiveTool != null;

			String shearability = getShearabilityString(player, block, pos, config);
			String silkTouchability = getSilkTouchabilityString(player, state, pos, config);

			if (toolRequiredOnly && state.getMaterial().isToolNotRequired() && !blockHasEffectiveTools && shearability.isEmpty() && silkTouchability.isEmpty())
				return;

			boolean canHarvest = false;
			boolean isEffective = false;
			boolean isAboveMinHarvestLevel = false;
			boolean isHoldingTinkersTool = false;

			ItemStack itemHeld = player.getHeldItemMainhand();
			if (!itemHeld.isEmpty())
			{
				canHarvest = ToolHelper.canToolHarvestBlock(itemHeld, state) || (!isHoldingTinkersTool && BlockHelper.canHarvestBlock(block, player, state));
				isAboveMinHarvestLevel = (showCurrentlyHarvestable || showHarvestLevel) && ToolHelper.canToolHarvestLevel(itemHeld, player.world, pos, player, harvestLevel);
				isEffective = showEffectiveTool && ToolHelper.isToolEffectiveAgainst(itemHeld, player.world, pos, effectiveTool);
			}

			boolean isCurrentlyHarvestable = (canHarvest && isAboveMinHarvestLevel) || (!isHoldingTinkersTool && BlockHelper.canHarvestBlock(block, player, state));

			if (hideWhileHarvestable && isCurrentlyHarvestable)
				return;

			String currentlyHarvestable = showCurrentlyHarvestable ? ColorHelper.getBooleanColor(isCurrentlyHarvestable) + (isCurrentlyHarvestable ? Config.MAIN.currentlyHarvestableString.get() : Config.MAIN.notCurrentlyHarvestableString.get()) + (!minimalLayout ? TextFormatting.RESET + I18n.format("wailaharvestability.currentlyharvestable") : "") : "";

			if (!currentlyHarvestable.isEmpty() || !shearability.isEmpty() || !silkTouchability.isEmpty())
			{
				String separator = (!shearability.isEmpty() || !silkTouchability.isEmpty() ? " " : "");
				stringList.add(new StringTextComponent(currentlyHarvestable + separator + silkTouchability + (!silkTouchability.isEmpty() ? separator : "") + shearability));
			}
			if (harvestLevel != -1 && showEffectiveTool && effectiveTool != null)
			{
				String effectiveToolString;
				if (I18n.hasKey("wailaharvestability.toolclass." + effectiveTool))
					effectiveToolString = I18n.format("wailaharvestability.toolclass." + effectiveTool);
				else
					effectiveToolString = effectiveToolName.substring(0, 1).toUpperCase() + effectiveToolName.substring(1);
				stringList.add(new TranslationTextComponent(!minimalLayout ? "wailaharvestability.effectivetool" : "").appendText(ColorHelper.getBooleanColor(isEffective && (!isHoldingTinkersTool || canHarvest), isHoldingTinkersTool && isEffective && !canHarvest) + effectiveToolString));
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

				stringList.add(new TranslationTextComponent(!minimalLayout ? "wailaharvestability.harvestlevel" : "").appendText(ColorHelper.getBooleanColor(isAboveMinHarvestLevel && canHarvest) + harvestLevelString));
			}
		}
	}

	public String getShearabilityString(PlayerEntity player, Block block, BlockPos pos, IPluginConfig config)
	{
		boolean isSneaking = player.isSneaking();
		boolean showShearability = config.get(new ResourceLocation("harvestability", "shearability")) && (!config.get(new ResourceLocation("harvestability", "shearability.sneakingonly")) || isSneaking);

		if (showShearability && block instanceof IShearable)
		{
			ItemStack itemHeld = player.getHeldItemMainhand();
			boolean isHoldingShears = !itemHeld.isEmpty() && itemHeld.getItem() instanceof ShearsItem;
			boolean isShearable = isHoldingShears && ((IShearable) block).isShearable(itemHeld, player.world, pos);
			return ColorHelper.getBooleanColor(isShearable, !isShearable && isHoldingShears) + Config.MAIN.shearabilityString.get();
		}
		return "";
	}

	public String getSilkTouchabilityString(PlayerEntity player, BlockState state, BlockPos pos, IPluginConfig config)
	{
		boolean isSneaking = player.isSneaking();
		boolean showSilkTouchability = config.get(new ResourceLocation("harvestability", "silktouchability")) && (!config.get(new ResourceLocation("harvestability", "silktouchability.sneakingonly")) || isSneaking);

		if (showSilkTouchability /*&& block.canSilkHarvest(player.world, pos, state, player)*/) //TODO
		{
			World world = player.world;
			if (world instanceof ServerWorld)
			{
				List<ItemStack> drops = Block.func_220070_a(state, (ServerWorld) world, pos, null);
				Item itemDropped = drops.get(0).getItem(); //TODO Test
				boolean silkTouchMatters = (itemDropped instanceof BlockItem && itemDropped != state.getBlock().asItem()) || drops.size() <= 0; //TODO Test
				if (silkTouchMatters)
				{
					boolean hasSilkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand()) > 0;
					return ColorHelper.getBooleanColor(hasSilkTouch) + Config.MAIN.silkTouchabilityString;
				}
			}
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
		configOptions.put(new ResourceLocation("harvestability", "silktouchability"), true);
		configOptions.put(new ResourceLocation("harvestability", "silktouchability.sneakingonly"), false);
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